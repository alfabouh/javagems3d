package javagems3d.system.core;

import api.system.JGemsAPI;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.help.JGemsHelper;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.JGemsMapping;
import javagems3d.mapping.processing.base.IMapProcessor;
import javagems3d.mapping.processing.callbacks.IMapActionCallback;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsStringSource;
import javagems3d.system.service.synchronizing.SyncManager;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.stat.PerformanceStat;
import logger.managers.JGemsLogging;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.*;

public final class JGemsCore implements ICore {
    public static final String ENG_FILEPATH = "jgems3d";
    public static final String ENG_NAME = "JavaGems 3D";
    public static final String ENG_VER = "0.30a-dev";

    private final JGemsSoundManager jGemsSoundManager;
    private final JGemsScreen jGemsScreen;
    private final JGemsPhysics jGemsPhysics;
    private final JGemsResourceManager resourceManager;

    private final EngineState engineState;
    private final RequestsFromThreads requestsFromThreads;
    private Thread systemThread;
    private JGemsMapping mapping;

    private final Set<Exception> exceptionsBuffer;

    public JGemsCore() {
        this.jGemsPhysics = new JGemsPhysics(JGemsPhysics.TICKS_PER_SECOND);
        this.jGemsSoundManager = new JGemsSoundManager();
        this.jGemsScreen = new JGemsScreen();
        this.resourceManager = new JGemsResourceManager();

        this.engineState = new EngineState();
        this.systemThread = null;

        this.requestsFromThreads = new RequestsFromThreads();
        this.exceptionsBuffer = SyncManager.createSyncronisedSet();

        JGemsHelper.initJGemsCore(this);
    }

    private void createMappingObject() {
        this.mapping = new JGemsMapping((SceneWorld) this.getScreen().getSceneWorld(), this.getPhysics().getPhysicsWorld(), this.getResourceManager());
    }

    public void update() {
        this.requestsFromThreads.update();
    }

    @SuppressWarnings("all")
    private boolean isCurrentThreadOGL() {
        return GLFW.glfwGetCurrentContext() != 0L;
    }

    public void exitMap() {
        if (!this.isCurrentThreadOGL()) {
            this.requestsFromThreads.destroyMap = true;
            return;
        }
        if (!this.getMapping().isMapValid()) {
            return;
        }

        this.pauseGame();
        this.getScreen().showGameLoadingScreen("Exiting world...");

        this.getMapping().destroyMap((IMapActionCallback) this.getScreen().getScene().getSceneRenderer());

        this.getSoundManager().stopAllSounds();
        this.getResourceManager().destroyResourcesDataCache();
        this.getResourceManager().getLocalResources().destroy();
        System.gc();

        this.getScreen().getScene().setCamera(null);
        this.getScreen().getWindow().setFocus(false);

        this.getScreen().removeLoadingScreen();
        JGemsHelper.ui().openMainMenu();
        this.setLockedResume(false);
        this.getScreen().getControllerDispatcher().setLock(false);

        JGemsAPI.clearScriptingEngine();
        this.requestsFromThreads.destroyMap = false;
    }

    public void loadMap(@NotNull IMapProcessor mapProcessor) {
        if (!this.engineState().isEngineIsReady()) {
            throw new JGemsRuntimeException("Attempted to load mapping, before initialization");
        }
        this.getSoundManager().stopAllSounds();
        JGemsHelper.screen().getScreen().showGameLoadingScreen("Loading Map: " + mapProcessor.getMapName() + "(" + mapProcessor.getMapInformation() + ")");

        this.getMapping().loadMap(mapProcessor, (IMapActionCallback) this.getScreen().getScene().getSceneRenderer());
        JGemsHelper.controller().setCursorInCenter();

        JGemsHelper.screen().setWindowFocus(true);
        this.getScreen().removeLoadingScreen();

        this.resumeGame();
        this.setLockedResume(false);
        this.getScreen().getControllerDispatcher().setLock(false);
        this.requestsFromThreads.mapProcessor = null;
    }

