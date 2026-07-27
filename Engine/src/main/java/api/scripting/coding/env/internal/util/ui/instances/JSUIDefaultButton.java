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
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIDefaultButton;

@JSCodingClass(binding = "JSUIDefaultButton", description = "Wrapper for a default UI button, exposing position, size, event handlers, and underlying Java UIDefaultButton.")
public class JSUIDefaultButton {
    @JSHideFromDoc private final UIDefaultButton uiDefaultButton;

    @JSHideFromDoc
    public JSUIDefaultButton(UIDefaultButton uiDefaultButton) {
        this.uiDefaultButton = uiDefaultButton;
    }

    @JSCodingFunctionOrMethod(description = "Get the position of the button as a JSVector2f.")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiDefaultButton.getPosition().x, this.uiDefaultButton.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "Get the size of the button as a JSVector2f.")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiDefaultButton.getScaledSize().x, this.uiDefaultButton.getScaledSize().y);
    }

    @JSCodingFunctionOrMethod(description = "Set the action to execute when the button is unclicked.")
    public JSUIDefaultButton setOnUnClick(Runnable onUnClick) {
        this.uiDefaultButton.setOnClick(onUnClick::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set the action to execute when the cursor enters the button area.")
    public JSUIDefaultButton setOnEntered(Runnable onEntered) {
        this.uiDefaultButton.setOnEntered(onEntered::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set the action to execute when the button is clicked.")
    public JSUIDefaultButton setOnClick(Runnable onClick) {
        this.uiDefaultButton.setOnClick(onClick::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set the action to execute when the cursor is inside the button.")
    public JSUIDefaultButton setOnInside(Runnable onInside) {
        this.uiDefaultButton.setOnInside(onInside::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set the action to execute when the cursor leaves the button.")
    public JSUIDefaultButton setOnLeft(Runnable onLeft) {
        this.uiDefaultButton.setOnLeft(onLeft::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java UIDefaultButton object.")
    public UIDefaultButton getJavaUIDefaultButton() {
        return this.uiDefaultButton;
    }
}