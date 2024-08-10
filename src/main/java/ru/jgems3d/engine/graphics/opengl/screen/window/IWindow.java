package ru.jgems3d.engine.graphics.opengl.screen.window;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public interface IWindow {
    long getDescriptor();

    Vector2i getWindowDimensions();

    boolean isInFocus();

    default boolean isWindowActive() {
        if (this.getWindowDimensions().x == 0 || this.getWindowDimensions().y == 0) {
            return false;
        }
        return GLFW.glfwGetWindowAttrib(this.getDescriptor(), GLFW.GLFW_ICONIFIED) == 0;
    }
}
