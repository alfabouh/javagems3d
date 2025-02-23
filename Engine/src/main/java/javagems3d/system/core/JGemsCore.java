/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.core;

import api.events.EventBus;
import api.system.JGemsAPI;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Plane;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.physics.entities.bullet.wrappers.BulletBody;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import api.events.EventLauncher;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.core.player.LocalPlayer;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.managing.resources.GameResources;
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
        this.exceptionsBuffer = new HashSet<>();
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
            JGemsHelper.getLogger().error("Firstly, the current map should be destroyed");
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
        JGemsHelper.getLogger().trace("Exit map");
        EventLauncher.pushEvent(new EventBus.MapDestroy(EventBus.Run.PRE, mapLoader));
        this.pauseGame();
        this.getScreen().showGameLoadingScreen("Exiting world...");
        this.clear();
        JGemsHelper.GAME.unPauseGameAndUnLockUnPausing();
        JGemsHelper.GAME.unLockController();
        JGemsHelper.CAMERA.setCurrentCamera(null);
        JGemsHelper.WINDOW.setWindowFocus(false);
        this.getScreen().removeLoadingScreen();
        this.mapLoader = null;
        JGems3D.get().showMainMenu();
        EventLauncher.pushEvent(new EventBus.MapDestroy(EventBus.Run.POST, mapLoader));
        this.requestsFromThreads.destroyMap = false;
    }

    private void readAndProcessMapData() {
        if (!this.engineState().isEngineIsReady()) {
            throw new JGemsRuntimeException("Attempted to load map, before initialization");
        }

        if (this.getMapLoader() == null) {
            JGemsHelper.getLogger().error("Invalid map");
            return;
        }

        GameResources globalRes = this.getResourceManager().getGlobalResources();
        GameResources localRes = this.getResourceManager().getLocalResources();

        this.getScreen().showGameLoadingScreen("Loading Map...");
        this.createWorlds();
        JGemsHelper.getLogger().trace("Loading map: " + this.currentMapName());
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
            environment.getSkyBox().getSun().setSunPosition(skyProp.getSunPos());
            environment.getSkyBox().getSun().setSunColor(skyProp.getSunColor());
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
            JGemsHelper.CONTROLLER.attachControllerTo(JGemsControllerDispatcher.mouseKeyboardController, this.getLocalPlayer().getEntityPlayer());
            JGemsHelper.CAMERA.enableAttachedCamera((WorldItem) this.getLocalPlayer().getEntityPlayer());
        } else {
            JGemsHelper.CAMERA.enableFreeCamera(JGemsHelper.CONTROLLER.getCurrentController(), startPos, startRot);
        }
        JGemsHelper.getLogger().info("Successfully loaded map: " + this.currentMapName());
        JGemsHelper.CONTROLLER.setCursorInCenter();

        if (true) {//TODO
           this.buildInvisibleBorders(physicsWorld, JGems3D.MAP_MAX_SIZE);
        }

        this.getMapLoader().postLoad(physicsWorld, sceneWorld);
        EventLauncher.pushEvent(new EventBus.MapLoad(EventBus.Run.POST, mapLoader));
        this.getResourceManager().writeResourcesDataCache();
        this.getScreen().getScene().getSceneRenderer().onMapLoaded(this.getMapLoader(), this.getResourceManager());

        JGemsHelper.WINDOW.setWindowFocus(true);
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
            JGemsHelper.getLogger().warn("Engine thread is not ready to be cleaned");
            return;
        }
        this.getSoundManager().stopAllSounds();
        this.destroyWorlds();
        this.getScreen().getScene().getSceneRenderer().onMapDestroyed(this.getMapLoader(), this.getResourceManager());
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
        this.getPhysics().getPhysicsWorld().onWorldEnd();
        this.getScreen().getSceneWorld().onWorldEnd();
    }

    @SuppressWarnings("all")
    public void startSystem() {
        JGemsCore.printSystemInfo();
        if (this.engineState().isEngineIsReady()) {
            JGemsHelper.getLogger().warn("Engine thread is currently running");
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
                this.appendException(err, e);
                JGemsHelper.getLogger().exception(e);
            } finally {
                try {
                    JGems3D.freeSync();
                    if (!this.getPhysics().waitForFullTermination()) {
                        JGemsHelper.getLogger().error("Waited for physics termination too long...");
                    }
                    this.getScreen().getScene().getSceneRenderer().destroySceneIndirectRenderBuffer();
                    this.destroyWorlds();
                    this.getSoundManager().stopAllSounds();
                    this.getResourceManager().destroy();
                    this.getSoundManager().destroy();
                    this.getPhysics().getPhysicsProcessor().clearResources();
                    this.localPlayer = null;
                    JGemsHelper.getLogger().debug("END");
                } catch (Exception e) {
                    this.appendException(err, e);
                    JGemsHelper.getLogger().exception(e);
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

        JGemsHelper.getLogger().info("==========================================================");
        JGemsHelper.getLogger().info("****DATA***");
        JGemsHelper.getLogger().info("==========================================================");

        JGemsHelper.getLogger().info("SYSTEM INFO");
        JGemsHelper.getLogger().info(osBean.getName());
        JGemsHelper.getLogger().info("System: " + osBean.getName());
        JGemsHelper.getLogger().info("System architecture: " + osBean.getArch());
        JGemsHelper.getLogger().info("System version: " + osBean.getVersion());

        JGemsHelper.getLogger().info("");
        JGemsHelper.getLogger().info("JAVA INFO");
        JGemsHelper.getLogger().info("Java version: " + runtimeBean.getSpecVersion());
        JGemsHelper.getLogger().info("Java vendor: " + runtimeBean.getSpecVendor());
        JGemsHelper.getLogger().info("Java VM: " + runtimeBean.getVmVersion());
        JGemsHelper.getLogger().info("Java VM version: " + runtimeBean.getVmVersion());

        JGemsHelper.getLogger().info("");
        JGemsHelper.getLogger().info("USER INFO");
        JGemsHelper.getLogger().info("User name: " + properties.getProperty("user.name"));
        JGemsHelper.getLogger().info("User home: " + properties.getProperty("user.home"));
        JGemsHelper.getLogger().info("User dir: " + properties.getProperty("user.dir"));

        JGemsHelper.getLogger().info("");
        JGemsHelper.getLogger().info("HARDWARE INFO");
        JGemsHelper.getLogger().info("Available processors: " + availableProcessors);
        JGemsHelper.getLogger().info("Free memory: " + freeMemory / 1024 / 1024 + " MB");
        JGemsHelper.getLogger().info("Total memory: " + totalMemory / 1024 / 1024 + " MB");
        JGemsHelper.getLogger().info("Max memory: " + (maxMemory == Long.MAX_VALUE ? "UNLIMITED" : maxMemory / 1024 / 1024 + " MB"));

        JGemsHelper.getLogger().info("==========================================================");
        JGemsHelper.getLogger().info("****DATA***");
        JGemsHelper.getLogger().info("==========================================================");
        JGemsHelper.getLogger().info("");
    }

    private void printGraphicsInfo() {
        JGemsHelper.getLogger().info("");
        JGemsHelper.getLogger().info("==========================================================");
        JGemsHelper.getLogger().info("***RENDER INFO***");
        JGemsHelper.getLogger().info("==========================================================");
        JGemsHelper.getLogger().info("Renderer: " + GL46.glGetString(GL46.GL_RENDERER));
        JGemsHelper.getLogger().info("OpenGL Version: " + GL46.glGetString(GL46.GL_VERSION));
        JGemsHelper.getLogger().info("Vendor: " + GL46.glGetString(GL46.GL_VENDOR));
        JGemsHelper.getLogger().info("==========================================================");
        JGemsHelper.getLogger().info("***RENDER INFO***");
        JGemsHelper.getLogger().info("==========================================================");
        JGemsHelper.getLogger().info("");
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
