package ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.render;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderPlayer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.inventory.items.ItemZippo;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;

public class AbstractInventoryZippo extends AbstractInventoryItem {
    private final MeshDataGroup model1;
    private final MeshDataGroup model2;

    public AbstractInventoryZippo() {
        Mesh mesh = MeshHelper.generatePlane3DMesh(new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f));
        Material material1 = Material.createDefault();
        Material material2 = Material.createDefault();
        material1.setDiffuse(JGemsResourceManager.globalTextureAssets.zippo1);
        material1.setEmissionMap(JGemsResourceManager.globalTextureAssets.zippo1_emission);
        material2.setDiffuse(JGemsResourceManager.globalTextureAssets.zippo2);
        ModelNode modelNode1 = new ModelNode(mesh, material1);
        ModelNode modelNode2 = new ModelNode(mesh, material2);
        this.model1 = new MeshDataGroup(modelNode1);
        this.model2 = new MeshDataGroup(modelNode2);
    }

    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, ru.jgems3d.engine.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        ItemZippo itemZippo = (ItemZippo) inventoryItem;
        float d1 = (float) (Math.cos(RenderPlayer.stepBobbing * 0.1f) * 0.051f);
        super.performTransformations(new Vector3f(0.1f, -1.0f + d1, -1.4f), new Vector3f(0.0f, (float) Math.toRadians(20.0f), 0.0f), new Vector3f(1.0f), inventoryItemRenderData);
        inventoryItemRenderData.getShaderManager().performUniform("use_emission", itemZippo.isOpened());
        super.renderInventoryModel(itemZippo.isOpened() ? this.model1 : this.model2, inventoryItemRenderData.getShaderManager());
    }

    @Override
    public void preRender(SceneRenderBase sceneRenderBase, ru.jgems3d.engine.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        super.preRender(sceneRenderBase, inventoryItem, inventoryItemRenderData);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void postRender(SceneRenderBase sceneRenderBase, ru.jgems3d.engine.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        super.postRender(sceneRenderBase, inventoryItem, inventoryItemRenderData);
        GL30.glDisable(GL30.GL_BLEND);
    }
}
