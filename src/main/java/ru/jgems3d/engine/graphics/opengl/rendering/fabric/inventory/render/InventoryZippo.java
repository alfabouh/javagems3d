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

package ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.render;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.graphics.opengl.screen.timer.JGemsTimer;
import ru.jgems3d.engine.system.inventory.items.ItemZippo;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.models.helper.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class InventoryZippo extends AbstractInventoryItem {
    protected final MeshDataGroup model1;
    protected final MeshDataGroup model2;

    protected final JGemsTimer jGemsTimer;
    protected int animState;

    public InventoryZippo() {
        this.jGemsTimer = JGemsHelper.createTimer();

        Mesh mesh = MeshHelper.generatePlane3DMesh(new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f));
        Material material1 = Material.createDefault();
        Material material2 = Material.createDefault();
        material2.setDiffuse(JGemsResourceManager.globalTextureAssets.zippo2);
        ModelNode modelNode1 = new ModelNode(mesh, material1);
        ModelNode modelNode2 = new ModelNode(mesh, material2);
        this.model1 = new MeshDataGroup(modelNode1);
        this.model2 = new MeshDataGroup(modelNode2);
    }

    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, ru.jgems3d.engine.system.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        this.animate();
        ItemZippo itemZippo = (ItemZippo) inventoryItem;
        super.performTransformations(new Vector3f(0.1f, -1.0f, -1.4f), new Vector3f(0.0f, (float) Math.toRadians(20.0f), 0.0f), new Vector3f(1.0f), inventoryItemRenderData);
        super.renderInventoryModel(itemZippo.isOpened() ? this.model1 : this.model2, inventoryItemRenderData.getShaderManager());
    }

    protected void animate() {
        if (this.jGemsTimer.resetTimerAfterReachedSeconds(0.3f)) {
            this.animState = this.animState == 0 ? 1 : 0;
        }
        this.model1.getModelNodeList().get(0).getMaterial().setDiffuse(this.animState == 0 ? JGemsResourceManager.globalTextureAssets.zippo1 : JGemsResourceManager.globalTextureAssets.zippo1_1);
    }

    @Override
    public void preRender(SceneRenderBase sceneRenderBase, ru.jgems3d.engine.system.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        super.preRender(sceneRenderBase, inventoryItem, inventoryItemRenderData);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void postRender(SceneRenderBase sceneRenderBase, ru.jgems3d.engine.system.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        super.postRender(sceneRenderBase, inventoryItem, inventoryItemRenderData);
        GL30.glDisable(GL30.GL_BLEND);
    }
}
