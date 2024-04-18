package ru.alfabouh.engine.render.scene.gui.base;

import org.joml.Vector2i;

public interface GUI {
    void onRender(double partialTicks);

    void onStartRender();

    void onStopRender();

    boolean isVisible();
    default void onWindowResize(Vector2i dim) {
    }
}
