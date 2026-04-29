package api.scripting.coding.env.internal.game.init.events.settings;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.settings.JSGameSettings;
import api.scripting.coding.env.internal.util.settings.JSPerfTestResult;
import api.scripting.coding.env.internal.util.settings.instances.JSSettingI;

@JSCodingClass(binding = "JSAfterSettingsPerfTestEvent", description = "Event triggered after settings performance test is completed, providing access to results and game settings.")
public class JSAfterSettingsPerfTestEvent implements JSEventI {
    @JSHideFromDoc private JSGameSettings jsGameSettings;
    @JSHideFromDoc private JSPerfTestResult jsPerfTestResult;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSAfterSettingsPerfTestEvent() {
    }

    @JSHideFromDoc
    public JSAfterSettingsPerfTestEvent(JSPerfTestResult jsPerfTestResult, JSGameSettings jsGameSettings) {
        this.jsPerfTestResult = jsPerfTestResult;
        this.jsGameSettings = jsGameSettings;
    }

    @JSCodingFunctionOrMethod(description = "Get game settings instance.")
    public JSGameSettings getGameSettings() {
        return this.jsGameSettings;
    }

    @JSCodingFunctionOrMethod(description = "Get performance test result.")
    public JSPerfTestResult getPerfTestResult() {
        return this.jsPerfTestResult;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSAfterSettingsPerfTestEvent";
    }
}