    @SuppressWarnings("all")
    public boolean isLockedResuming() {
        return this.engineState().lockedUnPausing;
    }

    public void setLockedResume(boolean lock) {
        this.engineState().lockedUnPausing = lock;
    }

    public void pauseGame() {
        this.engineState().paused = true;
    }

    public void resumeGame() {
        if (!this.isLockedResuming()) {
            this.engineState().paused = false;
        }
    }

    @SuppressWarnings("all")
    public void startSystem() {
        final ArrayList<Exception> exceptionList = new ArrayList<>();
        JGemsCore.printSystemInfo();
        if (this.engineState().isEngineIsReady()) {
            Log.get().warn("Engine thread is currently running");
            return;
        }
        this.systemThread = new Thread(() -> {
            try {
                JGemsAPI.APIAppData().preInit(this);
                JGems3D.get().getLocalisation().setLanguage(ISource.Source.INSIDE_JAR, JGems3D.get().getGameSettings().language.getCurrentLanguage());
                this.getResourceManager().initGlobalResources();
                this.getSoundManager().createSystem();
                this.getPhysics().initService();
                this.createGraphics();
                JGemsAPI.APIAppData().postInit(this);
                this.engineState().gameResourcesLoaded = true;
                this.engineState().engineIsReady = true;
                this.createMappingObject();
                this.getScreen().runRenderThread();

                if (JGemsLaunchArgsRegistry.INSTANCE.getValue(JGemsLaunchArgsRegistry.JGemsLaunchArgs.MAP_TEST) == Boolean.TRUE) {
                    @Nullable String mapPath = JGemsLaunchArgsRegistry.INSTANCE.getValue(JGemsLaunchArgsRegistry.JGemsLaunchArgs.MAP_PATH);
                    if (mapPath != null) {
                        //this.getMapping().loadMap(new External);
                    }
                }
            } catch (Exception e) {
                JGems3D.close(null);
                exceptionList.add(e);
                Log.get().exception(e);
            } finally {
                try {
                    JGems3D.freeSync();
                    this.exitMap();
                    if (!this.getPhysics().waitForFullTermination()) {
                        Log.get().error("Waited for physics termination too long...");
                    }
                    if (this.getScreen().getScene() != null) {
                        this.getScreen().getScene().getSceneRenderer().destroySceneIndirectRenderBuffer();
                    }
                    JGemsLaunchArgsRegistry.clear();
                    this.getSoundManager().stopAllSounds();
                    this.getResourceManager().destroy();
                    this.getSoundManager().destroy();
                    this.getPhysics().getPhysicsProcessor().clearResources();
                    JGemsAPI.get().close();
                    Log.get().debug("END");
                } catch (Exception e) {
                    exceptionList.add(e);
                    Log.get().exception(e);
                } finally {
                    if (!exceptionList.isEmpty()) {
                        JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder to find out the details.", exceptionList);
                    }
                }
            }
        });
        this.systemThread.setName("system");
        this.systemThread.start();
    }

    public void addExceptionInTrace(Exception e) {
        if (e != null) {
            this.getExceptionsBuffer().add(e);
        }
    }

    public boolean isCurrentGameMapPlayerValid() {
        return this.getMapping().isPlayerValid();
    }

    public boolean isCurrentGameMapValid() {
        return this.getMapping().isMapValid();
    }

    public IPlayer getCurrentGameMapPlayer() {
        return this.getMapping().getCurrentPlayer();
    }

    public IGameMap getCurrentGameMap() {
        return this.getMapping().getCurrentLoadedMap();
    }

    private JGemsMapping getMapping() {
        return this.mapping;
    }

    private Set<Exception> getExceptionsBuffer() {
        return this.exceptionsBuffer;
    }

    public JGemsSoundManager getSoundManager() {
        return this.jGemsSoundManager;
    }

    public JGemsScreen getScreen() {
        return this.jGemsScreen;
    }

