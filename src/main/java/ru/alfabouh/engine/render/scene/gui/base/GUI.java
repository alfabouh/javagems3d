package ru.alfabouh.engine.render.scene.gui.base;

public interface GUI {
    void onRender(double partialTicks);

    void onStartRender();

    void onStopRender();

    boolean isVisible();
}
