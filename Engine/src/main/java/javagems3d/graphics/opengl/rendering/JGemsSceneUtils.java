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

package javagems3d.graphics.opengl.rendering;

import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.rendering.programs.shaders.unifrom.DefaultUniformActions;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.system.resources.old.MeshGroup;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.rendering.items.AbstractSceneObject;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.render_base.groups.transparent.WorldTransparentRender;
import javagems3d.system.resources.old.MaterialOld;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.RenderPass;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public abstract class JGemsSceneUtils {
    public static Matrix4f getMainCameraViewMatrix() {
        return JGemsHelper.getScreen().getTransformationUtils().getMainCameraViewMatrix();
    }

    public static Matrix4f getMainPerspectiveMatrix() {
        return JGemsHelper.getScreen().getTransformationUtils().getPerspectiveMatrix();
    }

    public static Matrix4f getMainOrthographicMatrix() {
        return JGemsHelper.getScreen().getTransformationUtils().getOrthographicMatrix();
    }

    public static void renderModeledSceneObject(AbstractSceneObject sceneObject) {
        JGemsSceneUtils.renderModeledSceneObject(sceneObject, null);
    }

    public static void renderModeledSceneObject(AbstractSceneObject sceneObject, ICamera camera) {
        if (sceneObject != null) {
            MaterialOld overMaterial = sceneObject.getObjectRenderSettings().getOverlappingMaterial();
            JGemsOpenGLRenderer gemsOpenGLRenderer = JGemsHelper.getScreen().getScene().getSceneRenderer();
            Model<Format3D> model = sceneObject.getModel();
            if (model == null || !model.isValid()) {
                return;
            }
            if (JGemsSceneUtils.filterObjectTransparency(sceneObject)) {
                gemsOpenGLRenderer.addSceneModelObjectInTransparencyPass(sceneObject);
                return;
            }
            JGemsShaderManager shaderManager = sceneObject.getObjectRenderSettings().getShaderManager();
            shaderManager.getUtils().performViewAndModelMatricesSeparately(camera == null ? JGemsSceneUtils.getMainCameraViewMatrix() : TransformationUtils.getAbstractCameraViewMatrix(camera), model);
            shaderManager.getUtils().performRenderDataOnShader(sceneObject.getObjectRenderSettings());
            if (shaderManager.isUniformExist(new UniformString("alpha_discard"))) {
                shaderManager.performUniform(new UniformString("alpha_discard"),  DefaultUniformActions.FLOAT(sceneObject.getObjectRenderSettings().getRenderAttributes().getAlphaDiscardValue()));
                if (sceneObject.getObjectRenderSettings().getRenderAttributes().getAlphaDiscardValue() > 0) {
                    GL46.glDisable(GL46.GL_BLEND);
                }
            }
            boolean f = GL46.glIsEnabled(GL46.GL_CULL_FACE);
            if (sceneObject.getObjectRenderSettings().getRenderAttributes().isDisabledFaceCulling()) {
                GL46.glDisable(GL46.GL_CULL_FACE);
            }
            shaderManager.getUtils().performAnimationsInfo(sceneObject);
            for (MeshGroup.Node meshNode : model.getMeshStructure().getModelNodeList()) {
                MaterialOld material = overMaterial != null ? overMaterial : meshNode.getMaterial();
                if (sceneObject.getObjectRenderSettings().isAllowMoveMeshesIntoTransparencyPass()) {
                    if (material.hasTransparency()) {
                        gemsOpenGLRenderer.addModelNodeInTransparencyPass(new WorldTransparentRender.RenderNodeInfo(sceneObject.getObjectRenderSettings().getOverridenTransparencyShader(), sceneObject.getObjectRenderSettings().getRenderAttributes().isDisabledFaceCulling(), meshNode, model.getFormat()));
                        continue;
                    }
                }
                shaderManager.getUtils().performModelMaterialOnShader(material);
                JGemsSceneUtils.renderModelNode(meshNode);
                shaderManager.clearUsedTextureSlots();
            }
            if (f) {
                GL46.glEnable(GL46.GL_CULL_FACE);
            }
        }
    }

    private static boolean filterObjectTransparency(AbstractSceneObject sceneObject) {
        if (sceneObject.getObjectRenderSettings().isAllowMoveMeshesIntoTransparencyPass()) {
            if (sceneObject.getObjectRenderSettings().getShaderManager().checkShaderRenderPass(RenderPass.TRANSPARENCY) || sceneObject.getObjectRenderSettings().getRenderAttributes().getObjectOpacity() < 1.0f) {
                return true;
            }
            MaterialOld overMaterial = sceneObject.getObjectRenderSettings().getOverlappingMaterial();
            if (overMaterial != null) {
                return overMaterial.hasTransparency();
            }
        }
        return false;
    }

    // section SimpleRender
    @SuppressWarnings("all")
    public static void renderModel(Model<?> model, int code) {
        for (MeshGroup.Node meshNode : model.getMeshStructure().getModelNodeList()) {
            GL46.glBindVertexArray(meshNode.getMesh().getVao());
            meshNode.getMesh().enableAllMeshAttributes();
            GL46.glDrawElements(code, meshNode.getMesh().getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
            meshNode.getMesh().disableAllMeshAttributes();
            GL46.glBindVertexArray(0);
        }
    }

    //section ModelNode
    public static void renderModelNode(MeshGroup.Node meshNode) {
        GL46.glBindVertexArray(meshNode.getMesh().getVao());
        meshNode.getMesh().enableAllMeshAttributes();
        GL46.glDrawElements(GL46.GL_TRIANGLES, meshNode.getMesh().getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
        meshNode.getMesh().disableAllMeshAttributes();
        GL46.glBindVertexArray(0);
    }

    public static int getMaxTextureUnits() {
        return GL46.glGetInteger(GL46.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    public static Model<Format2D> createScreenModel() {
        return MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(JGemsHelper.getScreen().getWindowDimensions()), 0);
    }

    public static void checkGLErrors() {
        int errorCode;
        while ((errorCode = GL46.glGetError()) != GL46.GL_NO_ERROR) {
            String error;
            switch (errorCode) {
                case GL46.GL_INVALID_ENUM:
                    error = "INVALID_ENUM";
                    break;
                case GL46.GL_INVALID_VALUE:
                    error = "INVALID_VALUE";
                    break;
                case GL46.GL_INVALID_OPERATION:
                    error = "INVALID_OPERATION";
                    break;
                case GL46.GL_STACK_OVERFLOW:
                    error = "STACK_OVERFLOW";
                    break;
                case GL46.GL_STACK_UNDERFLOW:
                    error = "STACK_UNDERFLOW";
                    break;
                case GL46.GL_OUT_OF_MEMORY:
                    error = "OUT_OF_MEMORY";
                    break;
                case GL46.GL_INVALID_FRAMEBUFFER_OPERATION:
                    error = "INVALID_FRAMEBUFFER_OPERATION";
                    break;
                default:
                    error = "UNKNOWN";
                    break;
            }
            JGemsHelper.getLogger().error("GL ERROR: " + error);
        }
    }
}
