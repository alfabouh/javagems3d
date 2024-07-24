package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.panels;

import org.joml.Vector2i;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.inventory.Inventory;
import ru.alfabouh.jgems3d.engine.physics.entities.player.KinematicPlayer;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.RenderInventoryItemData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.AbstractPanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class GamePlayPanel extends AbstractPanelUI {
    public static boolean show_wasd = true;
    public static boolean show_shift = true;


    public GamePlayPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, float partialTicks) {
        this.renderTextOnScreen(immediateUI, partialTicks);
        this.renderImagesOnScreen(immediateUI, partialTicks);
    }

    private void renderTextOnScreen(ImmediateUI immediateUI, float partialTicks) {
        Window window = JGems.get().getScreen().getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        final WorldItem entityPlayerSP = (WorldItem) JGems.get().getPlayerSP();

        if (GamePlayPanel.show_wasd) {
            if (JGems.get().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput().mul(1, 0, 1).length() > 0) {
                GamePlayPanel.show_wasd = false;
            }
            String text = JGems.get().I18n("gameplay.walk_tip");
            int textWidth = ImmediateUI.getTextWidth(JGemsResourceManager.textureAssets.standardFont, text);
            immediateUI.textUI(text, JGemsResourceManager.textureAssets.standardFont, new Vector2i(windowW / 2 - textWidth / 2, windowH / 2 - 120), 0xffffff, 0.5f);
        }

        if (GamePlayPanel.show_shift) {
            if (JGems.get().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput().y < 0) {
                GamePlayPanel.show_shift = false;
            }
            String text = JGems.get().I18n("gameplay.shift_tip");
            int textWidth = ImmediateUI.getTextWidth(JGemsResourceManager.textureAssets.standardFont, text);
            immediateUI.textUI(text, JGemsResourceManager.textureAssets.standardFont, new Vector2i(windowW / 2 - textWidth / 2, windowH / 2 - 140), 0xffffff, 0.5f);
        }

        if (entityPlayerSP instanceof KinematicPlayer) {
            KinematicPlayer dynamicPlayer = (KinematicPlayer) entityPlayerSP;

            Inventory inventory = dynamicPlayer.inventory();
            int j = 0;


            if (inventory.getCurrentItem() != null && inventory.getCurrentItem().getDescription() != null) {
                immediateUI.textUI(inventory.getCurrentItem().getDescription(), JGemsResourceManager.textureAssets.standardFont, new Vector2i(80, 80), 0xffffff, 0.5f);
            }

            for (Inventory.Slot slot : inventory.getInventorySlots()) {
                if (slot.getInventoryItem() == null) {
                    continue;
                }
                RenderInventoryItemData renderInventoryItemData = JGemsResourceManager.renderDataAssets.inventoryItemRenderTable.getMap().get(slot.getInventoryItem().getClass());
                immediateUI.imageUI(renderInventoryItemData.getInventoryIcon(), new Vector2i(64 + (96 * j++), windowH - 112), new Vector2i(96), 0.5f);
                immediateUI.textUI("[" + j + "]", JGemsResourceManager.textureAssets.standardFont, new Vector2i(94 * j, windowH - 132), inventory.getCurrentSlot() == slot.getId() ? 0xff0000 : 0xffffff, 0.5f);
            }
        }
    }

    private void renderImagesOnScreen(ImmediateUI immediateUI, float partialTicks) {
        Window window = JGems.get().getScreen().getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        int crossSize = 32;
        immediateUI.imageUI(JGemsResourceManager.textureAssets.crosshair, new Vector2i(windowW / 2 - crossSize / 2, windowH / 2 - crossSize / 2), new Vector2i(crossSize), 0.5f);
    }
}
