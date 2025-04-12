package javagems3d.mapping;

import api.scripting.JGemsAPIScriptingEngine;
import api.scripting.functions.APIScriptingFunctions;
import api.system.JGemsAPI;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Plane;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.*;
import javagems3d.mapping.processing.base.IMapProcessor;
import javagems3d.mapping.processing.callbacks.IMapActionCallback;
import javagems3d.physics.entities.bullet.wrappers.BulletBody;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public final class JGemsMapping {
    public static final String DATA_INFO = JGemsMapping.DATA_COMM + " VER: " + JGemsMapping.DATA_VERSION;

    public static final String DATA_COMM = "JAVAGEMS3D MAP FILE";
    public static final String DATA_VERSION = "1.0";
    public static final String MAP_PROJECT_FILE = ".jg3d";
    public static final String MAP_DATA_FILE = ".mapdata";
    public static final String MAP_SCRIPT_FILE = ".js";

    public static final Set<String> SUPPORTED_VERSIONS = new HashSet<String>() {{
        add("1.0");
    }};

    private final JGemsResourceManager jGemsResourceManager;
    private final SceneWorld sceneWorld;
    private final PhysicsWorld physicsWorld;

    private IGameMap currentLoadedMap;

    public JGemsMapping(SceneWorld sceneWorld, PhysicsWorld physicsWorld, JGemsResourceManager jGemsResourceManager) {
        this.jGemsResourceManager = jGemsResourceManager;
        this.sceneWorld = sceneWorld;
        this.physicsWorld = physicsWorld;

        this.currentLoadedMap = null;
    }

    private void createWorlds() {
        this.getPhysicsWorld().onWorldStart();
        this.getSceneWorld().onWorldStart();
    }

    private void destroyWorlds() {
        if (this.getPhysicsWorld() != null) {
            this.getPhysicsWorld().onWorldEnd();
        }
        if (this.getSceneWorld() != null) {
            this.getSceneWorld().onWorldEnd();
        }
    }

    public void destroyMap(IMapActionCallback... callbacks) {
        if (!this.isMapValid()) {
            Log.get().error("Couldn't destroy invalid map");
            return;
        }
        for (IMapActionCallback mapActionCallback : callbacks) {
            mapActionCallback.onDestroying(this.getCurrentLoadedMap(), this.getResourceManager());
        }
        this.currentLoadedMap = null;
        this.destroyWorlds();
    }
    
    public void loadMap(@NotNull IMapProcessor processor, IMapActionCallback... callbacks) {
        if (this.isMapValid()) {
            Log.get().error("Couldn't load map, while previous was not destroyed");
            return;
        }

        IPlayer player = null;
        final IEnvironment environment = this.getSceneWorld().getEnvironment();

        Log.get().info("Loading Map: " + processor.getMapName() + "(" + processor.getMapInformation() + ")");
        this.createWorlds();
        if (!JGemsAPI.executeScriptFunction(null, APIScriptingFunctions.onInitialization, JGemsAPI.getAPIScripting().createInitializationJS())) {
            JGemsAPIScriptingEngine.warn(APIScriptingFunctions.onInitialization);
        }
        processor.init();

        processor.setGlobalResources(this.getResourceManager().getGlobalResources());
        processor.setLocalResources(this.getResourceManager().getLocalResources());
        processor.onSetupSkyBox(environment.getSkyBox(), environment.getSkyBox().getBackground());
        processor.onSetupFog(environment.getFogManager());
        if (!JGemsAPI.executeScriptFunction(null, APIScriptingFunctions.onWorldPreGeneration, JGemsAPI.getAPIScripting().getGameWorldJS())) {
            JGemsAPIScriptingEngine.warn(APIScriptingFunctions.onWorldPreGeneration);
        }
        processor.preProcessing(this.getPhysicsWorld(), this.getSceneWorld());
        processor.onProcessing(this.getPhysicsWorld(), this.getSceneWorld());

        if (processor.getPlayerConstructor() != null) {
            Pair<@NotNull IPlayer, @Nullable EntityRenderData> pair = processor.getPlayerConstructor().constructPlayer(this.getPhysicsWorld());
            player = pair.getFirst();
            ((WorldItem) player).setStartTransformations(processor.getDefaultStartPosition(), processor.getDefaultStartRotation(), new Vector3f(1.0f));
            JGemsWorldHelper.addItemInWorld((WorldItem) player, pair.getSecond() == null ? JGemsResourceManager.globalRenderDataAssets.defaultPlayer : pair.getSecond());
            JGemsControllerHelper.attachControllerTo(JGemsControllerDispatcher.mouseKeyboardController, player);
            JGemsCameraHelper.enableAttachedCamera((WorldItem) player);
        } else {
            JGemsCameraHelper.enableFreeCamera(JGemsControllerHelper.getCurrentController(), processor.getDefaultStartPosition(), processor.getDefaultStartRotation());
        }
        processor.postProcessing(this.getPhysicsWorld(), this.getSceneWorld());
        if (!JGemsAPI.executeScriptFunction(null, APIScriptingFunctions.onWorldPostGeneration, JGemsAPI.getAPIScripting().getGameWorldJS())) {
            JGemsAPIScriptingEngine.warn(APIScriptingFunctions.onWorldPostGeneration);
        }

        this.buildInvisibleBorders(physicsWorld, JGems3D.MAP_MAX_SIZE);

        this.getResourceManager().writeResourcesDataCache();

        this.currentLoadedMap = new GameMap(player, processor.getMapName(), processor.getMapInformation());
        for (IMapActionCallback mapActionCallback : callbacks) {
            mapActionCallback.onLoaded(processor, this.getCurrentLoadedMap(), this.getResourceManager());
        }
        Log.get().info("Successfully loaded map: " + this.getCurrentLoadedMap().getName());
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

    public boolean isMapValid() {
        return this.getCurrentLoadedMap() != null;
    }

    public boolean isPlayerValid() {
        return this.isMapValid() && this.getCurrentLoadedMap().getCurrentPlayer() != null;
    }

    public @Nullable IPlayer getCurrentPlayer() {
        return this.isMapValid() ? this.getCurrentLoadedMap().getCurrentPlayer() : null;
    }

    public IGameMap getCurrentLoadedMap() {
        return this.currentLoadedMap;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public PhysicsWorld getPhysicsWorld() {
        return this.physicsWorld;
    }

    public JGemsResourceManager getResourceManager() {
        return this.jGemsResourceManager;
    }
}