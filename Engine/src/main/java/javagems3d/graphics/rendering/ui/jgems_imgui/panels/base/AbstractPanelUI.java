/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

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
    public void onConstruct(JGemsUI JGemsUI) {

    }

    @Override
    public void onDestruct(JGemsUI JGemsUI) {

    }

    @Override
    public void onWindowResize(IWindow window) {

    }

    public void closePanel(JGemsUI JGemsUI) {
        JGemsUI.removePanel();
    }

    public void goBack(JGemsUI JGemsUI) {
        if (JGemsUI != null) {
            JGemsUI.setPanel(this.prevPanel);
        } else {
            Log.get().warn("Couldn't go back to NULL UI panel");
        }
    }
}
