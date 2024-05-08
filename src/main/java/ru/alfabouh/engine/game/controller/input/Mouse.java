package ru.alfabouh.engine.game.controller.input;

import org.lwjgl.glfw.GLFW;
import ru.alfabouh.engine.render.screen.window.Window;

public class Mouse {
    private final Window window;
    public boolean scrollUpdate;
    public int scrollVector;
    private boolean isInWindowBounds;
    private boolean flag1;
    private boolean flag2;
    private boolean flag3;
    private boolean forceInterruptLMB;
    private boolean forceInterruptRMB;
    private boolean forceInterruptMMB;

    public Mouse(Window window) {
        this.window = window;
        GLFW.glfwSetCursorEnterCallback(window.getDescriptor(), (getWindow, entered) -> this.isInWindowBounds = entered);
        GLFW.glfwSetScrollCallback(window.getDescriptor(), (getWindow, x, y) -> {
            if (y > 0) {
                this.scrollVector = 1;
            }
            if (y < 0) {
                this.scrollVector = -1;
            }
            this.scrollUpdate = true;
        });
    }

    public void update() {
        this.flag1 = GLFW.glfwGetMouseButton(this.window.getDescriptor(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
        this.flag2 = GLFW.glfwGetMouseButton(this.window.getDescriptor(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS;
        this.flag3 = GLFW.glfwGetMouseButton(this.window.getDescriptor(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS;

        if (this.forceInterruptLMB) {
            if (!this.flag1) {
                this.forceInterruptLMB = false;
            } else {
                this.flag1 = false;
            }
        }

        if (this.forceInterruptRMB) {
            if (!this.flag2) {
                this.forceInterruptRMB = false;
            } else {
                this.flag2 = false;
            }
        }

        if (this.forceInterruptMMB) {
            if (!this.flag3) {
                this.forceInterruptMMB = false;
            } else {
                this.flag3 = false;
            }
        }
    }

    public Window getWindow() {
        return this.window;
    }

    public double[] getCursorCoordinates() {
        double[] dx = new double[1];
        double[] dy = new double[1];
        GLFW.glfwGetCursorPos(this.getWindow().getDescriptor(), dx, dy);
        return new double[]{dx[0], dy[0]};
    }

    public void setCursorCoordinates(double[] xy) {
        GLFW.glfwSetCursorPos(this.window.getDescriptor(), xy[0], xy[1]);
    }

    public boolean isCursorInWindowBounds() {
        return this.isInWindowBounds;
    }

    public void forceInterruptLMB() {
        this.forceInterruptLMB = true;
        this.flag1 = false;
    }

    public void forceInterruptRMB() {
        this.forceInterruptRMB = true;
        this.flag2 = false;
    }

    public void forceInterruptMMB() {
        this.forceInterruptMMB = true;
        this.flag3 = false;
    }

    public boolean isLeftKeyPressed() {
        return this.flag1;
    }

    public boolean isRightKeyPressed() {
        return this.flag2;
    }

    public boolean isMiddleKeyPressed() {
        return this.flag3;
    }
}
