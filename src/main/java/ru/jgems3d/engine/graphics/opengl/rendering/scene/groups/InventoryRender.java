package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;
import ru.jgems3d.engine.inventory.IInventoryOwner;
import ru.jgems3d.engine.inventory.items.InventoryItem;
import ru.jgems3d.engine.physics.entities.player.Player;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.opengl.camera.FreeCamera;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class InventoryRender extends SceneRenderBase {
    public InventoryRender(JGemsOpenGLRenderer sceneRender) {
        super(-1, sceneRender, new RenderGroup("INVENTORY"));
    }

    public void onRender(float partialTicks) {
        Player player = JGems3D.get().getPlayer();
        if (player instanceof IInventoryOwner && !(JGemsHelper.getCurrentCamera() instanceof FreeCamera)) {
            IInventoryOwner hasInventory = (IInventoryOwner) player;
            InventoryItem current = hasInventory.inventory().getCurrentItem();
            if (hasInventory.inventory().getCurrentItem() != null && JGemsResourceManager.inventoryItemRenderTable.hasRender(current)) {
                InventoryItemRenderData inventoryItemRenderData = JGemsResourceManager.inventoryItemRenderTable.getMap().get(current.getClass());
                if (inventoryItemRenderData.getRenderFabric() != null) {
                    inventoryItemRenderData.getRenderFabric().preRender(this, current, inventoryItemRenderData);
                    inventoryItemRenderData.getRenderFabric().onRender(partialTicks, this, current, inventoryItemRenderData);
                    inventoryItemRenderData.getRenderFabric().postRender(this, current, inventoryItemRenderData);
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