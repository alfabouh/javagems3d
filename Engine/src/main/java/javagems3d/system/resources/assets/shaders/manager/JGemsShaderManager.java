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

package javagems3d.system.resources.assets.shaders.manager;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.environment.shadow.CascadeShadow;
import javagems3d.graphics.opengl.environment.shadow.PointLightShadow;
import javagems3d.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.scene.JGemsScene;
import javagems3d.graphics.transformation.Transformation;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.material.samples.ColorSample;
import javagems3d.system.resources.assets.material.samples.CubeMapSample;
import javagems3d.system.resources.assets.material.samples.TextureSample;
import javagems3d.system.resources.assets.material.samples.base.ISample;
import javagems3d.system.resources.assets.material.samples.base.ITextureSample;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.data.render.MeshRenderData;
import javagems3d.system.resources.assets.shaders.base.RenderPass;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.base.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.base.UniformString;

public final class JGemsShaderManager extends ShaderManager {
    private final JGemsShaderUtils shaderUtils;

    public JGemsShaderManager(ShadersContainer shadersContainer) {
        super(shadersContainer);
        this.shaderUtils = new JGemsShaderUtils();
    }

    @Override
    public JGemsShaderManager setShaderRenderPass(RenderPass renderPass) {
        return (JGemsShaderManager) super.setShaderRenderPass(renderPass);
    }

