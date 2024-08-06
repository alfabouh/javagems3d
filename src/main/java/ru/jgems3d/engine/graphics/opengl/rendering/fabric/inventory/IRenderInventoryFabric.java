package ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory;

import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.inventory.items.InventoryItem;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;

public interface IRenderInventoryFabric {
    void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData);

    void preRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData);

    void postRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData);
}
