package ru.jgems3d.toolbox.resources.shaders.manager;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsScene;
import ru.jgems3d.engine.graphics.transformation.Transformation;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.material.samples.ColorSample;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.ITextureSample;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.ISample;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.ShaderContainer;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.ShaderManager;
import ru.jgems3d.toolbox.ToolBox;
import ru.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;

public final class TBoxShaderManager extends ShaderManager {
    private final TBoxShaderUtils shaderUtils;

    public TBoxShaderManager(ShaderContainer shaderContainer) {
        super(shaderContainer);
        this.shaderUtils = new TBoxShaderUtils();
    }

    @Override
    public TBoxShaderManager setShaderRenderPass(RenderPass renderPass) {
        return (TBoxShaderManager) super.setShaderRenderPass(renderPass);
    }

    @Override
    public TBoxShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        return (TBoxShaderManager) super.attachUBOs(uniformBufferObjects);
    }

    public TBoxShaderManager copy() {
        return new TBoxShaderManager(this.getShaderContainer());
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
                JGemsScene.activeGlTexture(i);
                GL30.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            }

            this.performCameraData();

            if (diffuse != null) {
                if (diffuse instanceof ITextureSample) {
                    final int code = 0;
                    ITextureSample imageSample = ((ITextureSample) diffuse);
                    JGemsScene.activeGlTexture(code);
                    imageSample.bindTexture();
                    TBoxShaderManager.this.performUniformNoWarn(new UniformString("diffuse_map"), code);
                    texturing_code |= 1 << 2;
                } else {
                    if (diffuse instanceof ColorSample) {
                        TBoxShaderManager.this.performUniformNoWarn(new UniformString("diffuse_color"), ((ColorSample) diffuse).getColor());
                    }
                }
            }

            TBoxShaderManager.this.performUniformNoWarn(new UniformString("texturing_code"), texturing_code);
        }

        public void performCameraData() {
            TBoxShaderManager.this.performUniformNoWarn(new UniformString("camera_pos"), ToolBox.get().getScreen().getScene().getCamera());
        }

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Model<Format3D> model) {
            if (TBoxShaderManager.this.isUniformExist(new UniformString("model_matrix"))) {
                this.performModel3DMatrix(model);
            }
            if (TBoxShaderManager.this.isUniformExist(new UniformString("view_matrix"))) {
                this.performViewMatrix(viewMatrix);
            }
            if (TBoxShaderManager.this.isUniformExist(new UniformString("model_view_matrix"))) {
                this.performModel3DViewMatrix(model, viewMatrix);
            }
        }

        public void performViewAndModelMatricesSeparately(Model<Format3D> model) {
            this.performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), model);
        }

        public void performOrthographicMatrix(float aspectRatio, float borders) {
            this.performPerspectiveMatrix(Transformation.getOrthographic3DMatrix(-borders * aspectRatio, borders * aspectRatio, -borders, borders, 0, 100, true));
        }

        public void performPerspectiveMatrix() {
            this.performPerspectiveMatrix(TBoxSceneUtils.getMainPerspectiveMatrix());
        }

        public void performPerspectiveMatrix(Matrix4f Matrix4f) {
            TBoxShaderManager.this.performUniform(new UniformString("projection_matrix"), Matrix4f);
        }

        public void performOrthographicMatrix(Model<Format2D> model) {
            TBoxShaderManager.this.performUniform(new UniformString("projection_model_matrix"), Transformation.getModelOrthographicMatrix(model.getFormat(), TBoxSceneUtils.getMainOrthographicMatrix()));
        }

        public void performModel3DViewMatrix(Model<Format3D> model, Matrix4f view) {
            TBoxShaderManager.this.performUniform(new UniformString("model_view_matrix"), Transformation.getModelViewMatrix(model.getFormat(), view));
        }

        public void performModel3DViewMatrix(Matrix4f Matrix4f) {
            TBoxShaderManager.this.performUniform(new UniformString("model_view_matrix"), Matrix4f);
        }

        public void performViewMatrix(Matrix4f Matrix4f) {
            TBoxShaderManager.this.performUniform(new UniformString("view_matrix"), Matrix4f);
        }

        public void performModel3DMatrix(Format3D format3D) {
            this.performModel3DMatrix(Transformation.getModelMatrix(format3D));
        }

        public void performModel3DMatrix(Model<Format3D> model) {
            this.performModel3DMatrix(Transformation.getModelMatrix(model.getFormat()));
        }

        public void performModel3DMatrix(Matrix4f Matrix4f) {
            TBoxShaderManager.this.performUniform(new UniformString("model_matrix"), Matrix4f);
        }
    }
}
