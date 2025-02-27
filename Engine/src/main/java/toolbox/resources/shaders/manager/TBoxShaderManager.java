package toolbox.resources.shaders.manager;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose2D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;

import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import toolbox.ToolBox;
import toolbox.render.scene.dear_imgui.content.EditorContent;
import toolbox.render.scene.utils.TBoxSceneUtils;

public final class TBoxShaderManager extends ShaderManager {
    private final TBoxShaderUtils shaderUtils;

    public TBoxShaderManager(ShadersContainer shadersContainer) {
        super(shadersContainer);
        this.shaderUtils = new TBoxShaderUtils();
    }

    @Override
    public TBoxShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        return (TBoxShaderManager) super.attachUBOs(uniformBufferObjects);
    }

    public TBoxShaderManager copy() {
        return new TBoxShaderManager(this.getShadersContainer());
    }

    public TBoxShaderUtils getUtils() {
        return this.shaderUtils;
    }

    public class TBoxShaderUtils {
        public TBoxShaderUtils() {
        }

        public void performModelMaterialOnShader(Material material) {
            if (material == null) {
                return;
            }
            ISample diffuse = material.getDiffuse();

            int texturing_code = 0;
            for (int i = 0; i < 1; i++) {
                GL46.glActiveTexture(GL46.GL_TEXTURE0 + i);
                GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
            }
            TBoxShaderManager.this.performUniformNoWarn(new UniformString("alpha_discard"), UniformFunctions.FLOAT(EditorContent.alphaDiscard));

            this.performCameraData();

            if (diffuse != null) {
                if (diffuse instanceof ImageBasedTexture) {
                    final int code = 0;
                    ImageBasedTexture imageSample = ((ImageBasedTexture) diffuse);
                    GL46.glActiveTexture(GL46.GL_TEXTURE0 + code);
                    imageSample.bindTexture();
                    TBoxShaderManager.this.performUniformNoWarn(new UniformString("diffuse_map"), UniformFunctions.INTEGER(code));
                    texturing_code |= 1 << 2;
                } else {
                    if (diffuse instanceof RGBAColor) {
                        TBoxShaderManager.this.performUniformNoWarn(new UniformString("diffuse_color"), UniformFunctions.VEC4F(((RGBAColor) diffuse).getColor()));
                    }
                }
            }

            TBoxShaderManager.this.performUniformNoWarn(new UniformString("texturing_code"), UniformFunctions.INTEGER(texturing_code));
        }

        public void performCameraData() {
            TBoxShaderManager.this.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(ToolBox.get().getScreen().getScene().getCamera().getCamPosition()));
        }

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Model3D model) {
            if (TBoxShaderManager.this.isUniformExist(new UniformString("model_matrix"))) {
          //      this.performModel3DMatrix(new UniformString("model_matrix"), model);
            }
            if (TBoxShaderManager.this.isUniformExist(new UniformString("view_matrix"))) {
           //     thisperformViewMatrix(new UniformString("view_matrix"), viewMatrix);
            }
            if (TBoxShaderManager.this.isUniformExist(new UniformString("model_view_matrix"))) {
                this.performModel3DViewMatrix(model, viewMatrix);
            }
        }

        public void performViewAndModelMatricesSeparately(Model3D model) {
            this.performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), model);
        }

        public void performOrthographicMatrix(float aspectRatio, float borders) {
            this.performPerspectiveMatrix(TransformUtils.getOrthographic3DMatrix(-borders * aspectRatio, borders * aspectRatio, -borders, borders, 0, 100, true));
        }

        public void performPerspectiveMatrix() {
            this.performPerspectiveMatrix(TBoxSceneUtils.getMainPerspectiveMatrix());
        }

        public void performPerspectiveMatrix(Matrix4f matrix4f) {
            TBoxShaderManager.this.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performOrthographicMatrix(Model2D model) {
            TBoxShaderManager.this.performUniform(new UniformString("projection_model_matrix"), UniformFunctions.MAT4F(TransformUtils.getModelOrthographicMatrix(model.getPose(), TBoxSceneUtils.getMainOrthographicMatrix())));
        }

        public void performModel3DViewMatrix(Model3D model, Matrix4f view) {
            TBoxShaderManager.this.performUniform(new UniformString("model_view_matrix"), UniformFunctions.MAT4F(TransformUtils.getModelViewMatrix(model.getPose(), view)));
        }

        public void performModel3DViewMatrix(Matrix4f matrix4f) {
            TBoxShaderManager.this.performUniform(new UniformString("model_view_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performViewMatrix(Matrix4f matrix4f) {
            TBoxShaderManager.this.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performModel3DMatrix(Pose3D pose) {
          //  this.performModel3DMatrix(new UniformString("model_matrix"), TransformUtils.getModelMatrix(pose));
        }

        public void performModel3DMatrix(Model3D model) {
         //   this.performModel3DMatrix(new UniformString("model_matrix"), (TransformUtils.getModelMatrix(model.getPose()));
        }

        public void performModel3DMatrix(Matrix4f matrix4f) {
            TBoxShaderManager.this.performUniform(new UniformString("model_matrix"), UniformFunctions.MAT4F(matrix4f));
        }
    }
}
