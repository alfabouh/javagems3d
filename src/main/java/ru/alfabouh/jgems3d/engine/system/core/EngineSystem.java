package ru.alfabouh.jgems3d.engine.system.core;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.math.Pair;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.sky.skybox.SkyBox2D;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.panels.GamePlayPanel;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.programs.textures.CubeMapProgram;
import ru.alfabouh.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.alfabouh.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.alfabouh.jgems3d.engine.system.map.loaders.tbox.MapLoaderTBox;
import ru.alfabouh.jgems3d.engine.system.proxy.LocalPlayer;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.TextureAssetsLoader;
import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.logger.managers.JGemsLogging;
import ru.alfabouh.jgems3d.map_sys.save.objects.map_prop.FogProp;
import ru.alfabouh.jgems3d.map_sys.save.objects.map_prop.SkyProp;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

public class EngineSystem implements IEngine {
    public static final String ENG_FILEPATH = "jgems3d";
    public static final String ENG_NAME = "JavaGems 3D";
    public static final String ENG_VER = "0.20a";

    private final JGemsResourceManager resourceManager;
    private final EngineState engineState;
    private Thread thread;
    private IMapLoader mapLoader;
    private LocalPlayer localPlayer;
    private boolean lockedUnPausing;

    public EngineSystem() {
        this.thread = null;
        this.engineState = new EngineState();
        this.resourceManager = new JGemsResourceManager();
        this.mapLoader = null;
        this.lockedUnPausing = false;
    }

    public EngineState getEngineState() {
        return this.engineState;
    }

