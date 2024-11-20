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

package javagems3d.graphics.opengl.rendering.fabric.inventory.render;

import javagems3d.system.resources.assets.models.mesh.DirectRenderMesh;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.system.resources.old.MaterialOld;
import javagems3d.system.resources.assets.material.samples.TextureSample;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.old.MeshGroup;

public class InventoryCommon extends AbstractInventoryItem {
    protected final MeshGroup model1;

    public InventoryCommon(TextureSample diffuse) {
        DirectRenderMesh directRenderMesh = MeshHelper.generatePlane3DMesh(new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f));
        MaterialOld material1 = MaterialOld.createDefault();
        material1.setDiffuse(diffuse);
        MeshGroup.Node meshNode1 = new MeshGroup.Node(directRenderMesh, material1);
        this.model1 = new MeshGroup(meshNode1);
    }

    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, javagems3d.system.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        super.performTransformations(new Vector3f(0.1f, -1.0f, -1.4f), new Vector3f(0.0f, (float) Math.toRadians(20.0f), 0.0f), new Vector3f(1.0f), inventoryItemRenderData);
        super.renderInventoryModel(this.model1, inventoryItemRenderData.getShaderManager());
    }

    @Override
    public void preRender(SceneRenderBase sceneRenderBase, javagems3d.system.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        super.preRender(sceneRenderBase, inventoryItem, inventoryItemRenderData);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void postRender(SceneRenderBase sceneRenderBase, javagems3d.system.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        super.postRender(sceneRenderBase, inventoryItem, inventoryItemRenderData);
        GL46.glDisable(GL46.GL_BLEND);
    }
}
