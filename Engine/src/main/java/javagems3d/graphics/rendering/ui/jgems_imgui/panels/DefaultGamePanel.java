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

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.screen.window.IWindow;
import org.joml.Vector2f;
import org.joml.Vector2i;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.AbstractPanelUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class DefaultGamePanel extends AbstractPanelUI {
    public DefaultGamePanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(JGemsUI ui, float frameDeltaTicks) {
        this.renderTextOnScreen(ui, frameDeltaTicks);
        this.renderImagesOnScreen(ui, frameDeltaTicks);
    }

    @Override
    public String getPanelID() {
        return "default_game_panel";
    }

    protected void renderTextOnScreen(JGemsUI ui, float frameDeltaTicks) {
        IWindow window = ui.getWindow();
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        //final WorldItem entityPlayerSP = (WorldItem) JGems3D.get().getPlayer();

       //if (entityPlayerSP instanceof InventoryOwner) {
       //    InventoryOwner dynamicPlayer = (InventoryOwner) entityPlayerSP;

       //    InventoryBase inventory = dynamicPlayer.getInventory();
       //    int j = 0;

       //    if (inventory.getCurrentItem() != null && inventory.getCurrentItem().getDescription() != null) {
       //        JGemsUI.textUI(inventory.getCurrentItem().getDescription(), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(80, 80), 0xffffff, 0.5f);
       //    }

       //    for (InventoryBase.Slot slot : inventory.getInventorySlots()) {
       //        if (slot.getInventoryItem() == null) {
       //            continue;
       //        }
       //        InventoryItemRenderData inventoryItemRenderData = JGemsResourceManager.inventoryItemRenderTable.getMap().get(slot.getInventoryItem().getClass());

       //        IReloadableSample sample = inventoryItemRenderData == null ? null : inventoryItemRenderData.getInventoryIcon();
       //        if (sample == null) {
       //            sample = ResourceManager.DEFAULT_TEXTURE();
       //        }
       //        JGemsUI.imageUI(sample, new Vector2i(64 + (96 * j++), windowH - 112), new Vector2i(96), 0.5f);

       //        JGemsUI.textUI("[" + j + "]", JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(94 * j, windowH - 132), inventory.getCurrentSlot() == slot.getId() ? 0xff0000 : 0xffffff, 0.5f);
       //    }
       //}
    }

    protected void renderImagesOnScreen(JGemsUI ui, float frameDeltaTicks) {
        IWindow window = ui.getWindow();
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        int crossSize = 32;
        ui.imageUI(JGemsResourceManager.globalTextureAssets.crosshair, new Vector2f(windowW / 2f - crossSize / 2f, windowH / 2f - crossSize / 2f), new Vector2f(32f), 0.5f);
    }
}
