package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.inventory;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.inventory.items.InventoryItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base.IRenderInventoryItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.render.transformation.Transformation;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.IImageSample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public abstract class InventoryItemObject implements IRenderInventoryItem {
    protected void performTransformations(Vector3f pos, Vector3f rot, Vector3f scale, RenderInventoryItemData renderInventoryItemData) {
        Format3D format3D = new Format3D();
        format3D.setPosition(pos);
        format3D.setRotation(rot);
        format3D.setScaling(scale);
        renderInventoryItemData.getShaderManager().getUtils().performModel3DViewMatrix(Transformation.getModelMatrix(format3D));
    }

    @Override
    public void preRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        GL30.glDepthFunc(GL30.GL_ALWAYS);
        renderInventoryItemData.getShaderManager().bind();
        renderInventoryItemData.getShaderManager().performUniform("projection_matrix", Transformation.getPerspectiveMatrix(JGems.get().getScreen().getWindow(), JGemsSceneUtils.FOV, 0.1f, 10.0f));
    }

    @Override
    public void postRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        GL30.glDepthFunc(GL30.GL_LESS);
        renderInventoryItemData.getShaderManager().unBind();
    }

    protected void renderInventoryModel(MeshDataGroup meshDataGroup, JGemsShaderManager shaderManager) {
        for (ModelNode modelNode : meshDataGroup.getModelNodeList()) {
            IImageSample sample1 = (IImageSample) modelNode.getMaterial().getDiffuse();
            IImageSample sample2 = modelNode.getMaterial().getEmissive();
            if (sample1 != null) {
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                sample1.bindTexture();
                shaderManager.performUniform("diffuse_map", 0);
            }
            if (sample2 != null) {
                GL30.glActiveTexture(GL30.GL_TEXTURE1);
                sample2.bindTexture();
                shaderManager.performUniform("emissive_map", 1);
            }
            GL30.glBindVertexArray(modelNode.getMesh().getVao());
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glEnableVertexAttribArray(a);
            }
            GL30.glDrawElements(GL30.GL_TRIANGLES, modelNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glDisableVertexAttribArray(a);
            }
            GL30.glBindVertexArray(0);
        }
    }
}
