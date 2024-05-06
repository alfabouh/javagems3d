package ru.alfabouh.engine.render.scene.scene_render.groups;

import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.inventory.IHasInventory;
import ru.alfabouh.engine.inventory.items.InventoryItem;
import ru.alfabouh.engine.physics.entities.player.IPlayer;
import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.alfabouh.engine.render.scene.scene_render.RenderGroup;
import ru.alfabouh.engine.render.scene.world.camera.FreeCamera;

public class InventoryRender extends SceneRenderBase {
    public InventoryRender(SceneRender sceneRenderConveyor) {
        super(100, sceneRenderConveyor, new RenderGroup("INVENTORY"));
    }

    public void onRender(double partialTicks) {
        IPlayer player = Game.getGame().getPlayerSP();
        if (player instanceof IHasInventory && !(Game.getGame().getScreen().getCamera() instanceof FreeCamera)) {
            IHasInventory hasInventory = (IHasInventory) player;
            InventoryItem current = hasInventory.inventory().getCurrentItem();
            if (hasInventory.inventory().getCurrentItem() != null && ResourceManager.renderDataAssets.inventoryItemRenderTable.hasRender(current)) {
                RenderInventoryItemData renderInventoryItemData = ResourceManager.renderDataAssets.inventoryItemRenderTable.getMap().get(current.getClass());
                if (renderInventoryItemData.getRenderFabric() != null) {
                    renderInventoryItemData.getRenderFabric().preRender(this, current, renderInventoryItemData);
                    renderInventoryItemData.getRenderFabric().onRender(partialTicks, this, current, renderInventoryItemData);
                    renderInventoryItemData.getRenderFabric().postRender(this, current, renderInventoryItemData);
                }
            }
        }
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}