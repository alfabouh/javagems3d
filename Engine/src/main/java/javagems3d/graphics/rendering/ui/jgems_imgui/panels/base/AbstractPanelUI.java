/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
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
            ui.setUiPanel(this.prevPanel);
        } else {
            Log.get().warn("Couldn't go back to NULL UI panel");
        }
    }
}
