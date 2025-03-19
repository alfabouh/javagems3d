package javagems3d.system.core;

import api.events.EventBus;
import api.system.JGemsAPI;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Plane;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.help.JGemsCameraHelper;
import javagems3d.help.JGemsControllerHelper;
import javagems3d.help.JGemsCoreHelper;
import javagems3d.help.JGemsWindowHelper;
import javagems3d.physics.entities.bullet.wrappers.BulletBody;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.map.IMapActionsCallback;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.synchronizing.SyncManager;
import logger.Log;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import api.events.EventLauncher;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.core.player.LocalPlayer;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.stat.PerformanceStat;
import logger.managers.JGemsLogging;
import javagems3d.temp.map_sys.save.objects.map_prop.FogProp;
import javagems3d.temp.map_sys.save.objects.map_prop.SkyProp;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class JGemsCore implements ICore {
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
    private IMapLoader mapLoader;
    private LocalPlayer localPlayer;

    private final Set<Exception> exceptionsBuffer;

    public JGemsCore() {
        this.jGemsPhysics = new JGemsPhysics(JGemsPhysics.TICKS_PER_SECOND);
        this.jGemsSoundManager = new JGemsSoundManager();
        this.jGemsScreen = new JGemsScreen();
        this.resourceManager = new JGemsResourceManager();

        this.engineState = new EngineState();
        this.systemThread = null;
        this.mapLoader = null;

        this.requestsFromThreads = new RequestsFromThreads();
        this.exceptionsBuffer = SyncManager.createSyncronisedSet();
    }

    public void update() {
        this.requestsFromThreads.update();
    }

    @SuppressWarnings("all")
    private boolean isCurrentThreadOGL() {
        return GLFW.glfwGetCurrentContext() != 0L;
    }

    public void entryMap(IMapLoader mapLoader) {
        if (!this.isCurrentThreadOGL()) {
            this.requestsFromThreads.loadMap = mapLoader;
            return;
        }
        if (this.getMapLoader() != null) {
            Log.get().error("Firstly, the current mapping should be destroyed");
            return;
        }
        this.mapLoader = mapLoader;
        this.readAndProcessMapData();
        this.requestsFromThreads.loadMap = null;
    }

    public void exitMap() {
        if (!this.isCurrentThreadOGL()) {
            this.requestsFromThreads.destroyMap = true;
            return;
        }
        Log.get().trace("Exit mapping");
        EventLauncher.pushEvent(new EventBus.MapDestroy(EventBus.Run.PRE, mapLoader));
        this.pauseGame();
        this.getScreen().showGameLoadingScreen("Exiting world...");
        this.clear();
        JGemsCoreHelper.unPauseGameAndUnLockUnPausing();
        JGemsCoreHelper.unLockController();
        JGemsCameraHelper.setCurrentCamera(null);
        JGemsWindowHelper.setWindowFocus(false);
        this.getScreen().removeLoadingScreen();
        this.mapLoader = null;
        JGems3D.get().showMainMenu();
        EventLauncher.pushEvent(new EventBus.MapDestroy(EventBus.Run.POST, mapLoader));
        this.requestsFromThreads.destroyMap = false;
    }

    private void readAndProcessMapData() {
        if (!this.engineState().isEngineIsReady()) {
            throw new JGemsRuntimeException("Attempted to load mapping, before initialization");
        }

        if (this.getMapLoader() == null) {
            Log.get().error("Invalid mapping");
            return;
        }

        SystemResources globalRes = this.getResourceManager().getGlobalResources();
        SystemResources localRes = this.getResourceManager().getLocalResources();

        this.getScreen().showGameLoadingScreen("Loading Map...");
        this.createWorlds();
        Log.get().trace("Loading mapping: " + this.currentMapName());
        PhysicsWorld physicsWorld = this.getPhysics().getPhysicsProcessor().getPhysicsWorld();
        SceneWorld sceneWorld = this.getScreen().getSceneWorld();
        EventLauncher.pushEvent(new EventBus.MapLoad(EventBus.Run.PRE, mapLoader));
        this.getMapLoader().preLoad(physicsWorld, sceneWorld);

        JGemsEnvironment environment = sceneWorld.getEnvironment();
        FogProp fogProp = this.getMapLoader().getLevelInfo().getMapProperties().getFogProp();
        SkyProp skyProp = this.getMapLoader().getLevelInfo().getMapProperties().getSkyProp();

        if (fogProp != null) {
            if (fogProp.isFogEnabled()) {
                environment.getFogManager().setColor(fogProp.getFogColor());
                environment.getFogManager().setDensity(fogProp.getFogDensity());
                environment.getSkyBox().setSkyCoveredByFog(fogProp.isSkyCoveredByFog());
            } else {
                environment.getFogManager().disable();
            }
        }

        if (skyProp != null) {
            CubeMapTexture cubeMapProgram = globalRes.getResource(skyProp.getSkyBoxPath());
            if (cubeMapProgram != null) {
                environment.getSkyBox().setSky2DTexture(cubeMapProgram);
            }
            environment.getSkyBox().getSun().setLightPosition(skyProp.getSunPos());
            environment.getSkyBox().getSun().setLightColor(skyProp.getSunColor());
            environment.getSkyBox().getSun().setSunBrightness(skyProp.getSunBrightness());
        }

        this.getMapLoader().fillSkyBox(environment.getSkyBox().getBackground());
        this.getMapLoader().createMap(globalRes, localRes, physicsWorld, sceneWorld);

        Pair<Vector3f, Double> pair = this.getMapLoader().getLevelInfo().chooseRandomSpawnPoint();
        Vector3f startPos = new Vector3f(pair.getFirst());
        Vector3f startRot = new Vector3f(0.0f, (float) (pair.getSecond() + (Math.PI / 2.0f)), 0.0f);
        if (this.getMapLoader().playerConstructor() != null) {
            this.localPlayer = new LocalPlayer(this.getMapLoader().playerConstructor());
            this.getLocalPlayer().addPlayerInWorlds(physicsWorld, startPos, startRot);
            JGemsControllerHelper.attachControllerTo(JGemsControllerDispatcher.mouseKeyboardController, this.getLocalPlayer().getEntityPlayer());
            JGemsCameraHelper.enableAttachedCamera((WorldItem) this.getLocalPlayer().getEntityPlayer());
        } else {
            JGemsCameraHelper.enableFreeCamera(JGemsControllerHelper.getCurrentController(), startPos, startRot);
        }
        Log.get().info("Successfully loaded mapping: " + this.currentMapName());
        JGemsControllerHelper.setCursorInCenter();

        if (true) {//TODO
           this.buildInvisibleBorders(physicsWorld, JGems3D.MAP_MAX_SIZE);
        }

        this.getMapLoader().postLoad(physicsWorld, sceneWorld);
        EventLauncher.pushEvent(new EventBus.MapLoad(EventBus.Run.POST, mapLoader));
        this.getResourceManager().writeResourcesDataCache();
        ((IMapActionsCallback) this.getScreen().getScene().getSceneRenderer()).onMapLoaded(this.getMapLoader(), this.getResourceManager());

        JGemsWindowHelper.setWindowFocus(true);
        this.getScreen().removeLoadingScreen();

        this.unPauseGame();
    }

    private void buildInvisibleBorders(PhysicsWorld physicsWorld, int mapSize) {
        float worldSize = (float) mapSize;

        PlaneCollisionShape planeShape1 = new PlaneCollisionShape(new Plane(new com.jme3.math.Vector3f(1, 0, 0), -worldSize));
        PlaneCollisionShape planeShape2 = new PlaneCollisionShape(new Plane(new com.jme3.math.Vector3f(-1, 0, 0), -worldSize));
        PlaneCollisionShape planeShape3 = new PlaneCollisionShape(new Plane(new com.jme3.math.Vector3f(0, 1, 0), -worldSize));
        PlaneCollisionShape planeShape4 = new PlaneCollisionShape(new Plane(new com.jme3.math.Vector3f(0, -1, 0), -worldSize));
        PlaneCollisionShape planeShape5 = new PlaneCollisionShape(new Plane(new com.jme3.math.Vector3f(0, 0, 1), -worldSize));
        PlaneCollisionShape planeShape6 = new PlaneCollisionShape(new Plane(new com.jme3.math.Vector3f(0, 0, -1), -worldSize));

        physicsWorld.addItem(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape1, 0), "border_wall1"));
        physicsWorld.addItem(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape2, 0), "border_wall2"));
        physicsWorld.addItem(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape3, 0), "border_wall3"));
        physicsWorld.addItem(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape4, 0), "border_wall4"));
        physicsWorld.addItem(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape5, 0), "border_wall5"));
        physicsWorld.addItem(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape6, 0), "border_wall6"));
    }

    public void pauseGame() {
        this.engineState().paused = true;
    }

    public void unPauseGame() {
        if (!this.isLockedUnPausing()) {
            this.engineState().paused = false;
        }
    }

    public void clear() {
        if (this.mapLoader == null) {
            return;
        }
        if (!this.engineState().isEngineIsReady()) {
            Log.get().warn("Engine thread is not ready to be cleaned");
            return;
        }
        this.getSoundManager().stopAllSounds();
        this.destroyWorlds();
        ((IMapActionsCallback) this.getScreen().getScene().getSceneRenderer()).onMapDestroyed(this.getMapLoader(), this.getResourceManager());
        this.getResourceManager().destroyResourcesDataCache();
        this.getResourceManager().getLocalResources().destroy();
        this.localPlayer = null;
        System.gc();
    }

    private void createWorlds() {
        this.getPhysics().getPhysicsWorld().onWorldStart();
        this.getScreen().getSceneWorld().onWorldStart();
    }

    private void destroyWorlds() {
        if (this.getPhysics().getPhysicsWorld() != null) {
            this.getPhysics().getPhysicsWorld().onWorldEnd();
        }
        if (this.getScreen().getSceneWorld() != null) {
            this.getScreen().getSceneWorld().onWorldEnd();
        }
    }

    @SuppressWarnings("all")
    public void startSystem() {
        JGemsCore.printSystemInfo();
        if (this.engineState().isEngineIsReady()) {
            Log.get().warn("Engine thread is currently running");
            return;
        }
        this.systemThread = new Thread(() -> {
            StringBuilder err = new StringBuilder();
            try {
                JGemsAPI.APIAppData().preInit(this);
                JGems3D.get().getLocalisation().setLanguage(JGems3D.get().getGameSettings().language.getCurrentLanguage());
                this.getResourceManager().initGlobalResources();
                this.getSoundManager().createSystem();
                this.getPhysics().initService();
                this.createGraphics();
                JGemsAPI.APIAppData().postInit(this);
                this.engineState().gameResourcesLoaded = true;
                this.engineState().engineIsReady = true;
                this.getScreen().runRenderThread();
            } catch (Exception e) {
                JGems3D.close(null);
                this.appendException(err, e);
                Log.get().exception(e);
            } finally {
                try {
                    JGems3D.freeSync();
                    if (!this.getPhysics().waitForFullTermination()) {
                        Log.get().error("Waited for physics termination too long...");
                    }
                    if (this.getScreen().getScene() != null) {
                        this.getScreen().getScene().getSceneRenderer().destroySceneIndirectRenderBuffer();
                    }
                    this.destroyWorlds();
                    this.getSoundManager().stopAllSounds();
                    this.getResourceManager().destroy();
                    this.getSoundManager().destroy();
                    this.getPhysics().getPhysicsProcessor().clearResources();
                    this.localPlayer = null;
                    JGemsAPI.get().close();
                    Log.get().debug("END");
                } catch (Exception e) {
                    this.appendException(err, e);
                    Log.get().exception(e);
                } finally {
                    String mss = err.toString();
                    if (!mss.isEmpty()) {
                        this.collectExceptions(err);
                        JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder to find out the details.\n\n" + mss);
                    }
                }
            }
        });
        this.systemThread.setName("system");
        this.systemThread.start();
    }

    private void appendException(StringBuilder err, Exception ex) {
        err.append(ex.getClass().getSimpleName());
        String message = ex.getMessage();
        if (message != null && !message.isEmpty()) {
            err.append(": ").append(message);
        }
        err.append(System.lineSeparator());
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            err.append("-> ").append(element.getClassName()).append(".").append(element.getMethodName()).append("(").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")").append(System.lineSeparator()).append(System.lineSeparator());
        }
    }

    private void collectExceptions(StringBuilder err) {
        Iterator<Exception> iterator = this.getExceptionsBuffer().iterator();
        while (iterator.hasNext()) {
            Exception ex = iterator.next();
            this.appendException(err, ex);
            iterator.remove();
        }
    }

    public void addExceptionInTrace(Exception e) {
        if (e != null) {
            this.getExceptionsBuffer().add(e);
        }
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

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    public JGemsResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public String currentMapName() {
        return this.getMapLoader().getLevelInfo().getMapProperties().getMapName();
    }

    public Thread getSystemThread() {
        return this.systemThread;
    }

    public IMapLoader getMapLoader() {
        return this.mapLoader;
    }

    @SuppressWarnings("all")
    public boolean isLockedUnPausing() {
        return this.engineState().lockedUnPausing;
    }

    public void setLockedUnPausing(boolean lockedUnPausing) {
        this.engineState().lockedUnPausing = lockedUnPausing;
    }

    public static void printSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Properties properties = System.getProperties();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();

        Log.get().info("==========================================================");
        Log.get().info("****DATA***");
        Log.get().info("==========================================================");

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

        Log.get().info("==========================================================");
        Log.get().info("****DATA***");
        Log.get().info("==========================================================");
        Log.get().info("");
    }

    private void printGraphicsInfo() {
        Log.get().info("");
        Log.get().info("==========================================================");
        Log.get().info("***RENDER INFO***");
        Log.get().info("==========================================================");
        Log.get().info("Renderer: " + GL46.glGetString(GL46.GL_RENDERER));
        Log.get().info("OpenGL Version: " + GL46.glGetString(GL46.GL_VERSION));
        Log.get().info("Vendor: " + GL46.glGetString(GL46.GL_VENDOR));
        Log.get().info("==========================================================");
        Log.get().info("***RENDER INFO***");
        Log.get().info("==========================================================");
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
        public IMapLoader loadMap;

        public void update() {
            if (this.destroyMap) {
                JGemsCore.this.exitMap();
                return;
            }
            if (this.loadMap != null) {
                JGemsCore.this.entryMap(this.loadMap);
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
            return JGemsCore.this.getMapLoader() == null || this.paused;
        }
    }
}
