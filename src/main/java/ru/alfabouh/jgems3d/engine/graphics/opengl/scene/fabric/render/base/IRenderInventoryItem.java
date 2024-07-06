package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base;

import ru.alfabouh.jgems3d.engine.inventory.items.InventoryItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.inventory.RenderInventoryItemData;

public interface IRenderInventoryItem {
    void onRender(float partialTicks, SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData);

    void preRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData);

    void postRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData);
}
