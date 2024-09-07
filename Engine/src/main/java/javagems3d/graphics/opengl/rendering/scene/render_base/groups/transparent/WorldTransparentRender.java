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

package javagems3d.graphics.opengl.rendering.scene.render_base.groups.transparent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.items.IModeledSceneObject;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.render_base.RenderGroup;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.ModelNode;
import javagems3d.system.resources.assets.shaders.RenderPass;
import javagems3d.system.resources.assets.shaders.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.HashSet;
import java.util.Set;

public class WorldTransparentRender extends SceneRenderBase {
    private final Set<RenderNodeInfo> transparentModelModes;
    private final Set<IModeledSceneObject> transparentModelObjects;

    public WorldTransparentRender(JGemsOpenGLRenderer sceneRender) {
        super(0, sceneRender, new RenderGroup("WORLD_TRANSPARENT"));

        this.transparentModelModes = new HashSet<>();
        this.transparentModelObjects = new HashSet<>();
    }

    public void onRender(FrameTicking frameTicking) {
        this.transparentModelObjects.addAll(this.getSceneWorld().getFilteredEntitySet(RenderPass.TRANSPARENCY));
        for (IModeledSceneObject modeledSceneObject : this.transparentModelObjects) {
            this.renderIModeledSceneObject(modeledSceneObject);
        }
        for (RenderNodeInfo renderNodeInfo : this.transparentModelModes) {
            this.renderModelNode(renderNodeInfo.getOverlappingTransparencyShader(), renderNodeInfo.isDisableFaceCulling(), renderNodeInfo.getModelNode(), renderNodeInfo.getModelTransforms());
        }
        this.transparentModelObjects.clear();
        this.transparentModelModes.clear();
    }

    private void renderIModeledSceneObject(IModeledSceneObject object) {
        Material overMaterial = object.getMeshRenderData().getOverlappingMaterial();
        JGemsShaderManager gemsShaderManager = object.getMeshRenderData().getOverridenTransparencyShader();
        if (gemsShaderManager == null) {
            gemsShaderManager = this.getSceneRenderer().getBasicOITShader();
        }
        gemsShaderManager.bind();
        gemsShaderManager.getUtils().performPerspectiveMatrix();
        gemsShaderManager.getUtils().performViewAndModelMatricesSeparately(object.getModel());
        for (ModelNode modelNode : object.getModel().getMeshDataGroup().getModelNodeList()) {
            gemsShaderManager.getUtils().performShadowsInfo();
            gemsShaderManager.getUtils().performModelMaterialOnShader(overMaterial != null ? overMaterial : modelNode.getMaterial());
            gemsShaderManager.performUniform(new UniformString("alpha_factor"), modelNode.getMaterial().getFullOpacity() * object.getMeshRenderData().getRenderAttributes().getObjectOpacity());

            boolean f = GL30.glIsEnabled(GL11.GL_CULL_FACE);
            if (object.getMeshRenderData().getRenderAttributes().isDisabledFaceCulling()) {
                GL30.glDisable(GL11.GL_CULL_FACE);
            }
            JGemsSceneUtils.renderModelNode(modelNode);
            if (f) {
                GL30.glEnable(GL11.GL_CULL_FACE);
            }
            gemsShaderManager.clearUsedTextureSlots();
        }
        gemsShaderManager.unBind();
    }

    private void renderModelNode(JGemsShaderManager gemsShaderManager, boolean disableCulling, ModelNode modelNode, Format3D format3D) {
        if (gemsShaderManager == null) {
            gemsShaderManager = this.getSceneRenderer().getBasicOITShader();
        }
        gemsShaderManager.bind();
        gemsShaderManager.getUtils().performPerspectiveMatrix();
        gemsShaderManager.getUtils().performViewAndModelMatricesSeparately(JGemsSceneUtils.getMainCameraViewMatrix(), format3D);

        gemsShaderManager.getUtils().performShadowsInfo();
        gemsShaderManager.getUtils().performModelMaterialOnShader(modelNode.getMaterial());
        gemsShaderManager.performUniform(new UniformString("alpha_factor"), modelNode.getMaterial().getFullOpacity());

        boolean f = GL30.glIsEnabled(GL11.GL_CULL_FACE);
        if (disableCulling) {
            GL30.glDisable(GL11.GL_CULL_FACE);
        }
        JGemsSceneUtils.renderModelNode(modelNode);
        if (f) {
            GL30.glEnable(GL11.GL_CULL_FACE);
        }
        gemsShaderManager.clearUsedTextureSlots();
        gemsShaderManager.unBind();
    }

    public void addModelNodeInTransparencyPass(RenderNodeInfo node) {
        this.transparentModelModes.add(node);
    }

    public void addSceneModelObjectInTransparencyPass(IModeledSceneObject sceneObject) {
        this.transparentModelObjects.add(sceneObject);
    }

    private void clearSets() {
        this.transparentModelModes.clear();
        this.transparentModelObjects.clear();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
        this.clearSets();
    }

    public static class RenderNodeInfo {
        private final JGemsShaderManager overlappingTransparencyShader;
        private final ModelNode modelNode;
        private final Format3D modelTransforms;
        private final boolean disableFaceCulling;

        public RenderNodeInfo(@Nullable JGemsShaderManager overlappingTransparencyShader, boolean disableFaceCulling, @NotNull ModelNode modelNode, @NotNull Format3D modelTransforms) {
            this.overlappingTransparencyShader = overlappingTransparencyShader;
            this.disableFaceCulling = disableFaceCulling;
            this.modelNode = modelNode;
            this.modelTransforms = modelTransforms;
        }

        public JGemsShaderManager getOverlappingTransparencyShader() {
            return this.overlappingTransparencyShader;
        }

        public ModelNode getModelNode() {
            return this.modelNode;
        }

        public Format3D getModelTransforms() {
            return this.modelTransforms;
        }

        public boolean isDisableFaceCulling() {
            return this.disableFaceCulling;
        }
    }
}