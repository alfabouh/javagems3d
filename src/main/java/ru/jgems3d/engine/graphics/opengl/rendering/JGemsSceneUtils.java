package ru.jgems3d.engine.graphics.opengl.rendering;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent.WorldTransparentRender;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public abstract class JGemsSceneUtils {
    public synchronized static Matrix4f getMainCameraViewMatrix() {
        return JGemsHelper.getScreen().getTransformationUtils().getMainCameraViewMatrix();
    }

    public synchronized static Matrix4f getMainPerspectiveMatrix() {
        return JGemsHelper.getScreen().getTransformationUtils().getPerspectiveMatrix();
    }

    public synchronized static Matrix4f getMainOrthographicMatrix() {
        return JGemsHelper.getScreen().getTransformationUtils().getOrthographicMatrix();
    }

    // section SimpleRender
    @SuppressWarnings("all")
    public static void renderModel(Model<?> model, int code) {
        for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
            JGemsSceneUtils.renderModelNode(modelNode);
        }
    }

    // section TransparencyFilter
    public static boolean filterObjectTransparency(IModeledSceneObject sceneObject) {
        if (sceneObject.getMeshRenderData().isAllowMoveMeshesIntoTransparencyPass()) {
            if (sceneObject.getMeshRenderData().getShaderManager().checkShaderRenderPass(RenderPass.TRANSPARENCY) || sceneObject.getMeshRenderData().getRenderAttributes().getObjectOpacity() < 1.0f) {
                return true;
            }
            Material overMaterial = sceneObject.getMeshRenderData().getOverlappingMaterial();
            if (overMaterial != null) {
                return overMaterial.hasTransparency();
            }
        }
        return false;
    }

    //section ModelNode
    public static void renderModelNode(ModelNode modelNode) {
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

    // section RenderWithMaterial
    public static void renderSceneObject(IModeledSceneObject sceneObject) {
        if (sceneObject != null) {
            Material overMaterial = sceneObject.getMeshRenderData().getOverlappingMaterial();
            JGemsOpenGLRenderer gemsOpenGLRenderer = JGemsHelper.getScreen().getScene().getSceneRenderer();
            Model<Format3D> model = sceneObject.getModel();
            if (model == null || !model.isValid()) {
                return;
            }
            if (JGemsSceneUtils.filterObjectTransparency(sceneObject)) {
                gemsOpenGLRenderer.addSceneModelObjectInTransparencyPass(sceneObject);
                return;
            }
            JGemsShaderManager shaderManager = sceneObject.getMeshRenderData().getShaderManager();
            shaderManager.getUtils().performViewAndModelMatricesSeparately(JGemsSceneUtils.getMainCameraViewMatrix(), model);
            shaderManager.getUtils().performRenderDataOnShader(sceneObject.getMeshRenderData());
            if (shaderManager.isUniformExist("alpha_discard")) {
                shaderManager.performUniform("alpha_discard", sceneObject.getMeshRenderData().getRenderAttributes().getAlphaDiscardValue());
                if (sceneObject.getMeshRenderData().getRenderAttributes().getAlphaDiscardValue() > 0) {
                    GL30.glDisable(GL30.GL_BLEND);
                }
            }
            boolean f = GL30.glIsEnabled(GL11.GL_CULL_FACE);
            if (sceneObject.getMeshRenderData().getRenderAttributes().isDisabledFaceCulling()) {
                GL30.glDisable(GL11.GL_CULL_FACE);
            }
            for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
                if (sceneObject.getMeshRenderData().isAllowMoveMeshesIntoTransparencyPass()) {
                    if (modelNode.getMaterial().hasTransparency()) {
                        gemsOpenGLRenderer.addModelNodeInTransparencyPass(new WorldTransparentRender.RenderNodeInfo(sceneObject.getMeshRenderData().getOverridenTransparencyShader(), sceneObject.getMeshRenderData().getRenderAttributes().isDisabledFaceCulling(), modelNode, model.getFormat()));
                        continue;
                    }
                }
                shaderManager.getUtils().performModelMaterialOnShader(overMaterial != null ? overMaterial : modelNode.getMaterial());
                JGemsSceneUtils.renderModelNode(modelNode);
            }
            if (f) {
                GL30.glEnable(GL11.GL_CULL_FACE);
            }
        }
    }

    public static void checkGLErrors() {
        int errorCode;
        while ((errorCode = GL11.glGetError()) != GL11.GL_NO_ERROR) {
            String error;
            switch (errorCode) {
                case GL11.GL_INVALID_ENUM:
                    error = "INVALID_ENUM";
                    break;
                case GL11.GL_INVALID_VALUE:
                    error = "INVALID_VALUE";
                    break;
                case GL11.GL_INVALID_OPERATION:
                    error = "INVALID_OPERATION";
                    break;
                case GL11.GL_STACK_OVERFLOW:
                    error = "STACK_OVERFLOW";
                    break;
                case GL11.GL_STACK_UNDERFLOW:
                    error = "STACK_UNDERFLOW";
                    break;
                case GL11.GL_OUT_OF_MEMORY:
                    error = "OUT_OF_MEMORY";
                    break;
                case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
                    error = "INVALID_FRAMEBUFFER_OPERATION";
                    break;
                default:
                    error = "UNKNOWN";
                    break;
            }
            JGemsHelper.getLogger().warn("GL ERROR: " + error);
        }
    }
}
