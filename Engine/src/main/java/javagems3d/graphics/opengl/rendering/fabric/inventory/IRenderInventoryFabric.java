/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.rendering.fabric.inventory;

import javagems3d.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.system.inventory.items.InventoryItem;

public interface IRenderInventoryFabric {
    void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData);

    void preRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData);

    void postRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData);
}
