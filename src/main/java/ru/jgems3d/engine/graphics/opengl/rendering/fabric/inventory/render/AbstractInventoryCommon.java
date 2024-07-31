package ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.render;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderPlayer;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;

public class AbstractInventoryCommon extends AbstractInventoryItem {
    private final MeshDataGroup model1;

    public AbstractInventoryCommon(TextureSample diffuse) {
        Mesh mesh = MeshHelper.generatePlane3DMesh(new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f));
        Material material1 = Material.createDefault();
        material1.setDiffuse(diffuse);
        ModelNode modelNode1 = new ModelNode(mesh, material1);
        this.model1 = new MeshDataGroup(modelNode1);
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, ru.jgems3d.engine.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        float d1 = (float) (Math.cos(RenderPlayer.stepBobbing * 0.1f) * 0.051f);
        super.performTransformations(new Vector3f(0.1f, -1.0f + d1, -1.4f), new Vector3f(0.0f, (float) Math.toRadians(20.0f), 0.0f), new Vector3f(1.0f), inventoryItemRenderData);
        super.renderInventoryModel(this.model1, inventoryItemRenderData.getShaderManager());
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
