package ru.alfabouh.jgems3d.engine.render.opengl.screen.window;

import org.joml.Vector2i;

public interface IWindow {
    long getDescriptor();
    Vector2i getWindowDimensions();
    boolean isInFocus();
}
