package api.events;

import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSTriggerAction;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneWorldLiquid;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldLiquid;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.triggers.IHasCollisionTrigger;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.physics.world.triggers.liquids.base.Liquid;
import javagems3d.system.controller.base.IController;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.data.MapObjectsDataPack;
import javagems3d.system.external.mapping.data.items.*;
import javagems3d.system.external.mapping.data.templates.RowMapObjectData;
import javagems3d.system.service.collections.Pair;

import java.util.Collection;
import java.util.Set;

public abstract class EventBus {
    public enum ObjectState {
        SPAWN,
        DESTROY
    }

    public enum State {
        START,
        END
    }

    public enum Run {
        PRE,
        POST
    }

    public interface IEvent {
        default boolean canBeCancelled() {
            return this instanceof Cancellable;
        }

        @SuppressWarnings("all")
        default boolean isCancelled() {
            return this.canBeCancelled() && ((Cancellable) this).isCancelled();
        }

        class EmptyEvent implements IEvent {
        }
    }

    public static abstract class Cancellable {
        private boolean isCancelled;

        public Cancellable() {
            this.isCancelled = false;
        }

        public boolean isCancelled() {
            return this.isCancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.isCancelled = cancelled;
        }
    }


    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // NEW

    public static final class CollisionTriggered extends Cancellable implements IEvent {
        public final IHasCollisionTrigger object;
        public final ITriggerAction triggerAction;

        public CollisionTriggered(IHasCollisionTrigger object, ITriggerAction triggerAction) {
            this.object = object;
            this.triggerAction = triggerAction;
        }

        public IHasCollisionTrigger getObject() {
            return this.object;
        }

        public ITriggerAction getTriggerAction() {
            return this.triggerAction;
        }
    }

    public static final class RenderUIEvent extends Cancellable implements IEvent {
        public final JGemsUI jGemsUI;
        public final FrameTicking frameTicking;

        public RenderUIEvent(JGemsUI jGemsUI, FrameTicking frameTicking) {
            this.jGemsUI = jGemsUI;
            this.frameTicking = frameTicking;
        }

        public JGemsUI getjGemsUI() {
            return this.jGemsUI;
        }

        public FrameTicking getFrameTicking() {
            return this.frameTicking;
        }
    }

    public static final class RenderIMGUIEvent implements IEvent {
        public final DearUIInterface dearUIInterface;
        public final IController controller;
        public final FrameTicking frameTicking;

        public RenderIMGUIEvent(DearUIInterface dearUIInterface, IController controller, FrameTicking frameTicking) {
            this.dearUIInterface = dearUIInterface;
            this.controller = controller;
            this.frameTicking = frameTicking;
        }

        public DearUIInterface getDearUIInterface() {
            return this.dearUIInterface;
        }

        public IController getController() {
            return this.controller;
        }

        public FrameTicking getFrameTicking() {
            return this.frameTicking;
        }
    }

    public static final class InitRendererOGLEvent implements IEvent {
        private final JGemsOpenGLRenderer jGemsOpenGLRenderer;

        public InitRendererOGLEvent(JGemsOpenGLRenderer jGemsOpenGLRenderer) {
            this.jGemsOpenGLRenderer = jGemsOpenGLRenderer;
        }
    }

    public static final class StopRendererOGLEvent implements IEvent {
        private final JGemsOpenGLRenderer jGemsOpenGLRenderer;

        public StopRendererOGLEvent(JGemsOpenGLRenderer jGemsOpenGLRenderer) {
            this.jGemsOpenGLRenderer = jGemsOpenGLRenderer;
        }

        public JGemsOpenGLRenderer getjGemsOpenGLRenderer() {
            return this.jGemsOpenGLRenderer;
        }
    }

    public static final class RenderOGLNodeEvent extends Cancellable implements IEvent {
        private final JGemsOpenGLRenderer jGemsOpenGLRenderer;
        public final IRenderNode renderNode;
        private final FrameTicking frameTicking;
        private final Run run;

        public RenderOGLNodeEvent(JGemsOpenGLRenderer jGemsOpenGLRenderer, IRenderNode renderNode, FrameTicking frameTicking, Run run) {
            this.jGemsOpenGLRenderer = jGemsOpenGLRenderer;
            this.renderNode = renderNode;
            this.frameTicking = frameTicking;
            this.run = run;
        }

        public JGemsOpenGLRenderer getjGemsOpenGLRenderer() {
            return this.jGemsOpenGLRenderer;
        }

