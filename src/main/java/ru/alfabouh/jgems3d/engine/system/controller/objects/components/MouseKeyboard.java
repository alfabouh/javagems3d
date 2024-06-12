package ru.alfabouh.jgems3d.engine.system.controller.objects.components;

import org.lwjgl.glfw.GLFW;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.system.controller.binding.BindingManager;
import ru.alfabouh.jgems3d.engine.system.controller.components.Key;

public class MouseKeyboard {
    private final IWindow window;
    public boolean scrollUpdate;
    private int scrollVector;
    private boolean isInWindowBounds;
    private boolean flag1;
    private boolean flag2;
    private boolean flag3;
    private boolean forceInterruptLMB;
    private boolean forceInterruptRMB;
    private boolean forceInterruptMMB;

    public MouseKeyboard(IWindow window) {
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

    public void update(BindingManager bindingManager) {
        bindingManager.getBindingSet().forEach(e -> e.getKey().refreshState(this.isKeyPressed(e.getKey())));

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

        if (this.scrollUpdate) {
            this.scrollUpdate = false;
        } else {
            this.scrollVector = 0;
        }
    }

    public boolean isKeyPressed(int keyCode) {
        if (keyCode >= GLFW.GLFW_MOUSE_BUTTON_1 && keyCode <= GLFW.GLFW_MOUSE_BUTTON_8) {
            return GLFW.glfwGetMouseButton(this.getWindow().getDescriptor(), keyCode) == GLFW.GLFW_PRESS;
        }
        return GLFW.glfwGetKey(this.getWindow().getDescriptor(), keyCode) == GLFW.GLFW_PRESS;
    }

    public boolean isKeyPressed(Key key) {
        return this.isKeyPressed(key.getKeyCode());
    }

    public IWindow getWindow() {
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

    public int getScrollVector() {
        return this.scrollVector;
    }
}
