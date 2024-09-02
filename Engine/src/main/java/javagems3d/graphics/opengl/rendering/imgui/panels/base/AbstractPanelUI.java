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

package javagems3d.graphics.opengl.rendering.imgui.panels.base;

import org.joml.Vector2i;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.rendering.imgui.ImmediateUI;

public abstract class AbstractPanelUI implements PanelUI {
    private final PanelUI prevPanel;

    public AbstractPanelUI(PanelUI prevPanel) {
        this.prevPanel = prevPanel;
    }

    @Override
    public void onConstruct(ImmediateUI immediateUI) {

    }

    @Override
    public void onDestruct(ImmediateUI immediateUI) {

    }

    @Override
    public void onWindowResize(Vector2i dim) {

    }

    public void closePanel(ImmediateUI immediateUI) {
        immediateUI.removePanel();
    }

    public void goBack(ImmediateUI immediateUI) {
        if (immediateUI != null) {
            immediateUI.setPanel(this.prevPanel);
        } else {
            JGemsHelper.getLogger().warn("Couldn't go back to NULL UI panel!");
        }
    }
}
