package ru.alfabouh.jgems3d.engine.render.opengl.scene.utils;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.ICamera;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class JGemsSceneUtils {
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 100.0f;

    public static Matrix4f getMainCameraViewMatrix() {
        return JGems.get().getScreen().getTransformationUtils().getMainCameraViewMatrix();
    }

    public static Matrix4f getMainPerspectiveMatrix() {
        return JGems.get().getScreen().getTransformationUtils().getPerspectiveMatrix();
    }

    public static Matrix4f getMainOrthographicMatrix() {
        return JGems.get().getScreen().getTransformationUtils().getOrthographicMatrix();
    }

    public static boolean isSceneActive() {
        return JGems.get().getScreen().getWindow().isActive();
    }

    public static void setCamera(ICamera camera) {
        JGems.get().getScreen().getScene().setRenderCamera(camera);
    }

    @SuppressWarnings("all")
    public static void renderModel(Model<?> model, int code) {
        for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
            GL30.glBindVertexArray(modelNode.getMesh().getVao());
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glEnableVertexAttribArray(a);
            }
            GL30.glDrawElements(code, modelNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glDisableVertexAttribArray(a);
            }
            GL30.glBindVertexArray(0);
        }
    }

    public static void renderSceneObject(IModeledSceneObject sceneObject) {
        JGemsSceneUtils.renderSceneObject(sceneObject, null);
    }

    public static void renderSceneObject(IModeledSceneObject sceneObject, Material overMaterial) {
        if (sceneObject != null) {
            Model<Format3D> model = sceneObject.getModel3D();
            if (model == null || model.getMeshDataGroup() == null) {
                return;
            }
            JGemsShaderManager shaderManager = sceneObject.getModelRenderParams().getShaderManager();
            shaderManager.getUtils().performViewAndModelMatricesSeparately(JGemsSceneUtils.getMainCameraViewMatrix(), model);
            shaderManager.getUtils().performConstraintsOnShader(sceneObject.getModelRenderParams());
            if (shaderManager.checkUniformInGroup("texture_scaling")) {
                shaderManager.performUniform("texture_scaling", sceneObject.getModelRenderParams().getTextureScaling());
            }
            if (shaderManager.checkUniformInGroup("alpha_discard")) {
                shaderManager.performUniform("alpha_discard", sceneObject.getModelRenderParams().getAlphaDiscardValue());
                if (sceneObject.getModelRenderParams().getAlphaDiscardValue() > 0) {
                    GL30.glDisable(GL30.GL_BLEND);
                }
            }
            for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
                shaderManager.getUtils().performModelMaterialOnShader(overMaterial != null ? overMaterial : modelNode.getMaterial(), sceneObject.getModelRenderParams().isPassShadowsInfoInRender());
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
            GL30.glEnable(GL30.GL_BLEND);
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
            SystemLogging.get().getLogManager().warn("GL ERROR: " + error);
        }
    }
}
