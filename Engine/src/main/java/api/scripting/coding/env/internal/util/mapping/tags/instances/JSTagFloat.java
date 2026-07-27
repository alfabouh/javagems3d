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

package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import javagems3d.system.external.mapping.tags.items.TagFloat;

@JSCodingClass(binding = "JSTagFloat", description = "Floating point tag item.")
public class JSTagFloat {

    @JSHideFromDoc
    private final TagFloat tag;

    @JSCodingConstructor(description = "Create TagFloat with value, min and max.")
    public JSTagFloat(float value, float min, float max) {
        this.tag = new TagFloat(value, min, max);
    }

    @JSCodingConstructor(description = "Wrap existing TagItem as JSTagFloat.")
    public JSTagFloat(JSTagItem tagItem) {
        this.tag = (TagFloat) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagFloat getJava() {
        return this.tag;
    }

    @JSHideFromDoc
    public JSTagItem toTagItem() {
        return new JSTagItem(this.tag);
    }

    @JSCodingFunctionOrMethod(description = "Set value of the float tag.", paramNames = {"value"})
    public JSTagFloat setValue(float value) {
        this.tag.setValue(value);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get current value of the float tag.")
    public float getValue() {
        return this.tag.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Get minimum allowed value of the float tag.")
    public float getMin() {
        return this.tag.getMin();
    }

    @JSCodingFunctionOrMethod(description = "Get maximum allowed value of the float tag.")
    public float getMax() {
        return this.tag.getMax();
    }

    @JSCodingFunctionOrMethod(description = "Copy the tag.")
    public JSTagFloat copy() {
        return new JSTagFloat(new JSTagItem(this.tag.copy()));
    }

    @Override
    public String toString() {
        return "JSTagFloat(" + this.tag.getValue() + ", min=" + this.tag.getMin() + ", max=" + this.tag.getMax() + ")";
    }
}