        public IRenderNode getRenderNode() {
            return this.renderNode;
        }

        public FrameTicking getFrameTicking() {
            return this.frameTicking;
        }

        public Run getRun() {
            return this.run;
        }
    }

    public static final class RenderOGLSceneEvent extends Cancellable implements IEvent {
        private final JGemsOpenGLRenderer jGemsOpenGLRenderer;
        private final FrameTicking frameTicking;
        private final Run run;
        private final Set<SceneObject> toRenderObjects;
        private final Set<SceneWorldLiquid> toRenderLiquids;

        public RenderOGLSceneEvent(JGemsOpenGLRenderer jGemsOpenGLRenderer, FrameTicking frameTicking, Run run, Set<SceneObject> toRenderObjects, Set<SceneWorldLiquid> toRenderLiquids) {
            this.jGemsOpenGLRenderer = jGemsOpenGLRenderer;
            this.frameTicking = frameTicking;
            this.run = run;
            this.toRenderObjects = toRenderObjects;
            this.toRenderLiquids = toRenderLiquids;
        }

        public JGemsOpenGLRenderer getjGemsOpenGLRenderer() {
            return this.jGemsOpenGLRenderer;
        }

        public FrameTicking getFrameTicking() {
            return this.frameTicking;
        }

        public Run getRun() {
            return this.run;
        }

        public Set<SceneObject> getToRenderObjects() {
            return this.toRenderObjects;
        }

        public Set<SceneWorldLiquid> getToRenderLiquids() {
            return this.toRenderLiquids;
        }
    }

    public static final class UpdateRenderEnvironment implements IEvent {
        private final JGemsEnvironment environment;
        private final ICamera camera;
        private final Run run;

        public UpdateRenderEnvironment(JGemsEnvironment environment, ICamera camera, Run run) {
            this.environment = environment;
            this.camera = camera;
            this.run = run;
        }

        public JGemsEnvironment getEnvironment() {
            return this.environment;
        }

        public ICamera getCamera() {
            return this.camera;
        }

        public Run getRun() {
            return this.run;
        }
    }

    public static final class CreateRenderEnvironment implements IEvent {
        private final JGemsEnvironment environment;
        private final JGemsOpenGLRenderer jGemsOpenGLRenderer;

        public CreateRenderEnvironment(JGemsEnvironment environment, JGemsOpenGLRenderer jGemsOpenGLRenderer) {
            this.environment = environment;
            this.jGemsOpenGLRenderer = jGemsOpenGLRenderer;
        }

        public JGemsEnvironment getEnvironment() {
            return this.environment;
        }

        public JGemsOpenGLRenderer getjGemsOpenGLRenderer() {
            return this.jGemsOpenGLRenderer;
        }
    }

    public static final class DestroyRenderEnvironment implements IEvent {
        private final JGemsEnvironment environment;

        public DestroyRenderEnvironment(JGemsEnvironment environment) {
            this.environment = environment;
        }

        public JGemsEnvironment getEnvironment() {
            return this.environment;
        }
    }

    public static final class SceneWorldLifecycleEvent implements IEvent {
        private final SceneWorld world;
        private final State state;

        public SceneWorldLifecycleEvent(SceneWorld world, State state) {
            this.world = world;
            this.state = state;
        }

        public SceneWorld getWorld() { return world; }
        public State getState() { return state; }
    }

    public static final class SceneWorldUpdateEvent extends Cancellable implements IEvent {
        private final SceneWorld world;
        private final Run run;
        private final int tick;

        public SceneWorldUpdateEvent(SceneWorld world, Run run, int tick) {
            this.world = world;
            this.run = run;
            this.tick = tick;
        }

        public SceneWorld getWorld() {
            return this.world;
        }

        public Run getRun() {
            return this.run;
        }

        public int getTick() {
            return this.tick;
        }
    }

    public static final class SceneObjectSpawnEvent extends Cancellable implements IEvent {
        private final SceneWorld world;
        private final SceneObject object;
        private final Object renderData;

        public SceneObjectSpawnEvent(SceneWorld world, SceneObject object, Object renderData) {
            this.world = world;
            this.object = object;
            this.renderData = renderData;
        }

        public SceneWorld getWorld() { return this.world; }
        public SceneObject getObject() { return this.object; }
        public Object getRenderData() { return this.renderData; }
    }

    public static final class SceneObjectDestroyEvent extends Cancellable implements IEvent {
        private final SceneWorld world;
        private final SceneObject object;

