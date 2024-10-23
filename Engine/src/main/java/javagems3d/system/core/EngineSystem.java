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

import com.jme3.bullet.collision.shapes.Box2dShape;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import javagems3d.physics.entities.bullet.wrappers.BulletBody;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.resources.assets.material.samples.CubeMapSample;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import api.bridge.APIContainer;
import api.bridge.events.APIEventsLauncher;
import javagems3d.graphics.opengl.environment.Environment;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.core.player.LocalPlayer;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.resources.manager.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.stat.PerformanceStat;
import api.app.events.bus.Events;
import logger.managers.JGemsLogging;
import javagems3d.temp.map_sys.save.objects.map_prop.FogProp;
import javagems3d.temp.map_sys.save.objects.map_prop.SkyProp;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

public class EngineSystem implements IEngine {
    public static final String ENG_FILEPATH = "jgems3d";
    public static final String ENG_NAME = "JavaGems 3D";
    public static final String ENG_VER = "0.30a-dev";

    private final JGemsResourceManager resourceManager;
    private final EngineState engineState;
    private final RequestsFromThreads requestsFromThreads;
    private Thread thread;
    private IMapLoader mapLoader;
    private LocalPlayer localPlayer;

    public EngineSystem() {
        this.thread = null;
        this.engineState = new EngineState();
        this.resourceManager = new JGemsResourceManager();
        this.mapLoader = null;

        this.requestsFromThreads = new RequestsFromThreads();
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
        this.readAndProcessMapData();
        this.requestsFromThreads.loadMap = null;
    }

    public void destroyMap() {
        if (!this.isCurrentThreadOGL()) {
            this.requestsFromThreads.destroyMap = true;
            return;
        }
        JGemsHelper.getLogger().log("Destroying map!");
        APIEventsLauncher.pushEvent(new Events.MapDestroy(Events.Stage.PRE, mapLoader));
        this.pauseGame();
        JGems3D.get().getScreen().showGameLoadingScreen("Exit world...");
        this.clean();
        JGemsHelper.GAME.unPauseGameAndUnLockUnPausing();
        JGemsHelper.GAME.unLockController();
        JGemsHelper.CAMERA.setCurrentCamera(null);
        JGemsHelper.WINDOW.setWindowFocus(false);
        JGems3D.get().getScreen().removeLoadingScreen();
        this.mapLoader = null;
        JGems3D.get().showMainMenu();
        APIEventsLauncher.pushEvent(new Events.MapDestroy(Events.Stage.POST, mapLoader));
        this.requestsFromThreads.destroyMap = false;
    }

