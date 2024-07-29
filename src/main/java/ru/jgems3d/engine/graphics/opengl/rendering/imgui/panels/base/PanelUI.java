package ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base;

import org.joml.Vector2i;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;

public interface PanelUI {
    void onConstruct(ImmediateUI immediateUI);

    void onDestruct(ImmediateUI immediateUI);

    void drawPanel(ImmediateUI immediateUI, float partialTicks);

    void onWindowResize(Vector2i dim);
}
