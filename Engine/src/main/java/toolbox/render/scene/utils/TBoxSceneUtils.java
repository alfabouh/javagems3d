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

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.manager.helper.JGemsShadersHelper;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import toolbox.ToolBox;
import toolbox.resources.shaders.manager.TBoxShaderManager;

public class TBoxSceneUtils {
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 300.0f;

    public static Matrix4f getMainCameraViewMatrix() {
        return ToolBox.get().getScreen().getTransformationUtils().getCameraViewMatrix();
    }

    public static Matrix4f getMainPerspectiveMatrix() {
        return ToolBox.get().getScreen().getTransformationUtils().getPerspectiveMatrix();
    }

    public static Matrix4f getMainOrthographicMatrix() {
        return ToolBox.get().getScreen().getTransformationUtils().getOrthographicMatrix();
    }

    public static void renderModel(Model3D model, int code) {
        TBoxSceneUtils.renderModel(model.getMeshStructureCast(), code);
    }

    @SuppressWarnings("all")
    public static void renderModel(MeshGroup meshGroup, int code) {
        for (MeshNode3D<RenderMesh> meshNode3D : meshGroup.getAllNodes()) {
            GL46.glBindVertexArray(meshNode3D.getMeshData().getVao());
            meshNode3D.getMeshData().enableAllMeshAttributes();
            GL46.glDrawElements(code, meshNode3D.getMeshData().getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
            meshNode3D.getMeshData().disableAllMeshAttributes();
            GL46.glBindVertexArray(0);
        }
    }

    @SuppressWarnings("all")
    public static void renderModelTextured(TBoxShaderManager shaderManager, Model3D model, int code) {
        if (model == null) {
            return;
        }
        TBoxSceneUtils.renderModelTextured(shaderManager, model.getMeshStructureCast(), code);
    }

    @SuppressWarnings("all")
    public static void renderModelTextured(TBoxShaderManager shaderManager, MeshGroup meshGroup, int code) {
        if (meshGroup == null) {
            return;
        }
        for (MeshNode3D<RenderMesh> meshNode3D : meshGroup.getAllNodes()) {
            JGemsShadersHelper.performModelMaterialOnShader(null, meshNode3D.getMaterial());
            GL46.glBindVertexArray(meshNode3D.getMeshData().getVao());
            meshNode3D.getMeshData().enableAllMeshAttributes();
            GL46.glDrawElements(GL46.GL_TRIANGLES, meshNode3D.getMeshData().getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
            meshNode3D.getMeshData().disableAllMeshAttributes();
            GL46.glBindVertexArray(0);
        }
    }
}
