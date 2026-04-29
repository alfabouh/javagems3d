package api.scripting.coding.env.internal.game.init;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.game.init.events.camera.JSSceneCameraEvent;
import api.scripting.coding.env.internal.game.init.events.physics.JSPhysicsWorldClearEvent;
import api.scripting.coding.env.internal.game.init.events.physics.JSPhysicsWorldObjectAddEvent;
import api.scripting.coding.env.internal.game.init.events.physics.JSPhysicsWorldStateEvent;
import api.scripting.coding.env.internal.game.init.events.physics.JSPhysicsWorldUpdateEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.JSRenderIMGUIEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.JSRenderUIEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.environment.JSCreateRenderEnvironmentEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.environment.JSDestroyRenderEnvironmentEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.environment.JSUpdateRenderEnvironmentEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.ogl.JSInitRendererOGLEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.ogl.JSRenderOGLNodeEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.ogl.JSRenderOGLSceneEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.ogl.JSStopRendererOGLEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.world.*;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitAssetsEvent;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitShadersEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSAfterSettingsPerfTestEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSInitSettingsEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.ui.JSRegisterUiEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.ui.JSUiBehaviourEvent;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.events.JSEventSubscriber;

@JSCodingClass(binding = "JSGameRegistry", description = "Main registry used to subscribe to engine events from scripts.")
public class JSGameRegistry {
    @JSHideFromDoc private final JSEventSubscriber eventSubscriber;

    @JSHideFromDoc
    public JSGameRegistry(JSEventSubscriber eventSubscriber) {
        this.eventSubscriber = eventSubscriber;
    }

    @JSCodingField(description = "Event triggered during asset initialization (textures, models, sounds, etc).")
    public static final JSInitAssetsEvent INIT_ASSETS = new JSInitAssetsEvent();

    @JSCodingField(description = "Event triggered during shader initialization.")
    public static final JSInitShadersEvent INIT_SHADERS = new JSInitShadersEvent();

    @JSCodingField(description = "Event for registering UI panels.")
    public static final JSRegisterUiEvent REGISTER_UI = new JSRegisterUiEvent();

    @JSCodingField(description = "Event for assigning UI behaviour (draw, construct, input, etc).")
    public static final JSUiBehaviourEvent REGISTER_UI_BEHAVIOUR = new JSUiBehaviourEvent();

    @JSCodingField(description = "Event triggered during settings initialization.")
    public static final JSInitSettingsEvent INIT_SETTINGS = new JSInitSettingsEvent();

    @JSCodingField(description = "Event triggered after settings performance test is completed.")
    public static final JSAfterSettingsPerfTestEvent AFTER_SETTINGS_PERF_TEST_EVENT = new JSAfterSettingsPerfTestEvent();

    @JSCodingField(description = "...")
    public static final JSRenderUIEvent UI_RENDER_EVENT = new JSRenderUIEvent();

    @JSCodingField(description = "...")
    public static final JSRenderIMGUIEvent IMGUI_RENDER_EVENT = new JSRenderIMGUIEvent();

    @JSCodingField(description = "...")
    public static final JSSceneWorldLifecycleEvent SCENE_WORLD_LIFECYCLE_EVENT = new JSSceneWorldLifecycleEvent();

    @JSCodingField(description = "Event triggered during SceneWorld update.")
    public static final JSSceneWorldUpdateEvent SCENE_WORLD_UPDATE_EVENT = new JSSceneWorldUpdateEvent();

    @JSCodingField(description = "Event triggered during SceneWorld objects update.")
    public static final JSSceneWorldObjectsUpdateEvent SCENE_WORLD_OBJECTS_UPDATE_EVENT = new JSSceneWorldObjectsUpdateEvent();

    @JSCodingField(description = "Event triggered when a liquid in the scene is destroyed.")
    public static final JSSceneLiquidDestroyEvent SCENE_LIQUID_DESTROY_EVENT = new JSSceneLiquidDestroyEvent();

    @JSCodingField(description = "Event triggered when a scene object is destroyed.")
    public static final JSSceneObjectDestroyEvent SCENE_OBJECT_DESTROY_EVENT = new JSSceneObjectDestroyEvent();

    @JSCodingField(description = "Event triggered when a scene object is updated.")
    public static final JSSceneObjectUpdateEvent SCENE_OBJECT_UPDATE_EVENT = new JSSceneObjectUpdateEvent();

