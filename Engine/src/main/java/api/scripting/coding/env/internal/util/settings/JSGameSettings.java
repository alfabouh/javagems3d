/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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