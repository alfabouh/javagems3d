package ru.alfabouh.engine.render.scene.immediate_gui.panels.base;

import org.joml.Vector2i;
import ru.alfabouh.engine.render.scene.immediate_gui.ImmediateUI;

public interface PanelUI {
    void onConstruct(ImmediateUI immediateUI);
    void onDestruct(ImmediateUI immediateUI);
    void drawPanel(ImmediateUI immediateUI, double partialTicks);
    void onWindowResize(Vector2i dim);
}
