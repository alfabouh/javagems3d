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

package api.scripting.coding.env.internal.util.resources.instances.font;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;

import java.awt.*;

@JSCodingClass(binding = "JSFontStyles", description = "Enumeration of font styles for scripting, mapping to Java AWT font style constants.")
public enum JSFontStyles {
    @JSCodingField(description = "Plain style") PLAIN(Font.PLAIN),
    @JSCodingField(description = "Italic style") ITALIC(Font.ITALIC),
    @JSCodingField(description = "Bold style") BOLD(Font.BOLD);

    private final int value;
    JSFontStyles(int value) {
        this.value = value;
    }

    @JSCodingFunctionOrMethod(description = "Get the integer value of the font style as used by Java AWT.")
    public int getValue() {
        return this.value;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return super.toString();
    }
}
