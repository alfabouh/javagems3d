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

package javagems3d.graphics.rendering.ui.jgems_imgui.panels;

import javagems3d.graphics.screen.window.IWindow;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.AbstractPanelUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class DefaultLeaveConfirmationPanel extends AbstractPanelUI {
    public DefaultLeaveConfirmationPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(JGemsUI ui, float frameDeltaTicks) {
        DefaultMainMenuPanel.renderMenuBackGround(new Vector3f(1.0f, 0.2f, 1.0f));
        IWindow window = ui.getWindow();
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        String text = JGems3D.get().I18n("menu.confirm.text");
        int textSize = JGemsUI.getTextWidth(JGemsResourceManager.globalTextureAssets.standardFont, text);
        ui.textUI(text, JGemsResourceManager.globalTextureAssets.standardFont, new Vector2f(windowW / 2f - textSize / 2f, windowH / 2f - 60), 0xffffff, 0.5f);

        ui.buttonUI(JGems3D.get().I18n("menu.confirm.yes"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2f(windowW / 2f + 5, windowH / 2f), new Vector2f(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.close(null);
                });
        ui.buttonUI(JGems3D.get().I18n("menu.confirm.no"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2f(windowW / 2f - 205, windowH / 2f), new Vector2f(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    this.goBack(ui);
                });
    }

    @Override
    public String getPanelID() {
        return "default_leave_confirmation_panel";
    }
}
