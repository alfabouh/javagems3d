package ru.alfabouh.engine.system;

import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.immediate_gui.panels.GamePlayPanel;
import ru.alfabouh.engine.system.controller.ControllerDispatcher;
import ru.alfabouh.engine.system.logger.JGemsLogging;
import ru.alfabouh.engine.system.map.loader.IMapLoader;
import ru.alfabouh.engine.system.proxy.LocalPlayer;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.environment.Environment;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

public class EngineSystem implements IEngine {
    public static final String ENG_FILEPATH = "jgems3d";
    public static final String ENG_NAME = "JavaGems 3D";
    public static final String ENG_VER = "0.15a";

    private final ResourceManager resourceManager;
    private final EngineState engineState;
    private Thread thread;
    private IMapLoader mapLoader;
    private LocalPlayer localPlayer;
    private boolean lockedUnPausing;

    public EngineSystem() {
        this.thread = null;
        this.engineState = new EngineState();
        this.resourceManager = new ResourceManager();
        this.mapLoader = null;
        this.lockedUnPausing = false;
    }

    public EngineState getEngineState() {
        return this.engineState;
    }

    public String currentMapName() {
        return this.getMapLoader().levelInfo().getLevelName();
    }

    public IMapLoader getMapLoader() {
        return this.mapLoader;
    }

    public boolean isLockedUnPausing() {
        return this.lockedUnPausing;
    }

    public void setLockedUnPausing(boolean lockedUnPausing) {
        this.lockedUnPausing = lockedUnPausing;
    }

    public void loadMap(IMapLoader mapLoader) {
        if (this.getMapLoader() != null) {
            JGems.get().getLogManager().warn("Firstly, the current map should be destroyed!");
            return;
        }
        this.mapLoader = mapLoader;
        this.initMap();
    }

    public Thread getThread() {
        return this.thread;
    }

