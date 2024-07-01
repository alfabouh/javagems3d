package ru.alfabouh.jgems3d.engine.render.opengl.scene.components.groups;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.inventory.IHasInventory;
import ru.alfabouh.jgems3d.engine.inventory.items.InventoryItem;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.player.IPlayer;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.JGemsSceneRender;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.FreeCamera;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;

public class InventoryRender extends SceneRenderBase {
    public InventoryRender(JGemsSceneRender sceneRenderConveyor) {
        super(100, sceneRenderConveyor, new RenderGroup("INVENTORY"));
    }

    public void onRender(float partialTicks) {
        IPlayer player = JGems.get().getPlayerSP();
        if (player instanceof IHasInventory && !(JGems.get().getScreen().getCamera() instanceof FreeCamera)) {
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