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

@JSCodingClass(binding = "JSGameRegistry", description = "...")
public class JSGameRegistry {
    @JSHideFromDoc private final JSEventSubscriber eventSubscriber;

    @JSHideFromDoc
    public JSGameRegistry(JSEventSubscriber eventSubscriber) {
        this.eventSubscriber = eventSubscriber;
    }

    @JSCodingField(description = "...") public static final JSInitAssetsEvent INIT_ASSETS = new JSInitAssetsEvent();
    @JSCodingField(description = "...") public static final JSInitShadersEvent INIT_SHADERS = new JSInitShadersEvent();
    @JSCodingField(description = "...") public static final JSRegisterUiEvent REGISTER_UI = new JSRegisterUiEvent();
    @JSCodingField(description = "...") public static final JSUiBehaviourEvent REGISTER_UI_BEHAVIOUR = new JSUiBehaviourEvent();
    @JSCodingField(description = "...") public static final JSInitSettingsEvent INIT_SETTINGS = new JSInitSettingsEvent();
    @JSCodingField(description = "...") public static final JSAfterSettingsPerfTestEvent AFTER_SETTINGS_PERF_TEST_EVENT = new JSAfterSettingsPerfTestEvent();

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"eventToSubscribe", "jsFunctionName"})
    public void registerEvent(JSEventI eventToSubscribe, String jsFunctionName) {
        this.eventSubscriber.subscribeEvent(eventToSubscribe.name(), jsFunctionName);
    }
}
