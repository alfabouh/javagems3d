package api.scripting.coding.env.internal.game.init.events.settings;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.settings.JSGameSettings;
import api.scripting.coding.env.internal.util.settings.instances.JSSettingI;

@JSCodingClass(binding = "JSInitSettingsEvent", description = "Event triggered during game settings initialization, allows creating and registering settings.")
public class JSInitSettingsEvent implements JSEventI {
    @JSHideFromDoc private JSGameSettings jsGameSettings;

    @JSCodingConstructor(description = "Create a JSInitSettingsEvent with optional reference to JSGameSettings.")
    public JSInitSettingsEvent() {
    }

    @JSHideFromDoc
    public JSInitSettingsEvent(JSGameSettings jsGameSettings) {
        this.jsGameSettings = jsGameSettings;
    }

    @JSCodingFunctionOrMethod(description = "Register a new setting into the game settings system.", paramNames = {"setting"})
    public JSSettingI createSetting(JSSettingI setting) {
        return this.jsGameSettings.addSetting(setting);
    }

    @JSCodingFunctionOrMethod(description = "Get the game settings instance associated with this event.")
    public JSGameSettings getGameSettings() {
        return this.jsGameSettings;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSInitSettingsEvent";
    }
}