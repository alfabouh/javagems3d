package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.inventory;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.inventory.items.InventoryItem;
import ru.alfabouh.jgems3d.engine.inventory.items.ItemEmp;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.objects.RenderPlayerSP;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;

public class InventoryEmpObject extends InventoryItemObject {
    private final MeshDataGroup[] models = new MeshDataGroup[6];

    public InventoryEmpObject() {
        Mesh mesh = MeshHelper.generatePlane3DMesh(new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f));
        for (int i = 0; i < 6; i++) {
            Material material1 = Material.createDefault();
            material1.setDiffuse(JGemsResourceManager.renderAssets.emp[i]);
            ModelNode modelNode1 = new ModelNode(mesh, material1);
            this.models[i] = new MeshDataGroup(modelNode1);
        }
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        ItemEmp itemEmp = (ItemEmp) inventoryItem;
        float d1 = (float) (Math.cos(RenderPlayerSP.stepBobbing * 0.1f) * 0.051f);
        super.performTransformations(new Vector3f(0.1f, -1.0f + d1, -1.4f), new Vector3f(0.0f, (float) Math.toRadians(20.0f), 0.0f), new Vector3f(1.0f), renderInventoryItemData);
        super.renderInventoryModel(this.models[itemEmp.getLevel()], renderInventoryItemData.getShaderManager());
    }

    @Override
    public void preRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        super.preRender(sceneRenderBase, inventoryItem, renderInventoryItemData);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void postRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        super.postRender(sceneRenderBase, inventoryItem, renderInventoryItemData);
        GL30.glDisable(GL30.GL_BLEND);
    }
}
