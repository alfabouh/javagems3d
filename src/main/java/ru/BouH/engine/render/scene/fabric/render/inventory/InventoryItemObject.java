package ru.BouH.engine.render.scene.fabric.render.inventory;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.assets.materials.textures.IImageSample;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.assets.models.mesh.ModelNode;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.inventory.items.InventoryItem;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.render.base.IRenderInventoryItem;
import ru.BouH.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.BouH.engine.render.transformation.TransformationManager;

public abstract class InventoryItemObject implements IRenderInventoryItem {
    protected void performTransformations(Vector3d pos, Vector3d rot, Vector3d scale, RenderInventoryItemData renderInventoryItemData) {
        Format3D format3D = new Format3D();
        format3D.setPosition(pos);
        format3D.setRotation(rot);
        format3D.setScale(scale);
        renderInventoryItemData.getShaderManager().getUtils().performModelMatrix3d(TransformationManager.instance.getModelMatrix(format3D, false));
    }

    @Override
    public void preRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        GL30.glDepthFunc(GL30.GL_ALWAYS);
        renderInventoryItemData.getShaderManager().bind();
        renderInventoryItemData.getShaderManager().performUniform("projection_matrix", TransformationManager.instance.getProjectionMatrixFpv());
    }

    @Override
    public void postRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        GL30.glDepthFunc(GL30.GL_LESS);
        renderInventoryItemData.getShaderManager().unBind();
    }

    protected void renderInventoryModel(MeshDataGroup meshDataGroup, ShaderManager shaderManager) {
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