    @Override
    public JGemsShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        return (JGemsShaderManager) super.attachUBOs(uniformBufferObjects);
    }

    public JGemsShaderManager copy() {
        return new JGemsShaderManager(this.getShaderContainer());
    }

    public JGemsShaderUtils getUtils() {
        return this.shaderUtils;
    }

    public class JGemsShaderUtils {
        public JGemsShaderUtils() {
        }

        public void performUniformSampleNoWarn(UniformString uniform, ISample sample) {
            if (!JGemsShaderManager.this.isUniformExist(uniform)) {
                return;
            }
            this.performUniformSample(uniform, sample);
        }

        public void performUniformSample(UniformString uniform, ISample sample) {
            if (sample instanceof ColorSample) {
                ColorSample colorSample = (ColorSample) sample;
                JGemsShaderManager.this.performUniform(uniform, colorSample.getColor());
            } else {
                if (sample instanceof TextureSample) {
                    TextureSample textureSample = (TextureSample) sample;
                    JGemsShaderManager.this.performUniformTexture(uniform, textureSample.getTextureId(), textureSample.getTextureAttachment());
                }
            }
        }

        public void performRenderDataOnShader(MeshRenderData meshRenderData) {
            if (!JGemsShaderManager.this.isUniformExist(new UniformString("lighting_code"))) {
                return;
            }
            int lighting_code = 0;
            if (meshRenderData.getRenderAttributes().isBright()) {
                lighting_code |= 1 << 2;
            }
            JGemsShaderManager.this.performUniform(new UniformString("lighting_code"), lighting_code);
        }

        public void performModelMaterialOnShader(Material material) {
            if (material == null) {
                return;
            }

            ISample diffuse = material.getDiffuse();
            ITextureSample emission = material.getEmissionMap();
            ITextureSample metallic = material.getMetallicMap();
            ITextureSample normals = material.getNormalsMap();
            ITextureSample specular = material.getSpecularMap();
            CubeMapSample cubeMapProgram = JGemsHelper.ENVIRONMENT.getWorldEnvironment().getSkyBox().getSky2DTexture();

            int texturing_code = 0;

            this.performCameraData();
            if (JGemsShaderManager.this.isUniformExist(new UniformString("ambient_cubemap"))) {
                this.performCubeMapProgram(new UniformString("ambient_cubemap"), cubeMapProgram.getTextureId());
            }

            if (diffuse != null) {
                if (diffuse instanceof ITextureSample) {
                    this.performUniformSampleNoWarn(new UniformString("diffuse_map"), diffuse);
                    texturing_code |= 1 << 2;
                } else {
                    if (diffuse instanceof ColorSample) {
                        this.performUniformSampleNoWarn(new UniformString("diffuse_color"), diffuse);
                    }
                }
            }
            if (emission != null) {
                this.performUniformSampleNoWarn(new UniformString("emissive_map"), emission);
                texturing_code |= 1 << 3;
            }
            if (metallic != null) {
                this.performUniformSampleNoWarn(new UniformString("metallic_map"), metallic);
                texturing_code |= 1 << 4;
            }
            if (normals != null) {
                this.performUniformSampleNoWarn(new UniformString("normals_map"), normals);
                texturing_code |= 1 << 5;
            }
            if (specular != null) {
                this.performUniformSampleNoWarn(new UniformString("specular_map"), specular);
                texturing_code |= 1 << 6;
            }

            JGemsShaderManager.this.performUniformNoWarn(new UniformString("texturing_code"), texturing_code);
        }

        public void performCameraData() {
            JGemsShaderManager.this.performUniformNoWarn(new UniformString("camera_pos"), JGemsHelper.CAMERA.getCurrentCamera().getCamPosition());
        }

        public void performShadowsInfo() {
            JGemsScene scene = JGems3D.get().getScreen().getScene();
            for (int i = 0; i < JGemsSceneGlobalConstants.CASCADE_SPLITS; i++) {
                CascadeShadow cascadeShadow = scene.getSceneRenderer().getShadowScene().getCascadeShadows().get(i);
                if (JGemsShaderManager.this.isUniformExist(new UniformString("sun_shadow_map", i))) {
                    JGemsShaderManager.this.performUniformTexture(new UniformString("sun_shadow_map", i), scene.getSceneRenderer().getShadowScene().getShadowPostFBO().getTextureIDByIndex(i), GL30.GL_TEXTURE_2D);
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("cascade_shadow", ".split_distance", i), cascadeShadow.getSplitDistance());
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("cascade_shadow", ".projection_view", i), cascadeShadow.getLightProjectionViewMatrix());
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("PosExp"), JGemsSceneGlobalConstants.EVSM_POSITIVE_EXPONENT);
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("NegExp"), JGemsSceneGlobalConstants.EVSM_NEGATIVE_EXPONENT);
                }
            }
            for (int i = 0; i < JGemsSceneGlobalConstants.MAX_POINT_LIGHTS_SHADOWS; i++) {
                PointLightShadow pointLightShadow = scene.getSceneRenderer().getShadowScene().getPointLightShadows().get(i);
                JGemsShaderManager.this.performUniformNoWarn(new UniformString("far_plane"), pointLightShadow.farPlane());
                if (JGemsShaderManager.this.isUniformExist(new UniformString("point_light_cubemap", i))) {
                    this.performCubeMapProgram(new UniformString("point_light_cubemap", i), pointLightShadow.getPointLightCubeMap().getCubeMapProgram().getTextureId());
                }
            }
        }

        public void performCubeMapProgram(UniformString uniform, int cubeMapTextureId) {
            JGemsShaderManager.this.performUniformTexture(uniform, cubeMapTextureId, GL30.GL_TEXTURE_CUBE_MAP);
        }

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Format3D format3D) {
            Matrix4f modelM = new Matrix4f(Transformation.getModelMatrix(format3D));
            if (JGemsShaderManager.this.isUniformExist(new UniformString("model_matrix"))) {
                if (format3D.isOrientedToViewMatrix()) {
                    modelM = Transformation.getOrientedToViewModelMatrix(format3D, viewMatrix);
                }
                this.performModel3DMatrix(modelM);
            }
            if (JGemsShaderManager.this.isUniformExist(new UniformString("view_matrix"))) {
                this.performViewMatrix(viewMatrix);
            }
            if (JGemsShaderManager.this.isUniformExist(new UniformString("model_view_matrix"))) {
                this.performModel3DViewMatrix(modelM, viewMatrix);
            }
        }

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Model<Format3D> model) {
            this.performViewAndModelMatricesSeparately(viewMatrix, model.getFormat());
        }

        public void performViewAndModelMatricesSeparately(Model<Format3D> model) {
            this.performViewAndModelMatricesSeparately(JGemsSceneUtils.getMainCameraViewMatrix(), model);
        }

        public void performPerspectiveMatrix() {
            this.performPerspectiveMatrix(JGemsSceneUtils.getMainPerspectiveMatrix());
        }

        public void performPerspectiveMatrix(Matrix4f Matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("projection_matrix"), Matrix4f);
        }

        public void performOrthographicMatrix(Model<Format2D> model) {
            JGemsShaderManager.this.performUniform(new UniformString("projection_model_matrix"), Transformation.getModelOrthographicMatrix(model.getFormat(), JGemsSceneUtils.getMainOrthographicMatrix()));
        }

        public void performModel3DViewMatrix(Model<Format3D> model, Matrix4f view) {
            JGemsShaderManager.this.performUniform(new UniformString("model_view_matrix"), Transformation.getModelViewMatrix(model.getFormat(), view));
        }

        public void performModel3DViewMatrix(Matrix4f model, Matrix4f view) {
            JGemsShaderManager.this.performUniform(new UniformString("model_view_matrix"), new Matrix4f(view).mul(model));
        }

        public void performModel3DViewMatrix(Matrix4f Matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("model_view_matrix"), Matrix4f);
        }

        public void performViewMatrix(Matrix4f Matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("view_matrix"), Matrix4f);
        }

        public void performModel3DMatrix(Model<Format3D> model) {
            this.performModel3DMatrix(Transformation.getModelMatrix(model.getFormat()));
        }

        public void performModel3DMatrix(Matrix4f Matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("model_matrix"), Matrix4f);
        }
    }
}
