package api.scripting.coding.env.internal.util.settings;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.settings.instances.*;
import javagems3d.system.settings.JGemsSettings;

@JSCodingClass(binding = "JSGameSettings", description = "Exposes game settings to scripts, providing access to default values and allowing addition of custom settings.")
public class JSGameSettings {
    @JSHideFromDoc private final JGemsSettings jGemsSettings;

    @JSHideFromDoc
    public JSGameSettings(JGemsSettings jGemsSettings) {
        this.jGemsSettings = jGemsSettings;
    }

    @JSCodingFunctionOrMethod(description = "Get the default sound gain setting.")
    public JSSettingFloat defaultSetting_soundGain() {
        return new JSSettingFloat(this.jGemsSettings.soundGain);
    }

    @JSCodingFunctionOrMethod(description = "Get the default shadow quality setting.")
    public JSSettingIntSlots defaultSetting_shadowQuality() {
        return new JSSettingIntSlots(this.jGemsSettings.shadowQuality);
    }

    @JSCodingFunctionOrMethod(description = "Get the default window mode setting.")
    public JSSettingIntSlots defaultSetting_windowMode() {
        return new JSSettingIntSlots(this.jGemsSettings.windowMode);
    }

    @JSCodingFunctionOrMethod(description = "Get the default vSync setting.")
    public JSSettingIntSlots defaultSetting_vSync() {
        return new JSSettingIntSlots(this.jGemsSettings.vSync);
    }

    @JSCodingFunctionOrMethod(description = "Get the default anisotropic filtering setting.")
    public JSSettingIntSlots defaultSetting_anisotropic() {
        return new JSSettingIntSlots(this.jGemsSettings.anisotropic);
    }

    @JSCodingFunctionOrMethod(description = "Get the default FXAA setting.")
    public JSSettingIntSlots defaultSetting_fxaa() {
        return new JSSettingIntSlots(this.jGemsSettings.fxaa);
    }

    @JSCodingFunctionOrMethod(description = "Get the default SSAO setting.")
    public JSSettingIntSlots defaultSetting_ssao() {
        return new JSSettingIntSlots(this.jGemsSettings.ssao);
    }

    @JSCodingFunctionOrMethod(description = "Get the default texture quality setting.")
    public JSSettingIntSlots defaultSetting_texturesQuality() {
        return new JSSettingIntSlots(this.jGemsSettings.texturesQuality);
    }

    @JSCodingFunctionOrMethod(description = "Get the default texture filtering setting.")
    public JSSettingIntSlots defaultSetting_texturesFiltering() {
        return new JSSettingIntSlots(this.jGemsSettings.texturesFiltering);
    }

    @JSCodingFunctionOrMethod(description = "Get the default bloom effect setting.")
    public JSSettingIntSlots defaultSetting_bloom() {
        return new JSSettingIntSlots(this.jGemsSettings.bloom);
    }

    @JSCodingFunctionOrMethod(description = "Get the default language setting.")
    public JSSettingLanguage defaultSetting_language() {
        return new JSSettingLanguage(this.jGemsSettings.language);
    }

    @JSCodingFunctionOrMethod(description = "Add a new custom setting to the game settings.", paramNames = {"settingObject"})
    public JSSettingI addSetting(JSSettingI settingObject) {
        this.jGemsSettings.addSetting(settingObject.getJavaSetting());
        return settingObject;
    }
}