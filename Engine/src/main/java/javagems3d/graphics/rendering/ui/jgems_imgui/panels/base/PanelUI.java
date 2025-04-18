package javagems3d.graphics.rendering.ui.jgems_imgui.panels.base;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;

public interface PanelUI extends IWindow.ResizeEvent {
    void onConstruct(JGemsUI ui);

    void onDestruct(JGemsUI ui);

    void drawPanel(JGemsUI ui, float frameDeltaTicks);
}