    private void initMap() {
        if (!this.engineState().isEngineIsReady()) {
            JGems.get().getLogManager().warn("Engine thread is not ready to load map!");
            return;
        }
        this.startWorlds();
        JGems.get().getLogManager().log("Loading map " + this.currentMapName());
        World world = JGems.get().getPhysicThreadManager().getPhysicsTimer().getWorld();
        this.localPlayer = new LocalPlayer(world);
        this.getLocalPlayer().addPlayerInWorlds(this.getMapLoader().levelInfo().getPlayerStartPos());

        JGems.get().getLogManager().log("Created player");
        JGems.get().getLogManager().log(this.currentMapName() + ": Adding brushes!");
        this.getMapLoader().addBrushes(world);
        JGems.get().getLogManager().log(this.currentMapName() + ": Adding liquids!");
        this.getMapLoader().addLiquids(world);
        JGems.get().getLogManager().log(this.currentMapName() + ": Adding triggers!");
        this.getMapLoader().addTriggers(world);
        JGems.get().getLogManager().log(this.currentMapName() + ": Adding entities!");
        this.getMapLoader().addEntities(world);
        JGems.get().getLogManager().log(this.currentMapName() + ": Adding sounds!");
        this.getMapLoader().addSounds(world);
        JGems.get().getLogManager().log(this.currentMapName() + ": Reading Nav Mesh!");
        this.getMapLoader().readNavMesh(world);

        JGems.get().getScreen().getControllerDispatcher().attachControllerTo(ControllerDispatcher.mouseKeyboardController, this.getLocalPlayer().getEntityPlayerSP());
        JGems.get().getScreen().getScene().enableAttachedCamera((WorldItem) this.getLocalPlayer().getEntityPlayerSP());
        JGems.get().getScreen().getWindow().setInFocus(true);
        JGems.get().setUIPanel(new GamePlayPanel(null));

        Environment environment = JGems.get().getSceneWorld().getEnvironment();
        Vector4d fog1 = this.mapLoader.levelInfo().getFog();
        if (this.mapLoader.levelInfo().getFog() != null) {
            environment.getWorldFog().setColor(new Vector3d(fog1.x, fog1.y, fog1.z));
            environment.getWorldFog().setDensity((float) fog1.w);
            environment.getSky().setCoveredByFog(this.mapLoader.levelInfo().isSkyCoveredByFog());
        }
        environment.getSky().setSunColors(this.mapLoader.levelInfo().getSunColor());
        environment.getSky().setSunBrightness(this.mapLoader.levelInfo().getSunBrightness());

        this.unPauseGame();
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    public void destroyMap() {
        this.pauseGame();
        this.clean();
        this.mapLoader = null;
    }

    public void pauseGame() {
        this.engineState().paused = true;
    }

    public void unPauseGame() {
        if (!this.isLockedUnPausing()) {
            this.engineState().paused = false;
        }
    }

    public void clean() {
        if (this.mapLoader == null) {
            return;
        }
        if (!this.engineState().isEngineIsReady()) {
            JGems.get().getLogManager().warn("Engine thread is not ready to be cleaned!");
            return;
        }
        JGems.get().getSoundManager().stopAllSounds();
        JGems.get().getLogManager().log("Cleaning worlds!");
        this.endWorlds();
        this.localPlayer = null;
    }

    private void startWorlds() {
        JGems.get().getPhysicThreadManager().getPhysicsTimer().getWorld().onWorldStart();
        JGems.get().getScreen().getScene().getSceneWorld().onWorldStart();
    }

    private void endWorlds() {
        JGems.get().getPhysicThreadManager().getPhysicsTimer().getWorld().onWorldEnd();
        JGems.get().getScreen().getScene().getSceneWorld().onWorldEnd();
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    @SuppressWarnings("all")
    public void startSystem() {
        this.printSystemInfo();
        if (this.engineState().isEngineIsReady()) {
            JGems.get().getLogManager().warn("Engine thread is currently running!");
            return;
        }
        JGems.get().getLocalisation().setCurrentLang(JGems.get().getGameSettings().language.getCurrentLanguage());
        this.getResourceManager().init();
        this.thread = new Thread(() -> {
            boolean badExit = true;
            try {
                JGems.get().getSoundManager().createSystem();
                JGems.get().getPhysicThreadManager().initService();
                this.getEngineState().gameResourcesLoaded = true;
                this.createGraphics();
                this.engineState().engineIsReady = true;
                JGems.get().getScreen().startScreenRenderProcess();
                badExit = false;
            } catch (Exception e) {
                JGems.get().getLogManager().exception(e);
                badExit = true;
            } finally {
                try {
                    this.destroyMap();
                    JGems.get().destroyGame();
                    JGems.get().getSoundManager().stopAllSounds();
                    JGems.get().getResourceManager().destroy();
                    JGems.get().getSoundManager().destroy();
                    JGems.get().getPhysicThreadManager().getPhysicsTimer().cleanResources();
                    JGems.get().getLogManager().log("Engine-Off");
                } catch (Exception e) {
                    JGems.get().getLogManager().exception(e);
                    badExit = true;
                } finally {
                    if (badExit) {
                        JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder to find out the details.");
                    }
                }
            }
        });
        this.thread.setName("system");
        this.thread.start();
    }

    private void printSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Properties properties = System.getProperties();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();

        JGems.get().getLogManager().log("");
        JGems.get().getLogManager().log("==========================================================");
        JGems.get().getLogManager().log("****DATA***");
        JGems.get().getLogManager().log("==========================================================");

        JGems.get().getLogManager().log("SYSTEM INFO");
        JGems.get().getLogManager().log("System: " + osBean.getName());
        JGems.get().getLogManager().log("System architecture: " + osBean.getArch());
        JGems.get().getLogManager().log("System version: " + osBean.getVersion());

        JGems.get().getLogManager().log("");
        JGems.get().getLogManager().log("JAVA INFO");
        JGems.get().getLogManager().log("Java version: " + runtimeBean.getSpecVersion());
        JGems.get().getLogManager().log("Java vendor: " + runtimeBean.getSpecVendor());
        JGems.get().getLogManager().log("Java VM: " + runtimeBean.getVmVersion());
        JGems.get().getLogManager().log("Java VM version: " + runtimeBean.getVmVersion());

        JGems.get().getLogManager().log("");
        JGems.get().getLogManager().log("USER INFO");
        JGems.get().getLogManager().log("User name: " + properties.getProperty("user.name"));
        JGems.get().getLogManager().log("User home: " + properties.getProperty("user.home"));
        JGems.get().getLogManager().log("User dir: " + properties.getProperty("user.dir"));

        JGems.get().getLogManager().log("");
        JGems.get().getLogManager().log("HARDWARE INFO");
        JGems.get().getLogManager().log("Available processors: " + availableProcessors);
        JGems.get().getLogManager().log("Free memory: " + freeMemory / 1024 / 1024 + " MB");
        JGems.get().getLogManager().log("Total memory: " + totalMemory / 1024 / 1024 + " MB");
        JGems.get().getLogManager().log("Max memory: " + (maxMemory == Long.MAX_VALUE ? "UNLIMITED" : maxMemory / 1024 / 1024 + " MB"));

        JGems.get().getLogManager().log("==========================================================");
        JGems.get().getLogManager().log("****DATA***");
        JGems.get().getLogManager().log("==========================================================");
        JGems.get().getLogManager().log("");
    }

    private void printGraphicsInfo() {
        JGems.get().getLogManager().log("");
        JGems.get().getLogManager().log("==========================================================");
        JGems.get().getLogManager().log("***RENDER INFO***");
        JGems.get().getLogManager().log("==========================================================");
        JGems.get().getLogManager().log("Renderer: " + GL30.glGetString(GL30.GL_RENDERER));
        JGems.get().getLogManager().log("OpenGL Version: " + GL30.glGetString(GL30.GL_VERSION));
        JGems.get().getLogManager().log("Vendor: " + GL30.glGetString(GL30.GL_VENDOR));
        JGems.get().getLogManager().log("==========================================================");
        JGems.get().getLogManager().log("***RENDER INFO***");
        JGems.get().getLogManager().log("==========================================================");
        JGems.get().getLogManager().log("");
    }

    @Override
    public EngineState engineState() {
        return this.engineState;
    }

    private void createGraphics() {
        JGems.get().getScreen().buildScreen();
        this.printGraphicsInfo();
        JGems.get().getResourceManager().loadAllAssets();
    }

    public static class EngineState {
        private boolean gameResourcesLoaded;
        private boolean engineIsReady;
        private boolean paused;

        public EngineState() {
            this.gameResourcesLoaded = false;
            this.paused = true;
            this.engineIsReady = false;
        }

        public boolean isEngineIsReady() {
            return this.engineIsReady;
        }

        public boolean isGameResourcesLoaded() {
            return this.gameResourcesLoaded;
        }

        public boolean isPaused() {
            return this.paused;
        }
    }
}