        public SceneObjectDestroyEvent(SceneWorld world, SceneObject object) {
            this.world = world;
            this.object = object;
        }

        public SceneWorld getWorld() { return this.world; }
        public SceneObject getObject() { return this.object; }
    }

    public static final class SceneObjectUpdateEvent implements IEvent {
        private final SceneWorld world;
        private final SceneObject object;

        public SceneObjectUpdateEvent(SceneWorld world, SceneObject object) {
            this.world = world;
            this.object = object;
        }

        public SceneWorld getWorld() { return world; }
        public SceneObject getObject() { return object; }
    }

    public static final class SceneLiquidSpawnEvent extends Cancellable implements IEvent {
        private final SceneWorld world;
        private final Liquid liquid;
        private final LiquidRenderData renderData;

        public SceneLiquidSpawnEvent(SceneWorld world, Liquid liquid, LiquidRenderData renderData) {
            this.world = world;
            this.liquid = liquid;
            this.renderData = renderData;
        }

        public SceneWorld getWorld() { return this.world; }
        public Liquid getLiquid() { return this.liquid; }
        public LiquidRenderData getRenderData() { return this.renderData; }
    }

    public static final class SceneLiquidDestroyEvent extends Cancellable implements IEvent {
        private final SceneWorld world;
        private final SceneWorldLiquid liquid;

        public SceneLiquidDestroyEvent(SceneWorld world, SceneWorldLiquid liquid) {
            this.world = world;
            this.liquid = liquid;
        }

        public SceneWorld getWorld() { return this.world; }
        public SceneWorldLiquid getLiquid() { return this.liquid; }
    }

    public static final class SceneLightSpawnEvent extends Cancellable implements IEvent {
        private final SceneWorld world;
        private final Light light;

        public SceneLightSpawnEvent(SceneWorld world, Light light) {
            this.world = world;
            this.light = light;
        }

        public SceneWorld getWorld() { return this.world; }
        public Light getLight() { return this.light; }
    }

    public static final class SceneLightDestroyEvent extends Cancellable implements IEvent {
        private final SceneWorld world;
        private final Light light;

        public SceneLightDestroyEvent(SceneWorld world, Light light) {
            this.world = world;
            this.light = light;
        }

        public SceneWorld getWorld() { return this.world; }
        public Light getLight() { return this.light; }
    }

    public static final class SceneCameraEvent implements IEvent {
        private final SceneWorld world;
        private final ICamera camera;

        public SceneCameraEvent(SceneWorld world, ICamera camera) {
            this.world = world;
            this.camera = camera;
        }

        public SceneWorld getWorld() {
            return this.world;
        }

        public ICamera getCamera() {
            return this.camera;
        }
    }

    public static final class SceneWorldObjectsUpdateEvent extends Cancellable implements IEvent {
        private final SceneWorld world;
        private final boolean refresh;
        private final FrameTicking frameTicking;
        private final Run run;

        public SceneWorldObjectsUpdateEvent(SceneWorld world, boolean refresh, FrameTicking frameTicking, Run run) {
            this.world = world;
            this.refresh = refresh;
            this.frameTicking = frameTicking;
            this.run = run;
        }

        public SceneWorld getWorld() {
            return this.world;
        }

        public boolean isRefresh() {
            return this.refresh;
        }

        public FrameTicking getFrameTicking() {
            return this.frameTicking;
        }

        public Run getRun() {
            return this.run;
        }
    }

    public interface IMapConvertEvent extends IEvent {
        SceneWorld getSceneWorld();
        PhysicsWorld getPhysicsWorld();
        RowMapObjectData getTemplate();
    }

    public static final class MapPropConvertEvent extends Cancellable implements IMapConvertEvent {
        private final SceneWorld sceneWorld;
        private final PhysicsWorld physicsWorld;
        private final RowMapObjectData template;
        private final JGemsPropData propData;
        private final boolean background;
        private SceneProp result;

        public MapPropConvertEvent(boolean background, SceneWorld sceneWorld, PhysicsWorld physicsWorld, RowMapObjectData template, JGemsPropData propData) {
            this.background = background;
            this.sceneWorld = sceneWorld;
            this.physicsWorld = physicsWorld;
            this.template = template;
            this.propData = propData;
        }

        public boolean isBackground() {
            return this.background;
        }

        public SceneWorld getSceneWorld() { return this.sceneWorld; }
        public PhysicsWorld getPhysicsWorld() { return this.physicsWorld; }
        public RowMapObjectData getTemplate() { return this.template; }
        public JGemsPropData getPropData() { return this.propData; }

