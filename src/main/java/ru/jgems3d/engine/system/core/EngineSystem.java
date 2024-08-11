package ru.jgems3d.engine.system.core;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.system.service.misc.Pair;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.jgems3d.engine.graphics.opengl.environment.sky.skybox.SkyBox2D;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultGamePanel;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.CubeMapProgram;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.jgems3d.engine.system.core.player.LocalPlayer;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.logger.managers.JGemsLogging;
import ru.jgems3d.toolbox.map_sys.save.objects.map_prop.FogProp;
import ru.jgems3d.toolbox.map_sys.save.objects.map_prop.SkyProp;

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
    private final RequestsFromThreads requestsFromThreads;

    public EngineSystem() {
        this.thread = null;
        this.engineState = new EngineState();
        this.resourceManager = new JGemsResourceManager();
        this.mapLoader = null;
        this.lockedUnPausing = false;

        this.requestsFromThreads = new RequestsFromThreads();
    }

    public void setLockedUnPausing(boolean lockedUnPausing) {
        this.lockedUnPausing = lockedUnPausing;
    }

    public void update() {
        this.requestsFromThreads.update();
    }

    @SuppressWarnings("all")
    private boolean isCurrentThreadOGL() {
        return GLFW.glfwGetCurrentContext() != 0L;
    }

    public void loadMap(IMapLoader mapLoader) {
        if (!this.isCurrentThreadOGL()) {
            this.requestsFromThreads.loadMap = mapLoader;
            return;
        }
        if (this.getMapLoader() != null) {
            JGemsHelper.getLogger().warn("Firstly, the current map should be destroyed!");
            return;
        }
        this.mapLoader = mapLoader;
        this.initMap();
    }

    public void destroyMap() {
        if (!this.isCurrentThreadOGL()) {
            this.requestsFromThreads.destroyMap = true;
            return;
        }
        this.pauseGame();
        JGems3D.get().getScreen().showGameLoadingScreen("Exit world...");
        this.clean();
        JGems3D.get().getScreen().getScene().setRenderCamera(null);
        JGems3D.get().getScreen().getWindow().setInFocus(false);
        JGems3D.get().getScreen().removeLoadingScreen();
        this.mapLoader = null;
        JGems3D.get().showMainMenu();
    }

    private void initMap() {
        if (!this.engineState().isEngineIsReady()) {
            JGemsHelper.getLogger().warn("Engine thread is not ready to load map!");
            this.mapLoader = null;
            return;
        }
        JGems3D.get().getScreen().showGameLoadingScreen("Loading Map...");
        this.startWorlds();
        JGemsHelper.getLogger().log("Loading map " + this.currentMapName());
        PhysicsWorld physicsWorld = JGemsHelper.getPhysicsWorld();
        SceneWorld sceneWorld = JGemsHelper.getSceneWorld();
        this.getMapLoader().createMap(this.getResourceManager().getLocalResources(), physicsWorld, sceneWorld);
        Pair<Vector3f, Double> pair = this.getMapLoader().getLevelInfo().chooseRandomSpawnPoint();

        Vector3f startPos = new Vector3f(pair.getFirst()).add(0.0f, 0.6f, 0.0f);
        Vector3f startRot = new Vector3f(0.0f, (float) (pair.getSecond() + (Math.PI / 2.0f)), 0.0f);

        this.localPlayer = new LocalPlayer(APIContainer.get().getApiGameInfo().getAppManager().createPlayer(this.getMapLoader()));

        this.getLocalPlayer().addPlayerInWorlds(physicsWorld, startPos, startRot);

        JGemsHelper.getLogger().log(this.currentMapName() + ": Map Loaded!");

        Environment environment = sceneWorld.getEnvironment();
        FogProp fogProp = this.getMapLoader().getLevelInfo().getMapProperties().getFogProp();
        SkyProp skyProp = this.getMapLoader().getLevelInfo().getMapProperties().getSkyProp();

        if (fogProp != null) {
            if (fogProp.isFogEnabled()) {
                environment.getFog().setColor(fogProp.getFogColor());
                environment.getFog().setDensity(fogProp.getFogDensity());
                environment.getSky().setCoveredByFog(fogProp.isSkyCoveredByFog());
            } else {
                environment.getFog().disable();
            }
        }

        if (skyProp != null) {
            if (environment.getSky().getSkyBox() instanceof SkyBox2D) {
                SkyBox2D skyBox2D = (SkyBox2D) environment.getSky().getSkyBox();
                CubeMapProgram cubeMapProgram = JGemsResourceManager.skyBoxTexturesMap.get(skyProp.getSkyBoxName());
                if (cubeMapProgram != null) {
                    skyBox2D.setCubeMapTexture(cubeMapProgram);
                }
            }
            environment.getSky().setSunPos(skyProp.getSunPos());
            environment.getSky().setSunColors(skyProp.getSunColor());
            environment.getSky().setSunBrightness(skyProp.getSunBrightness());
        }

        JGemsHelper.CONTROLLER.setCursorInCenter();
        JGemsHelper.CONTROLLER.attachControllerTo(JGemsControllerDispatcher.mouseKeyboardController, this.getLocalPlayer().getEntityPlayer());
        JGemsHelper.CAMERA.enableAttachedCamera(this.getLocalPlayer().getEntityPlayer());

        this.getMapLoader().postLoad(physicsWorld, sceneWorld);

        JGemsHelper.WINDOW.setWindowFocus(true);
        JGemsHelper.UI.openUIPanel(new DefaultGamePanel(null));
        JGemsHelper.getScreen().removeLoadingScreen();

        this.unPauseGame();
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
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
            JGemsHelper.getLogger().warn("Engine thread is not ready to be cleaned!");
            return;
        }
        JGems3D.get().getSoundManager().stopAllSounds();
        JGemsHelper.getLogger().log("Cleaning worlds!");
        this.endWorlds();
        this.getResourceManager().getLocalResources().cleanCache();
        System.gc();
        this.localPlayer = null;
    }

    private void startWorlds() {
        JGems3D.get().getPhysicThreadManager().getPhysicsTimer().getWorld().onWorldStart();
        JGems3D.get().getScreen().getScene().getSceneWorld().onWorldStart();
    }

    private void endWorlds() {
        JGems3D.get().getPhysicThreadManager().getPhysicsTimer().getWorld().onWorldEnd();
        JGems3D.get().getScreen().getScene().getSceneWorld().onWorldEnd();
    }

    public JGemsResourceManager getResourceManager() {
        return this.resourceManager;
    }

    @SuppressWarnings("all")
    public void startSystem() {
        this.printSystemInfo();
        if (this.engineState().isEngineIsReady()) {
            JGemsHelper.getLogger().warn("Engine thread is currently running!");
            return;
        }
        APIContainer.get().getApiGameInfo().getAppInstance().preInitEvent();
        JGems3D.get().getLocalisation().setLanguage(JGems3D.get().getGameSettings().language.getCurrentLanguage());
        this.getResourceManager().initGlobalResources();
        this.getResourceManager().initLocalResources();
        this.thread = new Thread(() -> {
            boolean badExit = true;
            try {
                JGems3D.get().getSoundManager().createSystem();
                JGems3D.get().getPhysicThreadManager().initService();
                this.createGraphics();
                this.engineState().gameResourcesLoaded = true;
                this.engineState().engineIsReady = true;
                APIContainer.get().getApiGameInfo().getAppInstance().postInitEvent();
                JGems3D.get().getScreen().startScreenRenderProcess();
                badExit = false;
            } catch (Exception e) {
                JGemsHelper.getLogger().exception(e);
                badExit = true;
            } finally {
                try {
                    this.clean();
                    JGems3D.get().destroyGame();
                    JGems3D.get().getSoundManager().stopAllSounds();
                    JGems3D.get().getResourceManager().destroy();
                    JGems3D.get().getSoundManager().destroy();
                    if (!JGems3D.get().getPhysicThreadManager().waitForFullTermination()) {
                        JGemsHelper.getLogger().error("Waited for physics termination too long...");
                    }
                    if (JGems3D.get().getPhysicThreadManager().badExit) {
                        badExit = true;
                    }
                    JGems3D.get().getPhysicThreadManager().getPhysicsTimer().cleanResources();
                    JGemsHelper.getLogger().log("Engine-Off");
                } catch (Exception e) {
                    JGemsHelper.getLogger().exception(e);
                    badExit = true;
                } finally {
                    if (badExit) {
                        JGemsLogging.showExceptionDialog("An service occurred inside the system. Open the logs folder to find out the details.");
                    }
                }
            }
        });
        this.thread.setName("system");
        this.thread.start();
    }

    public String currentMapName() {
        return this.getMapLoader().getLevelInfo().getMapProperties().getMapName();
    }

    public Thread getThread() {
        return this.thread;
    }

    public IMapLoader getMapLoader() {
        return this.mapLoader;
    }

    @SuppressWarnings("all")
    public boolean isLockedUnPausing() {
        return this.lockedUnPausing;
    }

    private void printSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Properties properties = System.getProperties();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();

        JGemsHelper.getLogger().log("");
        JGemsHelper.getLogger().log("==========================================================");
        JGemsHelper.getLogger().log("****DATA***");
        JGemsHelper.getLogger().log("==========================================================");

        JGemsHelper.getLogger().log("SYSTEM INFO");
        JGemsHelper.getLogger().log("System: " + osBean.getName());
        JGemsHelper.getLogger().log("System architecture: " + osBean.getArch());
        JGemsHelper.getLogger().log("System version: " + osBean.getVersion());

        JGemsHelper.getLogger().log("");
        JGemsHelper.getLogger().log("JAVA INFO");
        JGemsHelper.getLogger().log("Java version: " + runtimeBean.getSpecVersion());
        JGemsHelper.getLogger().log("Java vendor: " + runtimeBean.getSpecVendor());
        JGemsHelper.getLogger().log("Java VM: " + runtimeBean.getVmVersion());
        JGemsHelper.getLogger().log("Java VM version: " + runtimeBean.getVmVersion());

        JGemsHelper.getLogger().log("");
        JGemsHelper.getLogger().log("USER INFO");
        JGemsHelper.getLogger().log("User name: " + properties.getProperty("user.name"));
        JGemsHelper.getLogger().log("User home: " + properties.getProperty("user.home"));
        JGemsHelper.getLogger().log("User dir: " + properties.getProperty("user.dir"));

        JGemsHelper.getLogger().log("");
        JGemsHelper.getLogger().log("HARDWARE INFO");
        JGemsHelper.getLogger().log("Available processors: " + availableProcessors);
        JGemsHelper.getLogger().log("Free memory: " + freeMemory / 1024 / 1024 + " MB");
        JGemsHelper.getLogger().log("Total memory: " + totalMemory / 1024 / 1024 + " MB");
        JGemsHelper.getLogger().log("Max memory: " + (maxMemory == Long.MAX_VALUE ? "UNLIMITED" : maxMemory / 1024 / 1024 + " MB"));

        JGemsHelper.getLogger().log("==========================================================");
        JGemsHelper.getLogger().log("****DATA***");
        JGemsHelper.getLogger().log("==========================================================");
        JGemsHelper.getLogger().log("");
    }

    private void printGraphicsInfo() {
        JGemsHelper.getLogger().log("");
        JGemsHelper.getLogger().log("==========================================================");
        JGemsHelper.getLogger().log("***RENDER INFO***");
        JGemsHelper.getLogger().log("==========================================================");
        JGemsHelper.getLogger().log("Renderer: " + GL30.glGetString(GL30.GL_RENDERER));
        JGemsHelper.getLogger().log("OpenGL Version: " + GL30.glGetString(GL30.GL_VERSION));
        JGemsHelper.getLogger().log("Vendor: " + GL30.glGetString(GL30.GL_VENDOR));
        JGemsHelper.getLogger().log("==========================================================");
        JGemsHelper.getLogger().log("***RENDER INFO***");
        JGemsHelper.getLogger().log("==========================================================");
        JGemsHelper.getLogger().log("");
    }

    @Override
    public EngineState engineState() {
        return this.engineState;
    }

    private void createGraphics() {
        JGems3D.get().getScreen().buildScreen();
        this.printGraphicsInfo();
        JGems3D.get().getResourceManager().loadGlobalResources();
    }

    private class RequestsFromThreads {
        public boolean destroyMap;
        public IMapLoader loadMap;

        public void update() {
            if (this.destroyMap) {
                EngineSystem.this.destroyMap();
                return;
            }
            if (this.loadMap != null) {
                EngineSystem.this.loadMap(this.loadMap);
            }
        }
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
