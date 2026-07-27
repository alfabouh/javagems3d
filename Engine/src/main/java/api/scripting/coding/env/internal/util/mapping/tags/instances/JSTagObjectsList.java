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
import javagems3d.system.external.mapping.tags.items.TagObjectsList;

@JSCodingClass(binding = "JSTagObjectsList", description = "Objects list tag item.")
public class JSTagObjectsList {

    @JSHideFromDoc
    private final TagObjectsList tag;

    @JSCodingConstructor(description = "Create empty objects list tag.")
    public JSTagObjectsList() {
        this.tag = new TagObjectsList();
    }

    @JSCodingConstructor(description = "Wrap existing TagObjectsList.")
    public JSTagObjectsList(JSTagItem tagItem) {
        this.tag = (TagObjectsList) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagObjectsList getJava() {
        return this.tag;
    }

    @JSHideFromDoc
    public JSTagItem toTagItem() {
        return new JSTagItem(this.tag);
    }

    @JSCodingFunctionOrMethod(description = "Set selected object value.", paramNames = {"value"})
    public JSTagObjectsList setValue(int value) {
        this.tag.setValue(value);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get selected object value.")
    public int getValue() {
        return this.tag.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Copy tag.")
    public JSTagObjectsList copy() {
        return new JSTagObjectsList(new JSTagItem(this.tag.copy()));
    }

    @Override
    public String toString() {
        return "JSTagObjectsList(" + this.tag.getValue() + ")";
    }
}