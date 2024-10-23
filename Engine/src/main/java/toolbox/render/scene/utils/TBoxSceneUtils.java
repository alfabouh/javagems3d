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

package toolbox.render.scene.utils;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import toolbox.ToolBox;
import toolbox.resources.shaders.manager.TBoxShaderManager;

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

    public static void renderModel(Model<Format3D> model, int code) {
        TBoxSceneUtils.renderModel(model.getMeshDataGroup(), code);
    }

    @SuppressWarnings("all")
    public static void renderModel(MeshGroup meshGroup, int code) {
        for (MeshGroup.Node meshNode : meshGroup.getModelNodeList()) {
            GL30.glBindVertexArray(meshNode.getMesh().getVao());
            meshNode.getMesh().enableAllMeshAttributes();
            GL30.glDrawElements(code, meshNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            meshNode.getMesh().disableAllMeshAttributes();
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
    public static void renderModelTextured(TBoxShaderManager shaderManager, MeshGroup meshGroup, int code) {
        if (meshGroup == null) {
            return;
        }
        for (MeshGroup.Node meshNode : meshGroup.getModelNodeList()) {
            shaderManager.getUtils().performModelMaterialOnShader(meshNode.getMaterial());
            GL30.glBindVertexArray(meshNode.getMesh().getVao());
            meshNode.getMesh().enableAllMeshAttributes();
            GL30.glDrawElements(GL30.GL_TRIANGLES, meshNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            meshNode.getMesh().disableAllMeshAttributes();
            GL30.glBindVertexArray(0);
        }
    }
}
