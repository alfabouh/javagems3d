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

import javagems3d.graphics.opengl.rendering.programs.shaders.unifrom.DefaultUniformActions;
import javagems3d.system.resources.old.MeshGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.items.AbstractSceneObject;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.render_base.RenderGroup;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.system.resources.old.MaterialOld;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.RenderPass;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.HashSet;
import java.util.Set;

public class WorldTransparentRender extends SceneRenderBase {
    private final Set<RenderNodeInfo> transparentModelModes;
    private final Set<AbstractSceneObject> transparentModelObjects;

    public WorldTransparentRender(JGemsOpenGLRenderer sceneRender) {
        super(0, sceneRender, new RenderGroup("WORLD_TRANSPARENT"));

        this.transparentModelModes = new HashSet<>();
        this.transparentModelObjects = new HashSet<>();
    }

    public void onRender(FrameTicking frameTicking) {
        this.transparentModelObjects.addAll(this.getSceneWorld().getFilteredEntitySet(RenderPass.TRANSPARENCY));
        for (AbstractSceneObject modeledSceneObject : this.transparentModelObjects) {
            this.renderIModeledSceneObject(modeledSceneObject);
        }
        for (RenderNodeInfo renderNodeInfo : this.transparentModelModes) {
            this.renderModelNode(renderNodeInfo.getOverlappingTransparencyShader(), renderNodeInfo.isDisableFaceCulling(), renderNodeInfo.getModelNode(), renderNodeInfo.getModelTransforms());
        }
        this.transparentModelObjects.clear();
        this.transparentModelModes.clear();
    }

    private void renderIModeledSceneObject(AbstractSceneObject object) {
        MaterialOld overMaterial = object.getObjectRenderSettings().getOverlappingMaterial();
        JGemsShaderManager gemsShaderManager = object.getObjectRenderSettings().getOverridenTransparencyShader();
        if (gemsShaderManager == null) {
            gemsShaderManager = this.getSceneRenderer().getBasicOITShader();
        }
        gemsShaderManager.beginShading();
        gemsShaderManager.getUtils().performPerspectiveMatrix();
        gemsShaderManager.getUtils().performViewAndModelMatricesSeparately(object.getModel());
        for (MeshGroup.Node meshNode : object.getModel().getMeshStructure().getModelNodeList()) {
            gemsShaderManager.getUtils().performShadowsInfo();
            gemsShaderManager.getUtils().performModelMaterialOnShader(overMaterial != null ? overMaterial : meshNode.getMaterial());
            gemsShaderManager.performUniform(new UniformString("alpha_factor"), DefaultUniformActions.FLOAT(meshNode.getMaterial().getFullOpacity() * object.getObjectRenderSettings().getRenderAttributes().getObjectOpacity()));

            boolean f = GL46.glIsEnabled(GL46.GL_CULL_FACE);
            if (object.getObjectRenderSettings().getRenderAttributes().isDisabledFaceCulling()) {
                GL46.glDisable(GL46.GL_CULL_FACE);
            }
            JGemsSceneUtils.renderModelNode(meshNode);
            if (f) {
                GL46.glEnable(GL46.GL_CULL_FACE);
            }
            gemsShaderManager.clearUsedTextureSlots();
        }
        gemsShaderManager.endShading();
    }

    private void renderModelNode(JGemsShaderManager gemsShaderManager, boolean disableCulling, MeshGroup.Node meshNode, Format3D format3D) {
        if (gemsShaderManager == null) {
            gemsShaderManager = this.getSceneRenderer().getBasicOITShader();
        }
        gemsShaderManager.beginShading();
        gemsShaderManager.getUtils().performPerspectiveMatrix();
        gemsShaderManager.getUtils().performViewAndModelMatricesSeparately(JGemsSceneUtils.getMainCameraViewMatrix(), format3D);

        gemsShaderManager.getUtils().performShadowsInfo();
        gemsShaderManager.getUtils().performModelMaterialOnShader(meshNode.getMaterial());
        gemsShaderManager.performUniform(new UniformString("alpha_factor"), DefaultUniformActions.FLOAT(meshNode.getMaterial().getFullOpacity()));

        boolean f = GL46.glIsEnabled(GL46.GL_CULL_FACE);
        if (disableCulling) {
            GL46.glDisable(GL46.GL_CULL_FACE);
        }
        JGemsSceneUtils.renderModelNode(meshNode);
        if (f) {
            GL46.glEnable(GL46.GL_CULL_FACE);
        }
        gemsShaderManager.clearUsedTextureSlots();
        gemsShaderManager.endShading();
    }

    public void addModelNodeInTransparencyPass(RenderNodeInfo node) {
        this.transparentModelModes.add(node);
    }

    public void addSceneModelObjectInTransparencyPass(AbstractSceneObject sceneObject) {
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
        private final MeshGroup.Node meshNode;
        private final Format3D modelTransforms;
        private final boolean disableFaceCulling;

        public RenderNodeInfo(@Nullable JGemsShaderManager overlappingTransparencyShader, boolean disableFaceCulling, @NotNull MeshGroup.Node meshNode, @NotNull Format3D modelTransforms) {
            this.overlappingTransparencyShader = overlappingTransparencyShader;
            this.disableFaceCulling = disableFaceCulling;
            this.meshNode = meshNode;
            this.modelTransforms = modelTransforms;
        }

        public JGemsShaderManager getOverlappingTransparencyShader() {
            return this.overlappingTransparencyShader;
        }

        public MeshGroup.Node getModelNode() {
            return this.meshNode;
        }

        public Format3D getModelTransforms() {
            return this.modelTransforms;
        }

        public boolean isDisableFaceCulling() {
            return this.disableFaceCulling;
        }
    }
}