    public String currentMapName() {
        return this.getMapLoader().getLevelInfo().getMapProperties().getMapName();
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

    public void loadMap(String mapName) {
        this.loadMap(new MapLoaderTBox(MapLoaderTBox.readMapFromJar("default_map")));
    }

    public void loadMap(IMapLoader mapLoader) {
        if (this.getMapLoader() != null) {
            SystemLogging.get().getLogManager().warn("Firstly, the current map should be destroyed!");
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
            SystemLogging.get().getLogManager().warn("Engine thread is not ready to load map!");
            this.mapLoader = null;
            return;
        }
        JGems.get().getScreen().showGameLoadingScreen("Loading Map...");
        this.startWorlds();
        SystemLogging.get().getLogManager().log("Loading map " + this.currentMapName());
        World world = JGems.get().getPhysicThreadManager().getPhysicsTimer().getWorld();
        this.getMapLoader().createMap(world);
        Pair<Vector3f, Double> pair = this.getMapLoader().getLevelInfo().chooseRandomSpawnPoint();

        Vector3f startPos = new Vector3f(pair.getFirst()).add(0.0f, 0.6f, 0.0f);
        Vector3f startRot = new Vector3f(0.0f, (float) (pair.getSecond() + (Math.PI / 2.0f)), 0.0f);

        this.localPlayer = new LocalPlayer(world, startPos, startRot);
        this.getLocalPlayer().addPlayerInWorlds();
        SystemLogging.get().getLogManager().log(this.currentMapName() + ": Map Loaded!");

        Environment environment = JGems.get().getSceneWorld().getEnvironment();
        FogProp fogProp = this.getMapLoader().getLevelInfo().getMapProperties().getFogProp();
        SkyProp skyProp = this.getMapLoader().getLevelInfo().getMapProperties().getSkyProp();

        if (fogProp != null) {
            if (fogProp.isFogEnabled()) {
                environment.getWorldFog().setColor(fogProp.getFogColor());
                environment.getWorldFog().setDensity(fogProp.getFogDensity());
                environment.getSky().setCoveredByFog(fogProp.isSkyCoveredByFog());
            } else {
                environment.getFog().disable();
            }
        }

        if (skyProp != null) {
            if (environment.getSky().getSkyBox() instanceof SkyBox2D) {
                SkyBox2D skyBox2D = (SkyBox2D) environment.getSky().getSkyBox();
                CubeMapProgram cubeMapProgram = TextureAssetsLoader.skyBoxMap.get(skyProp.getSkyBoxName());
                if (cubeMapProgram != null) {
                    skyBox2D.setCubeMapTexture(cubeMapProgram);
                }
            }
            environment.getSky().setSunPos(skyProp.getSunPos());
            environment.getSky().setSunColors(skyProp.getSunColor());
            environment.getSky().setSunBrightness(skyProp.getSunBrightness());
        }

        IController controller = JGems.get().getScreen().getControllerDispatcher().getCurrentController();
        if (controller instanceof MouseKeyboardController) {
            MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
            mouseKeyboardController.setCursorInCenter();
        }
        JGems.get().getScreen().getControllerDispatcher().attachControllerTo(JGemsControllerDispatcher.mouseKeyboardController, this.getLocalPlayer().getEntityPlayerSP());
        JGems.get().getScreen().getScene().enableAttachedCamera((WorldItem) this.getLocalPlayer().getEntityPlayerSP());
        JGems.get().getScreen().getWindow().setInFocus(true);
        mapLoader.postLoad(world);
        JGems.get().setUIPanel(new GamePlayPanel(null));
        JGems.get().getScreen().removeLoadingScreen();

        this.unPauseGame();
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    public void destroyMap() {
        this.pauseGame();
        JGems.get().getScreen().showGameLoadingScreen("Exit world...");
        this.clean();
        JGems.get().getScreen().removeLoadingScreen();
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
            SystemLogging.get().getLogManager().warn("Engine thread is not ready to be cleaned!");
            return;
        }
        JGems.get().getSoundManager().stopAllSounds();
        SystemLogging.get().getLogManager().log("Cleaning worlds!");
        this.endWorlds();
        this.getResourceManager().getLocalResources().cleanCache();
        System.gc();
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

    public JGemsResourceManager getResourceManager() {
        return this.resourceManager;
    }

    @SuppressWarnings("all")
    public void startSystem() {
        this.printSystemInfo();
        if (this.engineState().isEngineIsReady()) {
            SystemLogging.get().getLogManager().warn("Engine thread is currently running!");
            return;
        }
        JGems.get().getLocalisation().setCurrentLang(JGems.get().getGameSettings().language.getCurrentLanguage());
        this.getResourceManager().initGlobalResources();
        this.getResourceManager().initLocalResources();
        this.thread = new Thread(() -> {
            boolean badExit = true;
            try {
                JGems.get().getSoundManager().createSystem();
                JGems.get().getPhysicThreadManager().initService();
                this.createGraphics();
                this.getEngineState().gameResourcesLoaded = true;
                this.engineState().engineIsReady = true;
                JGems.get().getScreen().startScreenRenderProcess();
                badExit = false;
            } catch (Exception e) {
                SystemLogging.get().getLogManager().exception(e);
                badExit = true;
            } finally {
                try {
                    this.clean();
                    JGems.get().destroyGame();
                    JGems.get().getSoundManager().stopAllSounds();
                    JGems.get().getResourceManager().destroy();
                    JGems.get().getSoundManager().destroy();
                    if (!JGems.get().getPhysicThreadManager().waitForFullTermination()) {
                        SystemLogging.get().getLogManager().warn("Waited for physics termination too long...");
                    }
                    if (JGems.get().getPhysicThreadManager().badExit) {
                        badExit = true;
                    }
                    JGems.get().getPhysicThreadManager().getPhysicsTimer().cleanResources();
                    SystemLogging.get().getLogManager().log("Engine-Off");
                } catch (Exception e) {
                    SystemLogging.get().getLogManager().exception(e);
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

        SystemLogging.get().getLogManager().log("");
        SystemLogging.get().getLogManager().log("==========================================================");
        SystemLogging.get().getLogManager().log("****DATA***");
        SystemLogging.get().getLogManager().log("==========================================================");

        SystemLogging.get().getLogManager().log("SYSTEM INFO");
        SystemLogging.get().getLogManager().log("System: " + osBean.getName());
        SystemLogging.get().getLogManager().log("System architecture: " + osBean.getArch());
        SystemLogging.get().getLogManager().log("System version: " + osBean.getVersion());

        SystemLogging.get().getLogManager().log("");
        SystemLogging.get().getLogManager().log("JAVA INFO");
        SystemLogging.get().getLogManager().log("Java version: " + runtimeBean.getSpecVersion());
        SystemLogging.get().getLogManager().log("Java vendor: " + runtimeBean.getSpecVendor());
        SystemLogging.get().getLogManager().log("Java VM: " + runtimeBean.getVmVersion());
        SystemLogging.get().getLogManager().log("Java VM version: " + runtimeBean.getVmVersion());

        SystemLogging.get().getLogManager().log("");
        SystemLogging.get().getLogManager().log("USER INFO");
        SystemLogging.get().getLogManager().log("User name: " + properties.getProperty("user.name"));
        SystemLogging.get().getLogManager().log("User home: " + properties.getProperty("user.home"));
        SystemLogging.get().getLogManager().log("User dir: " + properties.getProperty("user.dir"));

        SystemLogging.get().getLogManager().log("");
        SystemLogging.get().getLogManager().log("HARDWARE INFO");
        SystemLogging.get().getLogManager().log("Available processors: " + availableProcessors);
        SystemLogging.get().getLogManager().log("Free memory: " + freeMemory / 1024 / 1024 + " MB");
        SystemLogging.get().getLogManager().log("Total memory: " + totalMemory / 1024 / 1024 + " MB");
        SystemLogging.get().getLogManager().log("Max memory: " + (maxMemory == Long.MAX_VALUE ? "UNLIMITED" : maxMemory / 1024 / 1024 + " MB"));

        SystemLogging.get().getLogManager().log("==========================================================");
        SystemLogging.get().getLogManager().log("****DATA***");
        SystemLogging.get().getLogManager().log("==========================================================");
        SystemLogging.get().getLogManager().log("");
    }

    private void printGraphicsInfo() {
        SystemLogging.get().getLogManager().log("");
        SystemLogging.get().getLogManager().log("==========================================================");
        SystemLogging.get().getLogManager().log("***RENDER INFO***");
        SystemLogging.get().getLogManager().log("==========================================================");
        SystemLogging.get().getLogManager().log("Renderer: " + GL30.glGetString(GL30.GL_RENDERER));
        SystemLogging.get().getLogManager().log("OpenGL Version: " + GL30.glGetString(GL30.GL_VERSION));
        SystemLogging.get().getLogManager().log("Vendor: " + GL30.glGetString(GL30.GL_VENDOR));
        SystemLogging.get().getLogManager().log("==========================================================");
        SystemLogging.get().getLogManager().log("***RENDER INFO***");
        SystemLogging.get().getLogManager().log("==========================================================");
        SystemLogging.get().getLogManager().log("");
    }

    @Override
    public EngineState engineState() {
        return this.engineState;
    }

    private void createGraphics() {
        JGems.get().getScreen().buildScreen();
        this.printGraphicsInfo();
        JGems.get().getResourceManager().loadGlobalResources();
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

        public boolean gameResourcesLoaded() {
            return this.gameResourcesLoaded;
        }

        public boolean isPaused() {
            return this.paused;
        }
    }
}
