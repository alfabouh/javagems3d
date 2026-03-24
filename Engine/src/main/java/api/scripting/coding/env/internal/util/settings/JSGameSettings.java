package api.scripting.coding.env.internal.util.settings;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.settings.instances.*;
import javagems3d.system.settings.JGemsSettings;

@JSCodingClass(binding = "JSGameSettings", description = "...")
public class JSGameSettings {
    @JSHideFromDoc private final JGemsSettings jGemsSettings;

    @JSHideFromDoc
    public JSGameSettings(JGemsSettings jGemsSettings) {
        this.jGemsSettings = jGemsSettings;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingFloat defaultSetting_soundGain() {
        return new JSSettingFloat(this.jGemsSettings.soundGain);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingIntSlots defaultSetting_shadowQuality() {
        return new JSSettingIntSlots(this.jGemsSettings.shadowQuality);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingIntSlots defaultSetting_windowMode() {
        return new JSSettingIntSlots(this.jGemsSettings.windowMode);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingIntSlots defaultSetting_vSync() {
        return new JSSettingIntSlots(this.jGemsSettings.vSync);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingIntSlots defaultSetting_anisotropic() {
        return new JSSettingIntSlots(this.jGemsSettings.anisotropic);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingIntSlots defaultSetting_fxaa() {
        return new JSSettingIntSlots(this.jGemsSettings.fxaa);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingIntSlots defaultSetting_ssao() {
        return new JSSettingIntSlots(this.jGemsSettings.ssao);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingIntSlots defaultSetting_texturesQuality() {
        return new JSSettingIntSlots(this.jGemsSettings.texturesQuality);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingIntSlots defaultSetting_texturesFiltering() {
        return new JSSettingIntSlots(this.jGemsSettings.texturesFiltering);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingIntSlots defaultSetting_bloom() {
        return new JSSettingIntSlots(this.jGemsSettings.bloom);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSettingLanguage defaultSetting_language() {
        return new JSSettingLanguage(this.jGemsSettings.language);
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"settingObject"})
    public JSSettingI addSetting(JSSettingI settingObject) {
        this.jGemsSettings.addSetting(settingObject.getJavaSetting());
        return settingObject;
    }
}
