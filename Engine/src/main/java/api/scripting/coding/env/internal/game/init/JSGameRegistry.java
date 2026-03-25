package api.scripting.coding.env.internal.game.init;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitAssetsEvent;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitShadersEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSAfterSettingsPerfTestEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSInitSettingsEvent;
import api.scripting.coding.env.internal.game.init.events.ui.JSRegisterUiEvent;
import api.scripting.coding.env.internal.game.init.events.ui.JSUiBehaviourEvent;
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

    @JSCodingFunctionOrMethod(description = "Subscribe a script function to a specific engine event.", paramNames = {"eventToSubscribe", "jsFunctionName"})
    public void registerEvent(JSEventI eventToSubscribe, String jsFunctionName) {
        this.eventSubscriber.subscribeEvent(eventToSubscribe.name(), jsFunctionName);
    }
}
