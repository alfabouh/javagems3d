package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.alfabouh.jgems3d.engine.inventory.IInventoryOwner;
import ru.alfabouh.jgems3d.engine.inventory.items.InventoryItem;
import ru.alfabouh.jgems3d.engine.physics.entities.player.IPlayer;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.RenderInventoryItemData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.camera.FreeCamera;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class InventoryRender extends SceneRenderBase {
    public InventoryRender(JGemsOpenGLRenderer sceneRenderConveyor) {
        super(100, sceneRenderConveyor, new RenderGroup("INVENTORY"));
    }

    public void onRender(float partialTicks) {
        IPlayer player = JGems.get().getPlayerSP();
        if (player instanceof IInventoryOwner && !(JGems.get().getScreen().getCamera() instanceof FreeCamera)) {
            IInventoryOwner hasInventory = (IInventoryOwner) player;
            InventoryItem current = hasInventory.inventory().getCurrentItem();
            if (hasInventory.inventory().getCurrentItem() != null && JGemsResourceManager.renderDataAssets.inventoryItemRenderTable.hasRender(current)) {
                RenderInventoryItemData renderInventoryItemData = JGemsResourceManager.renderDataAssets.inventoryItemRenderTable.getMap().get(current.getClass());
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