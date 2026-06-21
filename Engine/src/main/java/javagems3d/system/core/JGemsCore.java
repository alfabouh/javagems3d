package javagems3d.system.core;

import api.scripting.coding.env.internal.util.global.JSScriptGlobalData;
import api.scripting.coding.env.internal.util.lang.JSLocalization;
import api.scripting.coding.env.internal.util.settings.JSGameSettings;
import api.scripting.coding.env.internal.util.settings.JSPerfTestResult;
import api.system.JGemsAPI;
import api.scripting.JavaToJsAPI;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.help.JGemsHelper;
import javagems3d.system.core.transmitter.ThreadActionsTransmitter;
import javagems3d.system.external.gaming.JGemsGameInstance;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.JGemsMapInstance;
import javagems3d.system.external.mapping.processing.ExternalMapProcessor;
import javagems3d.system.external.mapping.processing.base.IMapProcessor;
import javagems3d.system.external.mapping.processing.callbacks.IMapActionCallback;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.resources.localisation.JGemsLocalization;
import javagems3d.system.resources.localisation.LocalizationManager;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
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
    public static final String ENG_VER = "1.0b-dev build 19";

    private final JGemsSoundManager jGemsSoundManager;
    private final JGemsScreen jGemsScreen;
    private final JGemsPhysics jGemsPhysics;
    private final JGemsResourceManager resourceManager;
    private final JGemsLocalization localisation;

    private final EngineState engineState;
    private final RequestsFromThreads requestsFromThreads;
    private Thread systemThread;
    private JGemsMapInstance mapInstance;
    private JGemsGameInstance gameInstance;
    private final Set<Exception> exceptionsBuffer;

    public JGemsCore() {
        this.jGemsPhysics = new JGemsPhysics(JGemsPhysics.TICKS_PER_SECOND);
        this.jGemsSoundManager = new JGemsSoundManager();
        this.jGemsScreen = new JGemsScreen();
        {
            JavaToJsAPI.setJSScreen(this.jGemsScreen);
        }
        this.resourceManager = new JGemsResourceManager();
        this.localisation = new JGemsLocalization();

        this.engineState = new EngineState();
        this.systemThread = null;

        this.requestsFromThreads = new RequestsFromThreads();
        this.exceptionsBuffer = SyncManager.createSyncronisedSet();

        JGemsHelper.initJGemsCore(this);
    }

    private void createGamingObject(@NotNull String externalGamePath) {
        this.gameInstance = new JGemsGameInstance();
        this.gameInstance.loadExternalGameFiles(JGemsAPI.APIEditorResources().getEditorResourcesManager(), new JGemsPath(externalGamePath));
        JSScriptGlobalData.setAbsoluteSystemPath(new JGemsPath(externalGamePath));
    }

    private void createMappingObject() {
        this.mapInstance = new JGemsMapInstance(this.getScreen().getScene().getSceneRenderer(), (SceneWorld) this.getScreen().getSceneWorld(), this.getPhysics().getPhysicsWorld(), this.getResourceManager());
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
        if (this.getMapInstance() == null || !this.getMapInstance().isMapValid()) {
            return;
        }

        this.pauseGame();
        this.getScreen().showGameLoadingScreen("Exiting world...");

        this.getMapInstance().destroyMap((IMapActionCallback) this.getScreen().getScene().getSceneRenderer());

        this.getSoundManager().stopAllSounds();
        this.getResourceManager().destroyResourcesDataCache();
        this.getResourceManager().getLocalResources().destroy();
        JGems3D.GC();

        this.getScreen().getScene().setCamera(null);
        this.getScreen().getWindow().setFocus(false);

        this.getScreen().removeLoadingScreen();
        JGemsHelper.ui().openMainMenu();
        this.setLockedResume(false);
        this.getScreen().getControllerDispatcher().setLock(false);

        JGemsAPI.getAPIScriptingCore().getLocalMapContext().close();
        this.requestsFromThreads.destroyMap = false;
    }

    public JGemsPath getMapPath(String relativePath) {
        return this.gameInstance != null ? this.gameInstance.getMaps().get(relativePath) : new JGemsPath(relativePath);
    }

    public void loadMap(@NotNull IMapProcessor mapProcessor) {
        if (!this.engineState().isEngineIsReady()) {
            throw new JGemsRuntimeException("Attempted to load mapping, before initialization");
        }
        if (this.getMapInstance().isMapValid()) {
            this.exitMap();
        } else {
            this.getSoundManager().stopAllSounds();
        }
        JGemsHelper.screen().getScreen().showGameLoadingScreen("Loading Map: " + mapProcessor.getMapName() + "(" + mapProcessor.getMapInformation() + ")");
        ThreadActionsTransmitter.INSTANCE.clear();
        this.getMapInstance().loadMap(mapProcessor, (IMapActionCallback) this.getScreen().getScene().getSceneRenderer());
        JGems3D.GC();
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

    public static boolean loadTestMap() {
        if (JGemsLaunchArgsRegistry.INSTANCE.getValue(JGemsLaunchArgsRegistry.DEFAULT_ARGS.MAP_TEST) == Boolean.TRUE) {
            @Nullable String mapPath = JGemsLaunchArgsRegistry.INSTANCE.getValue(JGemsLaunchArgsRegistry.DEFAULT_ARGS.TEST_MAP_ID);
            if (mapPath != null) {
                try {
                    JGemsHelper.map().loadMap(new ExternalMapProcessor.Default(new JGemsPath(mapPath), null));
                    return true;
                } catch (Exception e) {
                    Log.get().exception(e);
                    return false;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("all")
    public void startSystem(@Nullable String externalGamePath) {
        final ArrayList<Exception> exceptionList = new ArrayList<>();
        JGemsCore.printSystemInfo();
        if (this.engineState().isEngineIsReady()) {
            Log.get().warn("Engine thread is currently running");
            return;
        }
        this.systemThread = new Thread(() -> {
            try {
                JGemsAPI.APIAppData().preInit(this);
                this.getLocalization().readLanguageMap(LocalizationManager.ENGLISH, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.LANG, "english.lang"), ISource.Source.INSIDE_JAR));
                if (externalGamePath != null) {
                    JGemsAPI.getAPIScriptingCore().initGame(JGemsGameInstance.getScriptsFolder(new JGemsPath(externalGamePath)));
                    JSScriptGlobalData.setAbsoluteSystemPath(new JGemsPath(externalGamePath));
                }
                {
                    JavaToJsAPI.Js_GAME_initEvents();
                    JSScriptGlobalData.setLocalisation(new JSLocalization(this.getLocalization()));
                    final JSGameSettings gameSettings = new JSGameSettings(JGems3D.get().getGameSettings());
                    JSScriptGlobalData.setSettings(gameSettings);
                    JavaToJsAPI.Js_GAME_settingsInitEvent__EVENT(gameSettings);
                }
                {
                    if (!JGems3D.FIRST_LAUNCH) {
                        JGems3D.get().getGameSettings().loadOptions();
                    }
                }
                this.getResourceManager().initGlobalResources();
                JGemsHelper.initResourceManager(JGems3D.get().getResourceManager());
                this.getSoundManager().createSystem();
                this.getPhysics().initService();
                this.createGraphics();
                if (externalGamePath != null) {
                    this.createGamingObject(externalGamePath);
                }
                this.createMappingObject();
                JGemsAPI.APIAppData().postInit(this);
                this.engineState().gameResourcesLoaded = true;
                this.engineState().engineIsReady = true;
                this.getScreen().runRenderThread();
            } catch (Exception e) {
                JGems3D.close(null);
                exceptionList.add(e);
                Log.get().exception(e);
            } finally {
                try {
                    JGems3D.freeSync();
                    this.exitMap();
                    ThreadActionsTransmitter.INSTANCE.clear();
                    JavaToJsAPI.ScriptEnd(JavaToJsAPI.Target.Game);
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
        return this.getMapInstance().isPlayerValid();
    }

    public boolean isCurrentGameMapValid() {
        return this.getMapInstance().isMapValid();
    }

    public IPlayer getCurrentGameMapPlayer() {
        return this.getMapInstance().getCurrentPlayer();
    }

    public IGameMap getCurrentGameMap() {
        return this.getMapInstance().getCurrentLoadedMap();
    }

    private JGemsMapInstance getMapInstance() {
        synchronized (this) {
            return this.mapInstance;
        }
    }

    public JGemsGameInstance getGameInstance() {
        synchronized (this) {
            return this.gameInstance;
        }
    }

    private Set<Exception> getExceptionsBuffer() {
        return this.exceptionsBuffer;
    }

    public JGemsScreen getScreen() {
        synchronized (this.jGemsScreen) {
            return this.jGemsScreen;
        }
    }

    public JGemsPhysics getPhysics() {
        synchronized (this.jGemsPhysics) {
            return this.jGemsPhysics;
        }
    }

    public JGemsSoundManager getSoundManager() {
        synchronized (this.jGemsSoundManager) {
            return this.jGemsSoundManager;
        }
    }

    public JGemsResourceManager getResourceManager() {
        synchronized (this.resourceManager) {
            return this.resourceManager;
        }
    }

    public JGemsLocalization getLocalization() {
        synchronized (this.localisation) {
            return this.localisation;
        }
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
        synchronized (this) {
            return this.engineState;
        }
    }

    private void createGraphics() {
        this.getScreen().createScreenAndContext();
        if (JGems3D.FIRST_LAUNCH) {
            final PerformanceStat.Result result = PerformanceStat.getSystemStat();
            JGems3D.get().getGameSettings().setDefaultByPerfStat(result);
            {
                JavaToJsAPI.Js_GAME_afterSettingsPerfTestEvent__EVENT(JSPerfTestResult.choose(result), JSScriptGlobalData.getGameSettings());
            }
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
            return !JGemsCore.this.getMapInstance().isMapValid() || this.paused;
        }
    }
}
