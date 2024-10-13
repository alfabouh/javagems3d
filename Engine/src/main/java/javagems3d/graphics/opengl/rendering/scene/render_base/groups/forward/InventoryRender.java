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

package javagems3d.graphics.opengl.rendering.scene.render_base.groups.forward;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.camera.FreeControlledCamera;
import javagems3d.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.render_base.RenderGroup;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.system.inventory.IInventoryOwner;
import javagems3d.system.inventory.items.InventoryItem;
import javagems3d.system.resources.manager.JGemsResourceManager;

public class InventoryRender extends SceneRenderBase {
    public InventoryRender(JGemsOpenGLRenderer sceneRender) {
        super(-1, sceneRender, new RenderGroup("INVENTORY_FORWARD"));
    }

    public void onRender(FrameTicking frameTicking) {
        IPlayer player = JGems3D.get().getPlayer();
        if (player instanceof IInventoryOwner && !(JGemsHelper.CAMERA.getCurrentCamera() instanceof FreeControlledCamera)) {
            IInventoryOwner hasInventory = (IInventoryOwner) player;
            InventoryItem current = hasInventory.getInventory().getCurrentItem();
            if (hasInventory.getInventory().getCurrentItem() != null && JGemsResourceManager.inventoryItemRenderTable.hasRender(current)) {
                InventoryItemRenderData inventoryItemRenderData = JGemsResourceManager.inventoryItemRenderTable.getMap().get(current.getClass());
                if (inventoryItemRenderData.getRenderFabric() != null) {
                    inventoryItemRenderData.getRenderFabric().preRender(this, current, inventoryItemRenderData);
                    inventoryItemRenderData.getRenderFabric().onRender(frameTicking, this, current, inventoryItemRenderData);
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