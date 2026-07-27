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

package api.scripting.coding.env.internal.util.mapping.tags.instances.inner;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.items.TagRadioBoolean;

@JSCodingClass(binding = "JSRadioInfo", description = "Option info for JSTagRadioBoolean.")
public class JSRadioInfo {
    private final TagRadioBoolean.Info info;

    @JSCodingConstructor(description = "Create radio boolean info with name and flag.")
    public JSRadioInfo(String name, boolean flag) {
        this.info = new TagRadioBoolean.Info(name, flag);
    }

    @JSCodingConstructor(description = "Wrap existing JSRadioInfo.")
    public JSRadioInfo(TagRadioBoolean.Info info) {
        this.info = info;
    }

    @JSHideFromDoc
    public TagRadioBoolean.Info toJava() {
        return this.info;
    }

    @JSCodingFunctionOrMethod(description = "Get option name.")
    public String getName() {
        return this.info.getName();
    }

    @JSCodingFunctionOrMethod(description = "Check if option is selected.")
    public boolean isSelected() {
        return this.info.isFlag();
    }

    @JSCodingFunctionOrMethod(description = "Set option as selected or not.", paramNames = {"selected"})
    public JSRadioInfo setSelected(boolean selected) {
        this.info.setFlag(selected);
        return this;
    }
}