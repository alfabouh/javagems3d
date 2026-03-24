package api.scripting.coding.env.internal.game.init.events.settings;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.screen.JSScreen;
import api.scripting.coding.env.internal.util.settings.JSGameSettings;
import api.scripting.coding.env.internal.util.settings.instances.JSSettingI;
import api.scripting.coding.env.internal.util.ui.JSUIPanelWrapper;
import api.system.scripting.JavaToJsAPI;
import javagems3d.system.service.collections.Pair;

@JSCodingClass(binding = "JSInitSettingsEvent", description = "...")
public class JSInitSettingsEvent implements JSEventI {
    @JSHideFromDoc private JSGameSettings jsGameSettings;

    @JSCodingConstructor(description = "...")
    public JSInitSettingsEvent() {
    }

    @JSHideFromDoc
    public JSInitSettingsEvent(JSGameSettings jsGameSettings) {
        this.jsGameSettings = jsGameSettings;
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"setting"})
    public JSSettingI createSetting(JSSettingI setting) {
        return this.jsGameSettings.addSetting(setting);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSGameSettings getGameSettings() {
        return this.jsGameSettings;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSInitSettingsEvent";
    }
}
