package api.scripting.coding.env.internal.map.events;

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
import api.scripting.coding.env.internal.game.init.events.rendering.ui.JSRegisterUiEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.ui.JSUiBehaviourEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.world.*;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitAssetsEvent;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitShadersEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSAfterSettingsPerfTestEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSInitSettingsEvent;
import api.scripting.coding.env.internal.map.events.mapping.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.events.JSEventSubscriber;

@JSCodingClass(binding = "JSMapRegistry", description = "Main registry used to subscribe to engine events from scripts.")
public class JSMapRegistry {
    @JSHideFromDoc private final JSEventSubscriber eventSubscriber;

    @JSHideFromDoc
    public JSMapRegistry(JSEventSubscriber eventSubscriber) {
        this.eventSubscriber = eventSubscriber;
    }

    @JSCodingField(description = "Event triggered during conversion of a map prop to a SceneProp instance.")
    public static final JSMapPropConvertEvent MAP_PROP_CONVERT_EVENT = new JSMapPropConvertEvent();

    @JSCodingField(description = "Event triggered when a map entity is converted to a world item.")
    public static final JSMapEntityConvertEvent MAP_ENTITY_CONVERT_EVENT = new JSMapEntityConvertEvent();

    @JSCodingField(description = "Event triggered when a map marker is converted.")
    public static final JSMapMarkerConvertEvent MAP_MARKER_CONVERT_EVENT = new JSMapMarkerConvertEvent();

    @JSCodingField(description = "Event triggered during setup of sky and sun data in the scene.")
    public static final JSMapSkySetupEvent MAP_SKY_SETUP_EVENT = new JSMapSkySetupEvent();

    @JSCodingField(description = "Event triggered when fog is set up in the scene.")
    public static final JSMapFogSetupEvent MAP_FOG_SETUP_EVENT = new JSMapFogSetupEvent();

    @JSCodingField(description = "Event triggered when shadows is set up in the scene.")
    public static final JSMapShadowsSetupEvent MAP_SHADOW_SETUP_EVENT = new JSMapShadowsSetupEvent();

    @JSCodingField(description = "Event triggered when lighting is set up in the scene.")
    public static final JSMapLightingSetupEvent MAP_LIGHTING_SETUP_EVENT = new JSMapLightingSetupEvent();

    @JSCodingFunctionOrMethod(description = "Subscribe a script function to a specific engine event.", paramNames = {"eventToSubscribe", "jsFunctionName"})
    public void registerEvent(JSEventI eventToSubscribe, String jsFunctionName) {
        this.eventSubscriber.subscribeEvent(eventToSubscribe.name(), jsFunctionName);
    }

    /*
    new Pair<>(new JSSceneWorldLifecycleEvent(new JSSceneWorld(this), JSEventState.END), JavaToJsAPI.Target.Game)
     */
}
