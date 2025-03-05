package javagems3d.system.resources.assets.shaders.manager;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITextureProgram;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import org.joml.Matrix4f;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.texturing.Color4Texture;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.base.ISample;

import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;

public class JGemsShaderManager extends ShaderManager {
    public JGemsShaderManager(ShadersContainer shadersContainer) {
        super(shadersContainer);
    }

    @Override
    public JGemsShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        return (JGemsShaderManager) super.attachUBOs(uniformBufferObjects);
    }

    public JGemsShaderManager copy() {
        return new JGemsShaderManager(this.getShadersContainer());
    }

    public void performUniformSample(UniformString uniform, ISample sample) {
        if (sample instanceof Color4Texture) {
            Color4Texture color = (Color4Texture) sample;
            this.performUniform(uniform, UniformFunctions.VEC4F(color.getColor()));
        } else {
            if (sample instanceof ITextureProgram) {
                ITextureProgram textureProgram = (ITextureProgram) sample;
                this.performUniformTexture(uniform, textureProgram.getSamplerId(), textureProgram.getTextureId(), textureProgram.getTextureAttachment());
            }
        }
    }

    public void performPerspectiveMatrix(UniformString uniform, Matrix4f perspective) {
        this.performUniform(uniform, UniformFunctions.MAT4F(perspective));
    }

    public void performOrthographicMatrix(UniformString uniform, Model2D model, Matrix4f orthographicMatrix) {
        this.performUniform(uniform, UniformFunctions.MAT4F(TransformUtils.getModelOrthographicMatrix(model.getPose(), orthographicMatrix)));
    }

    public void performModel3DViewMatrix(UniformString uniform, Model3D model, Matrix4f view) {//new UniformString("model_view_matrix")
        this.performUniform(uniform, UniformFunctions.MAT4F(TransformUtils.getModelViewMatrix(model.getPose(), view)));
    }

    public void performModel3DViewMatrix(UniformString uniform, Matrix4f model, Matrix4f view) {//new UniformString("model_view_matrix")
        this.performUniform(uniform, UniformFunctions.MAT4F(new Matrix4f(view).mul(model)));
    }

    public void performModel3DViewMatrix(UniformString uniform, Matrix4f matrix4f) {//new UniformString("model_view_matrix")
        this.performUniform(uniform, UniformFunctions.MAT4F(matrix4f));
    }

    public void performViewMatrix(UniformString uniform, Matrix4f matrix4f) {//new UniformString("view_matrix")
        this.performUniform(uniform, UniformFunctions.MAT4F(matrix4f));
    }

    public void performModel3DMatrix(UniformString uniform, Model3D model) {//new UniformString("model_matrix")
        this.performUniform(uniform, UniformFunctions.MAT4F(TransformUtils.getModelMatrix(model.getPose())));
    }

    public void performModel3DMatrix(UniformString uniform, Matrix4f matrix4f) {//new UniformString("model_matrix")
        this.performUniform(uniform, UniformFunctions.MAT4F(matrix4f));
    }
}
