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

package javagems3d.graphics.rendering;

import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;

public abstract class JGemsSceneUtils {
    public static Matrix4f getMainCameraViewMatrix() {
        return JGemsHelper.getScreen().getTransformation().getMainCameraViewMatrix();
    }

    public static Matrix4f getMainPerspectiveMatrix() {
        return JGemsHelper.getScreen().getTransformation().getPerspectiveMatrix();
    }

    public static Matrix4f getMainOrthographicMatrix() {
        return JGemsHelper.getScreen().getTransformation().getOrthographicMatrix();
    }

    // section SimpleRender
    @SuppressWarnings("all")
    public static void renderModel(Model<?> model, int code) {
        for (MeshGroup.MeshGroupNode meshNode : model.<MeshGroup>getMeshStructureWithUnSafeCast().getMeshNodes()) {
            GL46.glBindVertexArray(meshNode.getMesh().getVao());
            meshNode.getMesh().enableAllMeshAttributes();
            GL46.glDrawElements(code, meshNode.getMesh().getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
            meshNode.getMesh().disableAllMeshAttributes();
            GL46.glBindVertexArray(0);
        }
    }

    //section ModelNode
    public static void renderModelNode(MeshGroup.MeshGroupNode meshNode) {
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
