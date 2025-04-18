package javagems3d.graphics.rendering.ui.jgems_imgui.panels.base;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import logger.Log;

public abstract class AbstractPanelUI implements PanelUI, IWindow.ResizeEvent {
    private final PanelUI prevPanel;

    public AbstractPanelUI(PanelUI prevPanel) {
        this.prevPanel = prevPanel;
    }

    @Override
    public void onConstruct(JGemsUI ui) {

    }

    @Override
    public void onDestruct(JGemsUI ui) {

    }

    @Override
    public void onWindowResize(IWindow window) {

    }

    public void closePanel(JGemsUI ui) {
        ui.removePanel();
    }

    public void goBack(JGemsUI ui) {
        if (ui != null) {
            ui.setPanel(this.prevPanel);
        } else {
            Log.get().warn("Couldn't go back to NULL UI panel");
        }
    }
}