    @JSCodingField(description = "Event triggered when a light in the scene is destroyed.")
    public static final JSSceneLightDestroyEvent SCENE_LIGHT_DESTROY_EVENT = new JSSceneLightDestroyEvent();

    @JSCodingField(description = "Event triggered when a light is spawned in the scene.")
    public static final JSSceneLightSpawnEvent SCENE_LIGHT_SPAWN_EVENT = new JSSceneLightSpawnEvent();

    @JSCodingField(description = "Event triggered when a scene object is spawned.")
    public static final JSSceneObjectSpawnEvent SCENE_OBJECT_SPAWN_EVENT = new JSSceneObjectSpawnEvent();

    @JSCodingField(description = "Event triggered for camera updates in the scene.")
    public static final JSSceneCameraEvent SCENE_CAMERA_EVENT = new JSSceneCameraEvent();

    @JSCodingField(description = "Event triggered when a liquid is spawned in the scene.")
    public static final JSSceneLiquidSpawnEvent SCENE_LIQUID_SPAWN_EVENT = new JSSceneLiquidSpawnEvent();

    @JSCodingField(description = "Event triggered when a render environment is created.")
    public static final JSCreateRenderEnvironmentEvent CREATE_RENDER_ENVIRONMENT_EVENT = new JSCreateRenderEnvironmentEvent();

    @JSCodingField(description = "Event triggered when a render environment is destroyed.")
    public static final JSDestroyRenderEnvironmentEvent DESTROY_RENDER_ENVIRONMENT_EVENT = new JSDestroyRenderEnvironmentEvent();

    @JSCodingField(description = "Event triggered when a render environment is updated.")
    public static final JSUpdateRenderEnvironmentEvent UPDATE_RENDER_ENVIRONMENT_EVENT = new JSUpdateRenderEnvironmentEvent();

    @JSCodingField(description = "Event triggered when the OpenGL renderer is initialized.")
    public static final JSInitRendererOGLEvent INIT_RENDERER_OPENGL_EVENT = new JSInitRendererOGLEvent();

    @JSCodingField(description = "Event triggered during OpenGL node rendering.")
    public static final JSRenderOGLNodeEvent RENDER_OGL_NODE_EVENT = new JSRenderOGLNodeEvent();

    @JSCodingField(description = "Event triggered when OpenGL renderer processes the scene.")
    public static final JSRenderOGLSceneEvent RENDER_OGL_SCENE_EVENT = new JSRenderOGLSceneEvent();

    @JSCodingField(description = "Event triggered when the OpenGL renderer is stopped.")
    public static final JSStopRendererOGLEvent STOP_RENDERER_OGL_EVENT = new JSStopRendererOGLEvent();

    @JSCodingField(description = "Event triggered when the PhysicsWorld changes state (START or END).")
    public static final JSPhysicsWorldStateEvent PHYSICS_WORLD_STATE_EVENT = new JSPhysicsWorldStateEvent();

    @JSCodingField(description = "Event triggered when the PhysicsWorld is updated (PRE or POST).")
    public static final JSPhysicsWorldUpdateEvent PHYSICS_WORLD_UPDATE_EVENT = new JSPhysicsWorldUpdateEvent();

    @JSCodingField(description = "Event triggered when the PhysicsWorld is cleared.")
    public static final JSPhysicsWorldClearEvent PHYSICS_WORLD_CLEAR_EVENT = new JSPhysicsWorldClearEvent();

    @JSCodingField(description = "Event triggered when a new object is added to the PhysicsWorld.")
    public static final JSPhysicsWorldObjectAddEvent PHYSICS_WORLD_OBJECT_ADD_EVENT = new JSPhysicsWorldObjectAddEvent();

    @JSCodingFunctionOrMethod(description = "Subscribe a script function to a specific engine event.", paramNames = {"eventToSubscribe", "jsFunctionName"})
    public void registerEvent(JSEventI eventToSubscribe, String jsFunctionName) {
        this.eventSubscriber.subscribeEvent(eventToSubscribe.name(), jsFunctionName);
    }

    /*
    new Pair<>(new JSSceneWorldLifecycleEvent(new JSSceneWorld(this), JSEventState.END), JavaToJsAPI.Target.Game)
     */
}
