package ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.render;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.IRenderInventoryFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.transformation.Transformation;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public abstract class AbstractInventoryItem implements IRenderInventoryFabric {
    protected void performTransformations(Vector3f pos, Vector3f rot, Vector3f scale, InventoryItemRenderData inventoryItemRenderData) {
        Format3D format3D = new Format3D();
        format3D.setPosition(pos);
        format3D.setRotation(rot);
        format3D.setScaling(scale);
        inventoryItemRenderData.getShaderManager().getUtils().performModel3DMatrix(Transformation.getModelMatrix(format3D));
    }

    @Override
    public void preRender(SceneRenderBase sceneRenderBase, ru.jgems3d.engine.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        GL30.glDepthFunc(GL30.GL_ALWAYS);
        inventoryItemRenderData.getShaderManager().bind();
        inventoryItemRenderData.getShaderManager().performUniform(new UniformString("projection_matrix"), Transformation.getPerspectiveMatrix(JGems3D.get().getScreen().getWindow(), JGemsSceneGlobalConstants.FOV, 0.1f, 10.0f));
    }

    @Override
    public void postRender(SceneRenderBase sceneRenderBase, ru.jgems3d.engine.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        GL30.glDepthFunc(GL30.GL_LESS);
        inventoryItemRenderData.getShaderManager().unBind();
    }

    protected void renderInventoryModel(MeshDataGroup meshDataGroup, JGemsShaderManager shaderManager) {
        for (ModelNode modelNode : meshDataGroup.getModelNodeList()) {
            IImageSample sample1 = (IImageSample) modelNode.getMaterial().getDiffuse();
            IImageSample sample2 = modelNode.getMaterial().getEmissionMap();
            if (sample1 != null) {
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                sample1.bindTexture();
                shaderManager.performUniform(new UniformString("diffuse_map"), 0);
            }
            if (sample2 != null) {
                GL30.glActiveTexture(GL30.GL_TEXTURE1);
                sample2.bindTexture();
                shaderManager.performUniform(new UniformString("emission_map"), 1);
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
