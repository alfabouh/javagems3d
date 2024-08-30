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

package ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels;

import org.joml.Vector2i;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.inventory.Inventory;
import ru.jgems3d.engine.physics.entities.player.SimpleKinematicPlayer;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.AbstractPanelUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.system.resources.assets.loaders.TextureAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.material.samples.TextureSample;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class DefaultGamePanel extends AbstractPanelUI {
    public DefaultGamePanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, float frameDeltaTicks) {
        this.renderTextOnScreen(immediateUI, frameDeltaTicks);
        this.renderImagesOnScreen(immediateUI, frameDeltaTicks);
    }

    protected void renderTextOnScreen(ImmediateUI immediateUI, float frameDeltaTicks) {
        Window window = immediateUI.getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        final WorldItem entityPlayerSP = JGems3D.get().getPlayer();

        if (entityPlayerSP instanceof SimpleKinematicPlayer) {
            SimpleKinematicPlayer dynamicPlayer = (SimpleKinematicPlayer) entityPlayerSP;

            Inventory inventory = dynamicPlayer.inventory();
            int j = 0;

            if (inventory.getCurrentItem() != null && inventory.getCurrentItem().getDescription() != null) {
                immediateUI.textUI(inventory.getCurrentItem().getDescription(), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(80, 80), 0xffffff, 0.5f);
            }

            for (Inventory.Slot slot : inventory.getInventorySlots()) {
                if (slot.getInventoryItem() == null) {
                    continue;
                }
                InventoryItemRenderData inventoryItemRenderData = JGemsResourceManager.inventoryItemRenderTable.getMap().get(slot.getInventoryItem().getClass());

                TextureSample sample = inventoryItemRenderData == null ? null : inventoryItemRenderData.getInventoryIcon();
                if (sample == null) {
                    sample = TextureAssetsLoader.DEFAULT;
                }
                immediateUI.imageUI(sample, new Vector2i(64 + (96 * j++), windowH - 112), new Vector2i(96), 0.5f);

                immediateUI.textUI("[" + j + "]", JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(94 * j, windowH - 132), inventory.getCurrentSlot() == slot.getId() ? 0xff0000 : 0xffffff, 0.5f);
            }
        }
    }

    protected void renderImagesOnScreen(ImmediateUI immediateUI, float frameDeltaTicks) {
        Window window = immediateUI.getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        int crossSize = 32;
        immediateUI.imageUI(JGemsResourceManager.globalTextureAssets.crosshair, new Vector2i(windowW / 2 - crossSize / 2, windowH / 2 - crossSize / 2), new Vector2i(crossSize), 0.5f);
    }
}
