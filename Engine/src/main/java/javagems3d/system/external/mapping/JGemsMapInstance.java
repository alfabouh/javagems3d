package javagems3d.system.external.mapping;

import api.events.EventBus;
import api.events.EventLauncher;
import api.scripting.JavaToJsAPI;
import api.scripting.coding.env.internal.map.events.mapping.JSPlayerConstructOnMapEvent;
import api.scripting.coding.env.internal.util.mapping.player.JSSpawnPlayerTranslateData;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Plane;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.*;
import javagems3d.system.external.mapping.processing.base.IMapProcessor;
import javagems3d.system.external.mapping.processing.callbacks.IMapActionCallback;
import javagems3d.physics.entities.bullet.wrappers.BulletBody;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class JGemsMapInstance {
    private final JGemsResourceManager jGemsResourceManager;
    private final SceneWorld sceneWorld;
    private final PhysicsWorld physicsWorld;
    private final OpenGLRenderer openGLRenderer;
    private IGameMap currentLoadedMap;

    public JGemsMapInstance(OpenGLRenderer openGLRenderer, SceneWorld sceneWorld, PhysicsWorld physicsWorld, JGemsResourceManager jGemsResourceManager) {
        this.openGLRenderer = openGLRenderer;
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
        this.getSceneWorld().getEnvironment().setEnvironmentDefaults();
        JavaToJsAPI.ScriptEnd(JavaToJsAPI.Target.Map);
        this.destroyWorlds();
        JGemsResourceManager.destroyDefaultMeshCubes();
        JGemsResourceManager.destroyDefaultMeshParticle();
    }
    
    public void loadMap(@NotNull IMapProcessor processor, IMapActionCallback... callbacks) {
        if (this.isMapValid()) {
            Log.get().error("Couldn't load map, while previous was not destroyed");
            return;
        }
        JGemsResourceManager.initDefaultMeshParticle();
        JGemsResourceManager.initDefaultMeshCubes();

        IPlayer player = null;
        final IEnvironment environment = this.getSceneWorld().getEnvironment();

        Log.get().info("Loading Map: " + processor.getMapName() + "(" + processor.getMapInformation() + ")");
        this.createWorlds();

        //EventLauncher.pushEvent(new EventBus.MapLoading(EventBus.Run.PRE, this.getPhysicsWorld(), this.getSceneWorld()));
        //if (!JGemsAPI.executeScriptFunction(null, APIScriptsListing.onInitialization, JGemsAPI.getAPIScripting().createInitializationJS())) {
        //    JGemsAPIScriptingEngine.warn(APIScriptsListing.onInitialization);
        //}
        JavaToJsAPI.Js_MAP_initEvents();
        processor.init();

        processor.setGlobalResources(this.getResourceManager().getGlobalResources());
        processor.setLocalResources(this.getResourceManager().getLocalResources());
        processor.onSetupSkyBox(environment.getSkyBox(), environment.getSkyBox().getBackground(), environment);
        processor.onSetupShadows(environment.getShadowScene(), environment);
        processor.onSetupLighting(environment.getLightScene(), environment);
        processor.onSetupFog(environment.getFogScene(), environment);
       //if (!JGemsAPI.executeScriptFunction(null, APIScriptsListing.onMapPreGeneration, JGemsAPI.getAPIScripting().getGameWorldJS())) {
       //    JGemsAPIScriptingEngine.warn(APIScriptsListing.onMapPreGeneration);
       //}
        processor.preProcessing(this.getPhysicsWorld(), this.getSceneWorld());
        processor.onProcessing(this.getPhysicsWorld(), this.getSceneWorld());
        environment.createEnvironment(this.openGLRenderer);

        boolean flag = false;
        final IGameMap.IPlayerConstructor playerConstructor = processor.getPlayerConstructor(this.getPhysicsWorld(), this.getSceneWorld());
        if (playerConstructor != null && (processor.getSpawnPlayersSet() != null)) {
            Pair<@NotNull IPlayer, @Nullable EntityRenderData> pair = playerConstructor.constructPlayer(this.getPhysicsWorld(), processor.getSpawnPlayersSet());
            if (pair != null) {
                player = pair.first();
                EntityRenderData renderData = pair.second() != null ? pair.second() : JGemsResourceManager.globalRenderDataAssets.defaultPlayer;

                EventBus.PlayerConstructOnMapEvent event = new EventBus.PlayerConstructOnMapEvent(processor.getMapName(), physicsWorld, processor.getSpawnPlayersSet());
                JSPlayerConstructOnMapEvent playerConstructOnMapEventJS = new JSPlayerConstructOnMapEvent(new JSPhysicsWorld(physicsWorld), processor.getSpawnPlayersSet() == null ? new ArrayList<>() : processor.getSpawnPlayersSet().stream().map(e -> new JSSpawnPlayerTranslateData(new JSVector3f(e.spawnPos()), new JSVector3f(e.spawnRot()))).collect(Collectors.toSet()));
                EventLauncher.pushEvent(event, new Pair<>(playerConstructOnMapEventJS, JavaToJsAPI.Target.Map));

                if (event.newPlayerResult != null) {
                    player = event.newPlayerResult;
                } else {
                    if (playerConstructOnMapEventJS.getPlayer() != null) {
                        player = playerConstructOnMapEventJS.getPlayer().getJavaPlayer();
                    }
                }

                if (event.renderData != null) {
                    renderData = event.renderData;
                } else {
                    if (playerConstructOnMapEventJS.getRenderData() != null) {
                        renderData = playerConstructOnMapEventJS.getRenderData().getJavaEntityRenderData();
                    }
                }

                JGemsHelper.world().addWorldItem((WorldItem) player, renderData);
                JGemsHelper.controller().attachControllerTo(JGemsHelper.controller().getControllerDispatcher().getCurrentController(), player);
                JGemsHelper.camera().enableAttachedCamera((WorldItem) (player));
                flag = true;
            }
        }
        if (!flag) {
            JGemsHelper.camera().enableFreeCamera(JGemsHelper.controller().getCurrentController(), processor.getDefaultStartPosition(), processor.getDefaultStartRotation());
        }
        processor.postProcessing(this.getPhysicsWorld(), this.getSceneWorld());
        //if (!JGemsAPI.executeScriptFunction(null, APIScriptsListing.onMapPostGeneration, JGemsAPI.getAPIScripting().getGameWorldJS())) {
        //    JGemsAPIScriptingEngine.warn(APIScriptsListing.onMapPostGeneration);
        //}

        this.buildInvisibleBorders(physicsWorld, JGems3D.MAP_MAX_SIZE);
        this.getResourceManager().writeResourcesDataCache();
        JGems3D.get().getScreen().refreshSceneResources();
        this.currentLoadedMap = new GameMap(player, processor.getMapName(), processor.getMapInformation());
        for (IMapActionCallback mapActionCallback : callbacks) {
            mapActionCallback.onLoaded(processor, this.getCurrentLoadedMap(), this.getResourceManager());
        }
        //EventLauncher.pushEvent(new EventBus.MapLoading(EventBus.Run.POST, this.getPhysicsWorld(), this.getSceneWorld()));
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

        physicsWorld.addObject(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape1, 0), "border_wall1"));
        physicsWorld.addObject(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape2, 0), "border_wall2"));
        physicsWorld.addObject(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape3, 0), "border_wall3"));
        physicsWorld.addObject(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape4, 0), "border_wall4"));
        physicsWorld.addObject(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape5, 0), "border_wall5"));
        physicsWorld.addObject(new BulletBody(physicsWorld, new PhysicsRigidBody(planeShape6, 0), "border_wall6"));
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