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

package jgems_api.horror.inventory;

import jgems_api.horror.render.RenderHorrorPlayer;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.render.InventoryZippo;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.system.inventory.items.ItemZippo;

public class ZippoHorror extends InventoryZippo {
    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, ru.jgems3d.engine.system.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        this.animate();
        ItemZippo itemZippo = (ItemZippo) inventoryItem;
        float d1 = (float) (Math.cos(RenderHorrorPlayer.stepBobbing * 0.1f) * 0.051f);
        super.performTransformations(new Vector3f(0.1f, -1.0f + d1, -1.4f), new Vector3f(0.0f, (float) Math.toRadians(20.0f), 0.0f), new Vector3f(1.0f), inventoryItemRenderData);
        super.renderInventoryModel(itemZippo.isOpened() ? this.model1 : this.model2, inventoryItemRenderData.getShaderManager());
    }
}
