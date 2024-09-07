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

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.rendering.items.IModeledSceneObject;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.render_base.groups.transparent.WorldTransparentRender;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.ModelNode;
import javagems3d.system.resources.assets.shaders.RenderPass;
import javagems3d.system.resources.assets.shaders.UniformString;
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

    // section SimpleRender
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

    public static int getMaxTextureUnits() {
        return GL30.glGetInteger(GL30.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    public static Model<Format2D> createScreenModel() {
        return MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(JGemsHelper.getScreen().getWindowDimensions()), 0);
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
            JGemsHelper.getLogger().error("GL ERROR: " + error);
        }
    }
}
