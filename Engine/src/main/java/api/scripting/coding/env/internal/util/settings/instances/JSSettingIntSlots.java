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
import javagems3d.system.settings.objects.SettingIntSlots;
import javagems3d.system.settings.objects.SettingSlot;

@JSCodingClass(binding = "JSSettingIntSlots", description = "Integer slot-based setting.")
public class JSSettingIntSlots implements JSSettingI, JSSettingSlotI {
    @JSHideFromDoc
    private final SettingIntSlots setting;

    @JSCodingConstructor(description = "Create int slot setting.", paramNames = {"name", "defaultValue", "min", "max"})
    public JSSettingIntSlots(String name, int defaultValue, int min, int max) {
        this.setting = new SettingIntSlots(name, defaultValue, min, max);
    }

    @JSHideFromDoc
    public JSSettingIntSlots(SettingIntSlots setting) {
        this.setting = setting;
    }

    @JSCodingFunctionOrMethod(description = "Add option.", paramNames = {"index", "name", "isI18n"})
    public void add(int index, String name, boolean isI18n) {
        this.setting.addArticle(index, name, isI18n);
    }

    @JSCodingFunctionOrMethod(description = "Get current index.")
    public int get() {
        return this.setting.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set index.", paramNames = {"value"})
    public void set(int value) {
        this.setting.setValue(value);
    }

    @JSCodingFunctionOrMethod(description = "Get current name.")
    public String getCurrentName() {
        return this.setting.getCurrentName();
    }

    @JSHideFromDoc
    public SettingIntSlots getJavaSetting() {
        return this.setting;
    }

    @JSHideFromDoc
    @Override
    public SettingSlot getJavaSettingSlot() {
        return this.getJavaSetting();
    }
}