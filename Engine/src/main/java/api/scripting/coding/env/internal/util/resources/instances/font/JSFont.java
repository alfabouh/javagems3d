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
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;

@JSCodingClass(binding = "JSFont", description = "Wrapper for Java GUI fonts used in scripting, providing access to the underlying JGemsGuiFont for rendering text and UI elements.")
public class JSFont implements JSCanBeCachedInMemory {
    @JSHideFromDoc
    private final JGemsGuiFont guiFont;

    @JSHideFromDoc
    public JSFont(JGemsGuiFont guiFont) {
        this.guiFont = guiFont;
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java GUI font object.")
    public JGemsGuiFont getJavaGuiFont() {
        return this.guiFont;
    }
}
