package api.scripting.coding.env.internal.util.controlling.devices;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.system.controller.devices.MouseKeyboard;

@JSCodingClass(binding = "JSMouseKeyboard", description = "Wrapper for mouse and keyboard input, providing cursor position, mouse buttons, and scroll state.")
public class JSMouseKeyboard {
    @JSHideFromDoc
    private final MouseKeyboard mouseKeyboard;

    @JSCodingConstructor(description = "Wraps a MouseKeyboard instance.", paramNames = {"mouseKeyboard"})
    public JSMouseKeyboard(MouseKeyboard mouseKeyboard) {
        this.mouseKeyboard = mouseKeyboard;
    }

    @JSCodingFunctionOrMethod(description = "Returns true if left mouse button is pressed.")
    public boolean isLeftButtonPressed() {
        return this.mouseKeyboard.isLeftKeyPressed();
    }

    @JSCodingFunctionOrMethod(description = "Returns true if right mouse button is pressed.")
    public boolean isRightButtonPressed() {
        return this.mouseKeyboard.isRightKeyPressed();
    }

    @JSCodingFunctionOrMethod(description = "Returns true if middle mouse button is pressed.")
    public boolean isMiddleButtonPressed() {
        return this.mouseKeyboard.isMiddleKeyPressed();
    }

    @JSCodingFunctionOrMethod(description = "Returns the current scroll vector. -1 = down, 0 = none, 1 = up.")
    public int getScrollVector() {
        return this.mouseKeyboard.getScrollVector();
    }

    @JSCodingFunctionOrMethod(description = "Returns cursor coordinates as a 2D vector.")
    public JSVector2f getCursorPosition() {
        return new JSVector2f(this.mouseKeyboard.getCursorCoordinatesV2F());
    }

    @JSCodingFunctionOrMethod(description = "Sets cursor coordinates. Expects array of two elements [x, y].")
    public void setCursorPosition(double[] xy) {
        this.mouseKeyboard.setCursorCoordinates(xy);
    }

    @JSCodingFunctionOrMethod(description = "Returns true if cursor is inside window bounds.")
    public boolean isCursorInWindow() {
        return this.mouseKeyboard.isCursorInWindowBounds();
    }

    @JSCodingFunctionOrMethod(description = "Force interrupts left mouse button state.")
    public void forceInterruptLMB() {
        this.mouseKeyboard.forceInterruptLMB();
    }

    @JSCodingFunctionOrMethod(description = "Force interrupts right mouse button state.")
    public void forceInterruptRMB() {
        this.mouseKeyboard.forceInterruptRMB();
    }

    @JSCodingFunctionOrMethod(description = "Force interrupts middle mouse button state.")
    public void forceInterruptMMB() {
        this.mouseKeyboard.forceInterruptMMB();
    }

    @JSHideFromDoc
    public MouseKeyboard getJavaMouseKeyboard() {
        return this.mouseKeyboard;
    }
}