        public SceneProp getResult() { return this.result; }
        public void setResult(SceneProp result) { this.result = result; }
    }

    public static final class MapEntityConvertEvent extends Cancellable implements IMapConvertEvent {
        private final SceneWorld sceneWorld;
        private final PhysicsWorld physicsWorld;
        private final RowMapObjectData template;
        private final JGemsEntityData entityData;
        private WorldItem result;

        public MapEntityConvertEvent(SceneWorld sceneWorld, PhysicsWorld physicsWorld, RowMapObjectData template, JGemsEntityData entityData) {
            this.sceneWorld = sceneWorld;
            this.physicsWorld = physicsWorld;
            this.template = template;
            this.entityData = entityData;
        }

        public SceneWorld getSceneWorld() { return this.sceneWorld; }
        public PhysicsWorld getPhysicsWorld() { return this.physicsWorld; }
        public RowMapObjectData getTemplate() { return this.template; }
        public JGemsEntityData getEntityData() { return this.entityData; }

        public WorldItem getResult() { return this.result; }
        public void setResult(WorldItem result) { this.result = result; }
    }

    public static final class MapMarkerConvertEvent extends Cancellable implements IMapConvertEvent {
        private final SceneWorld sceneWorld;
        private final PhysicsWorld physicsWorld;
        private final RowMapObjectData template;
        private final JGemsMarkerData markerData;

        public MapMarkerConvertEvent(SceneWorld sceneWorld, PhysicsWorld physicsWorld, RowMapObjectData template, JGemsMarkerData markerData) {
            this.sceneWorld = sceneWorld;
            this.physicsWorld = physicsWorld;
            this.template = template;
            this.markerData = markerData;
        }

        public SceneWorld getSceneWorld() { return this.sceneWorld; }
        public PhysicsWorld getPhysicsWorld() { return this.physicsWorld; }
        public RowMapObjectData getTemplate() { return this.template; }
        public JGemsMarkerData getMarkerData() { return this.markerData; }
    }

    public static final class MapPointLightConvertEvent extends Cancellable implements IMapConvertEvent {
        private final SceneWorld sceneWorld;
        private final PhysicsWorld physicsWorld;
        private final RowMapObjectData template;
        private Pair<PointLight, Integer> result;

        public MapPointLightConvertEvent(SceneWorld sceneWorld, PhysicsWorld physicsWorld, RowMapObjectData template) {
            this.sceneWorld = sceneWorld;
            this.physicsWorld = physicsWorld;
            this.template = template;
        }

        public SceneWorld getSceneWorld() { return this.sceneWorld; }
        public PhysicsWorld getPhysicsWorld() { return this.physicsWorld; }
        public RowMapObjectData getTemplate() { return this.template; }

        public Pair<PointLight, Integer> getResult() { return this.result; }
        public void setResult(Pair<PointLight, Integer> result) { this.result = result; }
    }

    public static final class MapSkySetupEvent extends Cancellable implements IEvent {
        private final SceneWorld sceneWorld;
        private final ISkyBox skyBox;
        private final ISkyBackground background;
        private final SunData sunData;
        private final SkyData skyData;

        public MapSkySetupEvent(SceneWorld sceneWorld, ISkyBox skyBox, ISkyBackground background, SunData sunData, SkyData skyData) {
            this.sceneWorld = sceneWorld;
            this.skyBox = skyBox;
            this.background = background;
            this.sunData = sunData;
            this.skyData = skyData;
        }

        public SceneWorld getSceneWorld() { return this.sceneWorld; }
        public ISkyBox getSkyBox() { return this.skyBox; }
        public ISkyBackground getBackground() { return this.background; }
        public SunData getSunData() { return this.sunData; }
        public SkyData getSkyData() { return this.skyData; }
    }

    public static final class MapFogSetupEvent extends Cancellable implements IEvent {
        private final IFogScene fogScene;
        private final FogData fogData;

        public MapFogSetupEvent(IFogScene fogScene, FogData fogData) {
            this.fogScene = fogScene;
            this.fogData = fogData;
        }

        public IFogScene getFogScene() { return this.fogScene; }
        public FogData getFogData() { return this.fogData; }
    }

    public static final class MapShadowsSetupEvent extends Cancellable implements IEvent {
        private final IShadowScene shadowScene;
        private final ShadowsData shadowsData;

        public MapShadowsSetupEvent(IShadowScene shadowScene, ShadowsData shadowsData) {
            this.shadowScene = shadowScene;
            this.shadowsData = shadowsData;
        }

