package ru.BouH.engine.game.controller.input;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.render.screen.window.Window;

import java.util.concurrent.atomic.AtomicBoolean;

public class Mouse {
    private final Window window;
    private boolean isInWindowBounds;

    public Mouse(Window window) {
        this.window = window;
        GLFW.glfwSetCursorEnterCallback(window.getDescriptor(), (getWindow, entered) -> this.isInWindowBounds = entered);
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
        GLFW.glfwSetCursorPos(window.getDescriptor(), xy[0], xy[1]);
    }

    public boolean isCursorInWindowBounds() {
        return this.isInWindowBounds;
    }

    public boolean isLeftKeyPressed() {
        return this.isMouseKeyPressed(GLFW.GLFW_MOUSE_BUTTON_1);
    }

    public boolean isRightKeyPressed() {
        return this.isMouseKeyPressed(GLFW.GLFW_MOUSE_BUTTON_2);
    }

    public boolean isMiddleKeyPressed() {
        return this.isMouseKeyPressed(GLFW.GLFW_MOUSE_BUTTON_3);
    }

    public boolean isMouseKeyPressed(int code) {
        AtomicBoolean flag = new AtomicBoolean(false);
        GLFW.glfwSetMouseButtonCallback(this.getWindow().getDescriptor(), (getWindow, button, action, mode) -> {
            if (button == code && action == GLFW.GLFW_PRESS) {
                flag.set(true);
            }
        });
        return flag.get();
    }
}
