package ru.BouH.engine.render.scene.gui.base;

import org.joml.Vector2d;

public interface GUI {
    void onRender(double partialTicks);
    void onStartRender();
    void onStopRender();
    boolean isVisible();
}
