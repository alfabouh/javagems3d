package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.jgems3d.engine.inventory.IInventoryOwner;
import ru.jgems3d.engine.inventory.items.InventoryItem;
import ru.jgems3d.engine.physics.entities.player.Player;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.RenderInventoryItemData;
import ru.jgems3d.engine.graphics.opengl.camera.FreeCamera;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class InventoryRender extends SceneRenderBase {
    public InventoryRender(JGemsOpenGLRenderer sceneRenderConveyor) {
        super(100, sceneRenderConveyor, new RenderGroup("INVENTORY"));
    }

    public void onRender(float partialTicks) {
        Player player = JGems.get().getPlayer();
        if (player instanceof IInventoryOwner && !(JGems.get().getScreen().getCamera() instanceof FreeCamera)) {
            IInventoryOwner hasInventory = (IInventoryOwner) player;
            InventoryItem current = hasInventory.inventory().getCurrentItem();
            if (hasInventory.inventory().getCurrentItem() != null && JGemsResourceManager.globalRenderDataAssets.inventoryItemRenderTable.hasRender(current)) {
                RenderInventoryItemData renderInventoryItemData = JGemsResourceManager.globalRenderDataAssets.inventoryItemRenderTable.getMap().get(current.getClass());
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