package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels;

import org.joml.Vector2f;
import org.joml.Vector2i;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.inventory.Inventory;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.player.KinematicPlayerSP;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.common.PhysPlank;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.base.AbstractPanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.base.PanelUI;
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
            int textWidth = ImmediateUI.getTextWidth(JGemsResourceManager.renderAssets.standardFont, text);
            immediateUI.textUI(text, JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 - textWidth / 2, windowH / 2 - 120), 0xffffff, 0.5f);
        }

        if (GamePlayPanel.show_shift) {
            if (JGems.get().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput().y < 0) {
                GamePlayPanel.show_shift = false;
            }
            String text = JGems.get().I18n("gameplay.shift_tip");
            int textWidth = ImmediateUI.getTextWidth(JGemsResourceManager.renderAssets.standardFont, text);
            immediateUI.textUI(text, JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 - textWidth / 2, windowH / 2 - 140), 0xffffff, 0.5f);
        }

        if (entityPlayerSP instanceof KinematicPlayerSP) {
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) entityPlayerSP;

            if (kinematicPlayerSP.getCurrentSelectedItem() instanceof PhysPlank) {
                String text = JGems.get().I18n("gameplay.crowbar_use");
                int textWidth = ImmediateUI.getTextWidth(JGemsResourceManager.renderAssets.standardFont, text);
                immediateUI.textUI(text, JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 - textWidth / 2, windowH / 2 - 100), 0xffc8c8, 0.5f);
            }

            Inventory inventory = kinematicPlayerSP.inventory();
            int j = 0;

            String cdText = JGems.get().I18n("gameplay.disks", "[" + kinematicPlayerSP.getPickedCds() + " / " + kinematicPlayerSP.getMaxCds() + "]");
            String cassetteText = JGems.get().I18n("gameplay.cassettes", "[" + kinematicPlayerSP.getPrickedCassettes() + " / " + kinematicPlayerSP.getMaxCassettes() + "]");

            int textWidth1 = ImmediateUI.getTextWidth(JGemsResourceManager.renderAssets.standardFont, cdText);
            int textWidth2 = ImmediateUI.getTextWidth(JGemsResourceManager.renderAssets.standardFont, cassetteText);
            int maxW = Math.max(textWidth1, textWidth2);

            immediateUI.textUI(cdText, JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW - maxW - 20, 200), 0xffee22, 0.5f);
            immediateUI.textUI(cassetteText, JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW - maxW - 20, 240), 0xffee22, 0.5f);

            if (inventory.getCurrentItem() != null && inventory.getCurrentItem().getDescription() != null) {
                immediateUI.textUI(inventory.getCurrentItem().getDescription(), JGemsResourceManager.renderAssets.standardFont, new Vector2i(80, 80), 0xffffff, 0.5f);
            }

            for (Inventory.Slot slot : inventory.getInventorySlots()) {
                if (slot.getInventoryItem() == null) {
                    continue;
                }
                RenderInventoryItemData renderInventoryItemData = JGemsResourceManager.renderDataAssets.inventoryItemRenderTable.getMap().get(slot.getInventoryItem().getClass());
                immediateUI.imageUI(renderInventoryItemData.getInventoryIcon(), new Vector2i(64 + (96 * j++), windowH - 112), new Vector2i(96), 0.5f);
                immediateUI.textUI("[" + j + "]", JGemsResourceManager.renderAssets.standardFont, new Vector2i(94 * j, windowH - 132), inventory.getCurrentSlot() == slot.getId() ? 0xff0000 : 0xffffff, 0.5f);
            }

            if (kinematicPlayerSP.isHasSoda()) {
                immediateUI.imageUI(JGemsResourceManager.renderAssets.soda_inventory, new Vector2i(windowW - 120, 50), new Vector2i(96), 0.5f);
                immediateUI.textUI(JGems.get().I18n("gameplay.press_x"), JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW - 140, 26), 0xffffff, 0.5f);
            }
        }

        immediateUI.textUI(JGems.get().I18n("gameplay.stamina"), JGemsResourceManager.renderAssets.standardFont, new Vector2i(42, windowH - 190), 0xffffff, 0.5f);
        immediateUI.textUI(JGems.get().I18n("gameplay.mind"), JGemsResourceManager.renderAssets.standardFont, new Vector2i(42, windowH - 250), 0xffffff, 0.5f);
    }

    private void renderImagesOnScreen(ImmediateUI immediateUI, float partialTicks) {
        Window window = JGems.get().getScreen().getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        float stamina = ((KinematicPlayerSP) JGems.get().getPlayerSP()).getStamina();
        float mind = ((KinematicPlayerSP) JGems.get().getPlayerSP()).getMind();

        immediateUI.scale(new Vector2f(2.0f));
        immediateUI.imageUI(JGemsResourceManager.renderAssets.gui1, new Vector2i(42, windowH - 160), new Vector2f(0.0f), new Vector2f(101.0f, 6.0f), 0.5f);
        immediateUI.imageUI(JGemsResourceManager.renderAssets.gui1, new Vector2i(42, windowH - 160), new Vector2f(0.0f, 6.0f), new Vector2f(101.0f * stamina, 6.0f), 0.5f);

        immediateUI.imageUI(JGemsResourceManager.renderAssets.gui1, new Vector2i(42, windowH - 220), new Vector2f(0.0f), new Vector2f(101.0f, 6.0f), 0.5f);
        immediateUI.imageUI(JGemsResourceManager.renderAssets.gui1, new Vector2i(42, windowH - 220), new Vector2f(0.0f, 6.0f), new Vector2f(101.0f * mind, 6.0f), 0.5f);
        immediateUI.defaultScale();

        int crossSize = 32;
        immediateUI.imageUI(JGemsResourceManager.renderAssets.crosshair, new Vector2i(windowW / 2 - crossSize / 2, windowH / 2 - crossSize / 2), new Vector2i(crossSize), 0.5f);
    }
}
