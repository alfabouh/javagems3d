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
import javagems3d.system.settings.objects.SettingChooseLanguage;
import javagems3d.system.settings.objects.SettingSlot;

@JSCodingClass(binding = "JSSettingLanguage", description = "Language selection setting.")
public class JSSettingLanguage implements JSSettingI, JSSettingSlotI {
    @JSHideFromDoc private final SettingChooseLanguage setting;

    @JSCodingConstructor(description = "Create language setting.", paramNames = {"name"})
    public JSSettingLanguage(String name) {
        this.setting = new SettingChooseLanguage(name, null);
    }

    @JSHideFromDoc
    public JSSettingLanguage(SettingChooseLanguage setting) {
        this.setting = setting;
    }

    @JSCodingFunctionOrMethod(description = "Get current language name.")
    public String getCurrentName() {
        return this.setting.getCurrentName();
    }

    @JSCodingFunctionOrMethod(description = "Get current index.")
    public int get() {
        return this.setting.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set language index.", paramNames = {"value"})
    public void set(int value) {
        this.setting.setValue(value);
    }

    @JSHideFromDoc
    public SettingChooseLanguage getJavaSetting() {
        return this.setting;
    }

    @JSHideFromDoc
    @Override
    public SettingSlot getJavaSettingSlot() {
        return this.getJavaSetting();
    }
}