package ru.alfabouh.jgems3d.toolbox.resources.shaders.manager;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.JGemsScene;
import ru.alfabouh.jgems3d.engine.render.transformation.Transformation;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.ColorSample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.IImageSample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.ISample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.ShaderGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.ShaderManager;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;

public final class TBoxShaderManager extends ShaderManager {
    private final TBoxShaderUtils shaderUtils;

    public TBoxShaderManager(ShaderGroup shaderGroup) {
        super(shaderGroup);
        this.shaderUtils = new TBoxShaderUtils();
    }

    @Override
    public TBoxShaderManager setUseForGBuffer(boolean useForGBuffer) {
        return (TBoxShaderManager) super.setUseForGBuffer(useForGBuffer);
    }

    @Override
    public TBoxShaderManager addUBO(UniformBufferObject uniformBufferObject) {
        return (TBoxShaderManager) super.addUBO(uniformBufferObject);
    }

    public TBoxShaderManager copy() {
        return new TBoxShaderManager(this.getShaderGroup());
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
                if (diffuse instanceof IImageSample) {
                    final int code = 0;
                    IImageSample imageSample = ((IImageSample) diffuse);
                    JGemsScene.activeGlTexture(code);
                    imageSample.bindTexture();
                    TBoxShaderManager.this.performUniformNoWarn("diffuse_map", code);
                    texturing_code |= 1 << 2;
                } else {
                    if (diffuse instanceof ColorSample) {
                        TBoxShaderManager.this.performUniformNoWarn("diffuse_color", ((ColorSample) diffuse).getColor());
                    }
                }
            }

            TBoxShaderManager.this.performUniformNoWarn("texturing_code", texturing_code);
        }

        public void performCameraData() {
            TBoxShaderManager.this.performUniformNoWarn("camera_pos", ToolBox.get().getScreen().getScene().getCamera());
        }

        public void performViewAndModelMatricesSeparately(Matrix4d viewMatrix, Model<Format3D> model) {
            if (TBoxShaderManager.this.checkUniformInGroup("model_matrix")) {
                this.performModel3DMatrix(model);
            }
            if (TBoxShaderManager.this.checkUniformInGroup("view_matrix")) {
                this.performViewMatrix(viewMatrix);
            }
            if (TBoxShaderManager.this.checkUniformInGroup("model_view_matrix")) {
                this.performModel3DViewMatrix(model, viewMatrix);
            }
        }

        public void performViewAndModelMatricesSeparately(Model<Format3D> model) {
            this.performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), model);
        }

        public void performPerspectiveMatrix() {
            this.performPerspectiveMatrix(TBoxSceneUtils.getMainPerspectiveMatrix());
        }

        public void performPerspectiveMatrix(Matrix4d matrix4d) {
            TBoxShaderManager.this.performUniform("projection_matrix", matrix4d);
        }

        public void performOrthographicMatrix(Model<Format2D> model) {
            TBoxShaderManager.this.performUniform("projection_model_matrix", Transformation.getModelOrthographicMatrix(model.getFormat(), TBoxSceneUtils.getMainOrthographicMatrix()));
        }

        public void performModel3DViewMatrix(Model<Format3D> model, Matrix4d view) {
            TBoxShaderManager.this.performUniform("model_view_matrix", Transformation.getModelViewMatrix(model.getFormat(), view));
        }

        public void performModel3DViewMatrix(Matrix4d matrix4d) {
            TBoxShaderManager.this.performUniform("model_view_matrix", matrix4d);
        }

        public void performViewMatrix(Matrix4d matrix4d) {
            TBoxShaderManager.this.performUniform("view_matrix", matrix4d);
        }

        public void performModel3DMatrix(Model<Format3D> model) {
            this.performModel3DMatrix(Transformation.getModelMatrix(model.getFormat()));
        }

        public void performModel3DMatrix(Matrix4d matrix4d) {
            TBoxShaderManager.this.performUniform("model_matrix", matrix4d);
        }
    }
}