    private void readAndProcessMapData() {
        if (!this.engineState().isEngineIsReady()) {
            JGemsHelper.getLogger().error("Engine thread is not ready to load map!");
            this.mapLoader = null;
            return;
        }

        if (this.getMapLoader() == null) {
            JGemsHelper.getLogger().error("Invalid map!");
            return;
        }

        GameResources globalRes = this.getResourceManager().getGlobalResources();
        GameResources localRes = this.getResourceManager().getLocalResources();

        JGems3D.get().getScreen().showGameLoadingScreen("Loading Map...");
        this.startWorlds();
        JGemsHelper.getLogger().log("Loading map " + this.currentMapName());
        PhysicsWorld physicsWorld = JGemsHelper.getPhysicsWorld();
        SceneWorld sceneWorld = JGemsHelper.getSceneWorld();
        APIEventsLauncher.pushEvent(new Events.MapLoad(Events.Stage.PRE, mapLoader));
        this.getMapLoader().preLoad(physicsWorld, sceneWorld);

        Environment environment = sceneWorld.getEnvironment();
        FogProp fogProp = this.getMapLoader().getLevelInfo().getMapProperties().getFogProp();
        SkyProp skyProp = this.getMapLoader().getLevelInfo().getMapProperties().getSkyProp();

        if (fogProp != null) {
            if (fogProp.isFogEnabled()) {
                environment.getFog().setColor(fogProp.getFogColor());
                environment.getFog().setDensity(fogProp.getFogDensity());
                environment.getSkyBox().setSkyCoveredByFog(fogProp.isSkyCoveredByFog());
            } else {
                environment.getFog().disable();
            }
        }

        if (skyProp != null) {
            CubeMapSample cubeMapProgram = globalRes.getResource(skyProp.getSkyBoxPath());
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
        this.localPlayer = new LocalPlayer(APIContainer.get().getApiGameInfo().getAppManager().createPlayer(this.getMapLoader()));
        Vector3f startPos = new Vector3f(pair.getFirst());
        Vector3f startRot = new Vector3f(0.0f, (float) (pair.getSecond() + (Math.PI / 2.0f)), 0.0f);
        this.getLocalPlayer().addPlayerInWorlds(physicsWorld, startPos, startRot);

        JGemsHelper.getLogger().log(this.currentMapName() + ": Map Loaded!");

        JGemsHelper.CONTROLLER.setCursorInCenter();
        JGemsHelper.CONTROLLER.attachControllerTo(JGemsControllerDispatcher.mouseKeyboardController, this.getLocalPlayer().getEntityPlayer());
        JGemsHelper.CAMERA.enableAttachedCamera((WorldItem) this.getLocalPlayer().getEntityPlayer());

        if (true) {//TODO
           this.buildInvisibleBorders(physicsWorld, JGems3D.MAP_MAX_SIZE);
        }

        this.getMapLoader().postLoad(physicsWorld, sceneWorld);
        APIEventsLauncher.pushEvent(new Events.MapLoad(Events.Stage.POST, mapLoader));

        JGemsHelper.WINDOW.setWindowFocus(true);
        JGemsHelper.getScreen().removeLoadingScreen();

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
        JGemsHelper.getPhysicsWorld().onWorldStart();
        JGemsHelper.getSceneWorld().onWorldStart();
    }

    private void endWorlds() {
        JGemsHelper.getPhysicsWorld().onWorldEnd();
        JGemsHelper.getSceneWorld().onWorldEnd();
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
        this.thread = new Thread(() -> {
            boolean badExit = true;
            try {
                APIContainer.get().getApiGameInfo().getAppInstance().preInitEvent(this);
                JGems3D.get().getLocalisation().setLanguage(JGems3D.get().getGameSettings().language.getCurrentLanguage());
                this.getResourceManager().initGlobalResources();
                this.getResourceManager().initLocalResources();
                APIContainer.get().getApiTBoxInfo().getAppInstance().initEntitiesUserData(this.getResourceManager(), APIContainer.get().getTBoxEntitiesUserData());
                JGems3D.get().getSoundManager().createSystem();
                JGems3D.get().getPhysicThreadManager().initService();
                this.createGraphics();
                APIContainer.get().getApiGameInfo().getAppInstance().postInitEvent(this);
                this.engineState().gameResourcesLoaded = true;
                this.engineState().engineIsReady = true;
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
                        JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder to find out the details.");
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
        return this.engineState().lockedUnPausing;
    }

    public void setLockedUnPausing(boolean lockedUnPausing) {
        this.engineState().lockedUnPausing = lockedUnPausing;
    }

    private void printSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Properties properties = System.getProperties();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();

        JGemsHelper.getLogger().log(JGems3D.checkIfSys64B() ? "x64" : "x32");
        JGemsHelper.getLogger().log("==========================================================");
        JGemsHelper.getLogger().log("****DATA***");
        JGemsHelper.getLogger().log("==========================================================");

        JGemsHelper.getLogger().log("SYSTEM INFO");
        JGemsHelper.getLogger().log(osBean.getName());
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
        if (JGems3D.FIRST_LAUNCH) {
            JGems3D.get().getGameSettings().setDefaultByPerfStat(PerformanceStat.getSystemStat());
            JGems3D.get().getGameSettings().saveOptions();
        }
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
            return EngineSystem.this.getMapLoader() == null || this.paused;
        }
    }
}
