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

package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIPictureStatic;

@JSCodingClass(binding = "JSUIPictureStatic", description = "Wrapper for a static UI picture, exposing position, size, and the underlying Java UIPictureStatic object.")
public class JSUIPictureStatic {
    @JSHideFromDoc private final UIPictureStatic uiPictureStatic;

    @JSHideFromDoc
    public JSUIPictureStatic(UIPictureStatic uiPictureStatic) {
        this.uiPictureStatic = uiPictureStatic;
    }

    @JSCodingFunctionOrMethod(description = "Get the position of the static picture as a JSVector2f.")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiPictureStatic.getPosition().x, this.uiPictureStatic.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "Get the size of the static picture as a JSVector2f.")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiPictureStatic.getScaledSize().x, this.uiPictureStatic.getScaledSize().y);
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java UIPictureStatic object.")
    public UIPictureStatic getJavaUIPicture() {
        return this.uiPictureStatic;
    }
}