        public IShadowScene getShadowScene() { return this.shadowScene; }
        public ShadowsData getShadowsData() { return this.shadowsData; }
    }

    public static final class MapLightingSetupEvent extends Cancellable implements IEvent {
        private final ILightScene lightScene;
        private final LightingData shadowsData;

        public MapLightingSetupEvent(ILightScene lightScene, LightingData shadowsData) {
            this.lightScene = lightScene;
            this.shadowsData = shadowsData;
        }

        public ILightScene getLightScene() {
            return this.lightScene;
        }

        public LightingData getShadowsData() {
            return this.shadowsData;
        }
    }

    public static final class MapProcessingEvent extends Cancellable implements IEvent {
        private final PhysicsWorld physicsWorld;
        private final SceneWorld sceneWorld;
        private final MapObjectsDataPack dataPack;
        private final Run run;

        public MapProcessingEvent(PhysicsWorld physicsWorld, SceneWorld sceneWorld, MapObjectsDataPack dataPack, Run run) {
            this.physicsWorld = physicsWorld;
            this.sceneWorld = sceneWorld;
            this.dataPack = dataPack;
            this.run = run;
        }

        public PhysicsWorld getPhysicsWorld() { return this.physicsWorld; }
        public SceneWorld getSceneWorld() { return this.sceneWorld; }
        public MapObjectsDataPack getDataPack() { return this.dataPack; }
        public Run getRun() { return this.run; }
    }

    public static final class PlayerConstructOnMapEvent extends Cancellable implements IEvent {
        private final PhysicsWorld world;
        private final Collection<IGameMap.SpawnPlayerData> spawnDataList;
        public IPlayer player;
        public EntityRenderData renderData;

        public PlayerConstructOnMapEvent(PhysicsWorld world, Collection<IGameMap.SpawnPlayerData> spawnDataList) {
            this.world = world;
            this.spawnDataList = spawnDataList;
        }

        public PhysicsWorld getWorld() {
            return this.world;
        }

        public Collection<IGameMap.SpawnPlayerData> getSpawnDataList() {
            return this.spawnDataList;
        }

        public void setPlayer(IPlayer player) {
            this.player = player;
        }

        public void setRenderData(EntityRenderData renderData) {
            this.renderData = renderData;
        }
    }

    public static final class PhysicsWorldStateEvent extends Cancellable implements IEvent {
        private final PhysicsWorld world;
        private final State state;

        public PhysicsWorldStateEvent(State state, PhysicsWorld world) {
            this.state = state;
            this.world = world;
        }

        public PhysicsWorld getWorld() { return this.world; }
        public State getState() { return this.state; }
    }

    public static final class PhysicsWorldUpdateEvent extends Cancellable implements IEvent {
        private final PhysicsWorld world;
        private final Run run;

        public PhysicsWorldUpdateEvent(Run run, PhysicsWorld world) {
            this.run = run;
            this.world = world;
        }

        public PhysicsWorld getWorld() { return this.world; }
        public Run getRun() { return this.run; }
    }

    public static final class PhysicsWorldObjectAddEvent extends Cancellable implements IEvent {
        private final PhysicsWorld world;
        private final IWorldObject object;

        public PhysicsWorldObjectAddEvent(PhysicsWorld world, IWorldObject object) {
            this.world = world;
            this.object = object;
        }

        public PhysicsWorld getWorld() { return this.world; }
        public IWorldObject getObject() { return this.object; }
    }

    public static final class PhysicsWorldObjectRemoveEvent extends Cancellable implements IEvent {
        private final PhysicsWorld world;
        private final IWorldObject object;

        public PhysicsWorldObjectRemoveEvent(PhysicsWorld world, IWorldObject object) {
            this.world = world;
            this.object = object;
        }

        public PhysicsWorld getWorld() { return this.world; }
        public IWorldObject getObject() { return this.object; }
    }

    public static final class PhysicsWorldClearEvent extends Cancellable implements IEvent {
        private final PhysicsWorld world;

        public PhysicsWorldClearEvent(PhysicsWorld world) {
            this.world = world;
        }

        public PhysicsWorld getWorld() { return this.world; }
    }
}
/*
EventLauncher.pushEvent(new EventBus.RenderOGLSceneEvent(this, frameTicking, EventBus.Run.POST, toRenderObjects, toRenderLiquids), TODO);

    public static final class Class123 implements IEvent {

    }
 */