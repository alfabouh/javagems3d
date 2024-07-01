package ru.alfabouh.jgems3d.toolbox.render.scene.utils;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public class TBoxSceneUtils {
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 300.0f;

    public static Matrix4f getMainCameraViewMatrix() {
        return ToolBox.get().getScreen().getTransformationUtils().getMainCameraViewMatrix();
    }

    public static Matrix4f getMainPerspectiveMatrix() {
        return ToolBox.get().getScreen().getTransformationUtils().getPerspectiveMatrix();
    }

    public static Matrix4f getMainOrthographicMatrix() {
        return ToolBox.get().getScreen().getTransformationUtils().getOrthographicMatrix();
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

    @SuppressWarnings("all")
    public static void renderModelTextured(TBoxShaderManager shaderManager, Model<Format3D> model, int code) {
        if (model == null) {
            return;
        }
        TBoxSceneUtils.renderModelTextured(shaderManager, model.getMeshDataGroup(), code);
    }

    @SuppressWarnings("all")
    public static void renderModelTextured(TBoxShaderManager shaderManager, MeshDataGroup meshDataGroup, int code) {
        if (meshDataGroup == null) {
            return;
        }
        for (ModelNode modelNode : meshDataGroup.getModelNodeList()) {
            shaderManager.getUtils().performModelMaterialOnShader(modelNode.getMaterial());
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
