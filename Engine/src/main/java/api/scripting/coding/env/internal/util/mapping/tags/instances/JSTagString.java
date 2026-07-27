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
import javagems3d.system.external.mapping.tags.items.TagString;

@JSCodingClass(binding = "JSTagString", description = "String tag item.")
public class JSTagString {
    @JSHideFromDoc
    private final TagString tag;

    @JSCodingConstructor(description = "Create string tag.", paramNames = {"text"})
    public JSTagString(String text) {
        this.tag = new TagString(text);
    }

    @JSCodingConstructor(description = "Wrap existing TagString.", paramNames = {"tagItem"})
    public JSTagString(JSTagItem tagItem) {
        this.tag = (TagString) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagString getJava() {
        return this.tag;
    }

    @JSHideFromDoc
    public JSTagItem toTagItem() {
        return new JSTagItem(this.tag);
    }

    @JSCodingFunctionOrMethod(description = "Set text value.", paramNames = {"text"})
    public JSTagString setText(String text) {
        this.tag.setText(text);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get text value.")
    public String getText() {
        return this.tag.getText();
    }

    @JSCodingFunctionOrMethod(description = "Copy tag.")
    public JSTagString copy() {
        return new JSTagString(this.tag.getText());
    }

    @Override
    public String toString() {
        return "JSTagString(" + this.tag.getText() + ")";
    }
}