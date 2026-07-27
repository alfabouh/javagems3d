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

package api.scripting.coding.env.internal.util.controlling;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.controlling.devices.JSMouseKeyboard;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.base.ScanningMode;
import org.joml.Vector2f;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSMouseKeyboardController", description = "Controller wrapper for keyboard and mouse input, including movement, rotation, and scanning modes.")
public class JSMouseKeyboardController extends JSController {
    @JSHideFromDoc
    private final MouseKeyboardController controller;

    @JSCodingConstructor(description = "Wraps an existing MouseKeyboardController instance.", paramNames = {"controller"})
    public JSMouseKeyboardController(MouseKeyboardController controller) {
        super(controller);
        this.controller = controller;
    }

    @JSCodingFunctionOrMethod(description = "Returns the current raw mouse and keyboard handler.")
    public JSMouseKeyboard getMouseAndKeyboard() {
        return new JSMouseKeyboard(this.controller.getMouseAndKeyboard());
    }

    @JSCodingFunctionOrMethod(description = "Returns the current rotation input vector (raw, not normalized).")
    public JSVector2f getRotationInput() {
        Vector2f input = this.controller.getRotationInput();
        return new JSVector2f(input.x, input.y);
    }

    @JSCodingFunctionOrMethod(description = "Returns the current position input vector (raw, not normalized).")
    public JSVector3f getPositionInput() {
        Vector3f input = this.controller.getPositionInput();
        return new JSVector3f(input.x, input.y, input.z);
    }

    @JSCodingFunctionOrMethod(description = "Clears all movement and rotation inputs.")
    public void clearInputs() {
        this.controller.clear();
    }

    @JSCodingFunctionOrMethod(description = "Centers the mouse cursor in the window.")
    public void centerCursor() {
        this.controller.setCursorInCenter();
    }

    @JSCodingFunctionOrMethod(description = "Returns the scanning mode for the mouse input.")
    public ScanningMode getScanningMode() {
        return this.controller.getScanningMode();
    }

    @JSCodingFunctionOrMethod(description = "Returns the camera sensitivity used for rotation input scaling.")
    public float getCamSensitivity() {
        return this.controller.getCamSensitivity();
    }

    @JSCodingFunctionOrMethod(description = "Returns true if mouse scanning is disabled.")
    public boolean disableMouseScanning() {
        return this.controller.disableMouseScanning();
    }

    @JSCodingFunctionOrMethod(description = "Returns true if keyboard scanning is disabled.")
    public boolean disableKeyboardScanning() {
        return this.controller.disableKeyboardScanning();
    }

    @JSHideFromDoc
    public MouseKeyboardController getJavaController() {
        return this.controller;
    }
}