    public JGemsPhysics getPhysics() {
        return this.jGemsPhysics;
    }

    public JGemsResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public Thread getSystemThread() {
        return this.systemThread;
    }

    public static void printSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Properties properties = System.getProperties();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();

        Log.get().separator();
        Log.get().info("****DATA***");
        Log.get().separator();

        Log.get().info("SYSTEM INFO");
        Log.get().info(osBean.getName());
        Log.get().info("System: " + osBean.getName());
        Log.get().info("System architecture: " + osBean.getArch());
        Log.get().info("System version: " + osBean.getVersion());

        Log.get().info("");
        Log.get().info("JAVA INFO");
        Log.get().info("Java version: " + runtimeBean.getSpecVersion());
        Log.get().info("Java vendor: " + runtimeBean.getSpecVendor());
        Log.get().info("Java VM: " + runtimeBean.getVmVersion());
        Log.get().info("Java VM version: " + runtimeBean.getVmVersion());

        Log.get().info("");
        Log.get().info("USER INFO");
        Log.get().info("User name: " + properties.getProperty("user.name"));
        Log.get().info("User home: " + properties.getProperty("user.home"));
        Log.get().info("User dir: " + properties.getProperty("user.dir"));

        Log.get().info("");
        Log.get().info("HARDWARE INFO");
        Log.get().info("Available processors: " + availableProcessors);
        Log.get().info("Free memory: " + freeMemory / 1024 / 1024 + " MB");
        Log.get().info("Total memory: " + totalMemory / 1024 / 1024 + " MB");
        Log.get().info("Max memory: " + (maxMemory == Long.MAX_VALUE ? "UNLIMITED" : maxMemory / 1024 / 1024 + " MB"));

        Log.get().separator();
        Log.get().info("****DATA***");
        Log.get().separator();
        Log.get().info("");
    }

    private void printGraphicsInfo() {
        Log.get().info("");
        Log.get().separator();
        Log.get().info("***RENDER INFO***");
        Log.get().separator();
        Log.get().info("Renderer: " + GL46.glGetString(GL46.GL_RENDERER));
        Log.get().info("OpenGL Version: " + GL46.glGetString(GL46.GL_VERSION));
        Log.get().info("Vendor: " + GL46.glGetString(GL46.GL_VENDOR));
        Log.get().separator();
        Log.get().info("***RENDER INFO***");
        Log.get().separator();
        Log.get().info("");
    }

    @Override
    public EngineState engineState() {
        return this.engineState;
    }

    private void createGraphics() {
        this.getScreen().createScreenAndContext();
        if (JGems3D.FIRST_LAUNCH) {
            JGems3D.get().getGameSettings().setDefaultByPerfStat(PerformanceStat.getSystemStat());
            JGems3D.get().getGameSettings().saveOptions();
        }
        this.printGraphicsInfo();
        this.getResourceManager().loadGlobalResources();
        this.getScreen().createObjects(this.getScreen().getWindow());
    }

    private class RequestsFromThreads {
        public boolean destroyMap;
        public IMapProcessor mapProcessor;

        public void update() {
            if (this.destroyMap) {
                JGemsCore.this.exitMap();
                return;
            }
            if (this.mapProcessor != null) {
                JGemsCore.this.loadMap(this.mapProcessor);
            }
        }
    }

    public class EngineState {
        private boolean gameResourcesLoaded;
        private boolean engineIsReady;
        private boolean paused;
        private boolean lockedUnPausing;

        public EngineState() {
            this.gameResourcesLoaded = false;
            this.paused = true;
            this.engineIsReady = false;
            this.lockedUnPausing = false;
        }

        public boolean isLockedUnPausing() {
            return this.lockedUnPausing;
        }

        public boolean isEngineIsReady() {
            return this.engineIsReady;
        }

        public boolean gameResourcesLoaded() {
            return this.gameResourcesLoaded;
        }

        public boolean isPaused() {
            return !JGemsCore.this.getMapping().isMapValid() || this.paused;
        }
    }
}
