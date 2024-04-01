package ru.BouH.engine.render.scene.fabric.render.base;

import ru.BouH.engine.inventory.items.InventoryItem;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;

public interface IRenderInventoryItem {
    void onRender(double partialTicks, SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData);

    void preRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData);

    void postRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData);
}
