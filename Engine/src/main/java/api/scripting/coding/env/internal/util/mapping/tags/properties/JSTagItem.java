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

package api.scripting.coding.env.internal.util.mapping.tags.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.items.TagItem;

@JSCodingClass(binding = "JSTagItem", description = "Wrapper for TagItem.")
public class JSTagItem {
    @JSHideFromDoc
    private final TagItem tagItem;

    @JSCodingConstructor(description = "Create tag wrapper.", paramNames = {"tagItem"})
    public JSTagItem(TagItem tagItem) {
        this.tagItem = tagItem;
    }

    @JSHideFromDoc
    public TagItem getJavaTagItem() {
        return this.tagItem;
    }

    @JSCodingFunctionOrMethod(description = "Get tag type string.")
    public String getType() {
        return this.tagItem.getTypeString();
    }

    @Override
    public String toString() {
        return "JSTagItem(" + this.tagItem.getTypeString() + ")";
    }
}