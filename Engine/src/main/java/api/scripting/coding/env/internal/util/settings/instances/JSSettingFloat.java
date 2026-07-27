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

package api.scripting.coding.env.internal.util.settings.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.settings.objects.SettingFloatBar;

@JSCodingClass(binding = "JSSettingFloat", description = "Float setting.")
public class JSSettingFloat implements JSSettingI {
    @JSHideFromDoc
    private final SettingFloatBar setting;

    @JSCodingConstructor(description = "Create float setting.", paramNames = {"name", "defaultValue"})
    public JSSettingFloat(String name, float defaultValue) {
        this.setting = new SettingFloatBar(name, defaultValue);
    }

    @JSHideFromDoc
    public JSSettingFloat(SettingFloatBar setting) {
        this.setting = setting;
    }

    @JSCodingFunctionOrMethod(description = "Get value.")
    public float get() {
        return this.setting.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set value.", paramNames = {"value"})
    public void set(float value) {
        this.setting.setValue(value);
    }

    @JSHideFromDoc
    public SettingFloatBar getJavaSetting() {
        return this.setting;
    }
}