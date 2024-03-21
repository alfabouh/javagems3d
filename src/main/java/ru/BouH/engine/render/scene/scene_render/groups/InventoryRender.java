package ru.BouH.engine.render.scene.scene_render.groups;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.inventory.IHasInventory;
import ru.BouH.engine.inventory.items.InventoryItem;
import ru.BouH.engine.physics.entities.player.IPlayer;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.BouH.engine.render.scene.scene_render.RenderGroup;
import ru.BouH.engine.render.scene.world.camera.FreeCamera;

public class InventoryRender extends SceneRenderBase {
    public InventoryRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(100, sceneRenderConveyor, new RenderGroup("INVENTORY", true));
    }

    public void onRender(double partialTicks) {
        IPlayer player = Game.getGame().getPlayerSP();
        if (player instanceof IHasInventory && !(Game.getGame().getScreen().getCamera() instanceof FreeCamera)) {
            IHasInventory hasInventory = (IHasInventory) player;
            InventoryItem current = hasInventory.inventory().getCurrentItem();
            if (hasInventory.inventory().getCurrentItem() != null && ResourceManager.renderDataAssets.inventoryItemRenderTable.hasRender(current)) {
                RenderInventoryItemData renderInventoryItemData = ResourceManager.renderDataAssets.inventoryItemRenderTable.getMap().get(current.getClass());
                renderInventoryItemData.getRenderFabric().preRender(this, current, renderInventoryItemData);
                renderInventoryItemData.getRenderFabric().onRender(partialTicks, this, current, renderInventoryItemData);
                renderInventoryItemData.getRenderFabric().postRender(this, current, renderInventoryItemData);
            }
        }
    }

    public void onStartRender() {
    }

    public void onStopRender() {
    }
}