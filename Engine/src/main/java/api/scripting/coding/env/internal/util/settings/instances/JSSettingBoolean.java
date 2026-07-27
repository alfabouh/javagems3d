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
import javagems3d.system.settings.objects.SettingTrueFalse;

@JSCodingClass(binding = "JSSettingBoolean", description = "Boolean setting (true/false).")
public class JSSettingBoolean implements JSSettingI {
    @JSHideFromDoc
    private final SettingTrueFalse setting;

    @JSCodingConstructor(description = "Create boolean setting.", paramNames = {"name", "defaultValue"})
    public JSSettingBoolean(String name, boolean defaultValue) {
        this.setting = new SettingTrueFalse(name, defaultValue);
    }

    @JSHideFromDoc
    public JSSettingBoolean(SettingTrueFalse setting) {
        this.setting = setting;
    }

    @JSCodingFunctionOrMethod(description = "Get current value.")
    public boolean get() {
        return this.setting.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set value.", paramNames = {"value"})
    public void set(boolean value) {
        this.setting.setValue(value);
    }

    @JSHideFromDoc
    public SettingTrueFalse getJavaSetting() {
        return this.setting;
    }
}