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

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import javagems3d.JGems3D;
import javagems3d.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import javagems3d.graphics.opengl.rendering.fabric.inventory.IRenderInventoryFabric;
import javagems3d.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.transformation.Transformation;
import javagems3d.system.resources.assets.loaders.TextureAssetsLoader;
import javagems3d.system.resources.assets.material.samples.base.ITextureSample;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.MeshDataGroup;
import javagems3d.system.resources.assets.models.mesh.ModelNode;
import javagems3d.system.resources.assets.shaders.base.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public abstract class AbstractInventoryItem implements IRenderInventoryFabric {
    protected void performTransformations(Vector3f pos, Vector3f rot, Vector3f scale, InventoryItemRenderData inventoryItemRenderData) {
        Format3D format3D = new Format3D();
        format3D.setPosition(pos);
        format3D.setRotation(rot);
        format3D.setScaling(scale);
        inventoryItemRenderData.getShaderManager().getUtils().performModel3DMatrix(Transformation.getModelMatrix(format3D));
    }

    @Override
    public void preRender(SceneRenderBase sceneRenderBase, javagems3d.system.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        GL30.glDepthFunc(GL30.GL_ALWAYS);
        inventoryItemRenderData.getShaderManager().bind();
        inventoryItemRenderData.getShaderManager().performUniform(new UniformString("projection_matrix"), Transformation.getPerspectiveMatrix(JGems3D.get().getScreen().getWindow(), JGemsSceneGlobalConstants.FOV, 0.1f, 10.0f));
    }

    @Override
    public void postRender(SceneRenderBase sceneRenderBase, javagems3d.system.inventory.items.InventoryItem inventoryItem, InventoryItemRenderData inventoryItemRenderData) {
        GL30.glDepthFunc(GL30.GL_LESS);
        inventoryItemRenderData.getShaderManager().unBind();
    }

    protected void renderInventoryModel(MeshDataGroup meshDataGroup, JGemsShaderManager shaderManager) {
        for (ModelNode modelNode : meshDataGroup.getModelNodeList()) {
            ITextureSample sample1 = (ITextureSample) modelNode.getMaterial().getDiffuse();
            if (sample1 != null) {
                shaderManager.getUtils().performUniformSample(new UniformString("diffuse_map"), sample1);
            } else {
                shaderManager.getUtils().performUniformSample(new UniformString("diffuse_map"), TextureAssetsLoader.DEFAULT);
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
