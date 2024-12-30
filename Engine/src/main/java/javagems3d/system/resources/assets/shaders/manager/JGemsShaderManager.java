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

import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.rendering.configuration.ObjectRenderConfiguration;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.manager.JGemsResourceManager;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.environment.shadows.CascadeShadow;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.rendering.JGemsSceneUtils;
import javagems3d.graphics.rendering.scene.JGemsScene;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.assets.texturing.base.IImageTexture;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public final class JGemsShaderManager extends ShaderManager {
    private final JGemsShaderUtils shaderUtils;

    public JGemsShaderManager(ShaderRenderingTarget shaderRenderingTarget, ShadersContainer shadersContainer) {
        super(shaderRenderingTarget, shadersContainer);
        this.shaderUtils = new JGemsShaderUtils();
    }

    @Override
    public JGemsShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        return (JGemsShaderManager) super.attachUBOs(uniformBufferObjects);
    }

    public JGemsShaderManager copy() {
        return new JGemsShaderManager(this.getShaderTarget(), this.getShaderContainer());
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
            if (sample instanceof RGBAColor) {
                RGBAColor RGBAColor = (RGBAColor) sample;
                JGemsShaderManager.this.performUniform(uniform, UniformFunctions.VEC4F(RGBAColor.getColor()));
            } else {
                if (sample instanceof ImageTexture) {
                    ImageTexture textureSample = (ImageTexture) sample;
                    JGemsShaderManager.this.performUniformTexture(uniform, textureSample.getTextureId(), textureSample.getTextureAttachment());
                }
            }
        }

        public void performRenderDataOnShader(ObjectRenderConfiguration objectRenderingConfiguration) {
            if (!JGemsShaderManager.this.isUniformExist(new UniformString("lighting_code"))) {
                return;
            }
            int lighting_code = 0;
            if (objectRenderingConfiguration.isDefaultBrightLighted()) {
                lighting_code |= 1 << 2;
            }
            JGemsShaderManager.this.performUniform(new UniformString("lighting_code"), UniformFunctions.INTEGER(lighting_code));
        }

        public void performModelMaterialOnShader(Material material) {
            if (material == null) {
                return;
            }

            ISample diffuse = material.getDiffuse();
            IImageTexture emission = material.getEmissionMap();
            IImageTexture metallic = material.getMetallicMap();
            IImageTexture normals = material.getNormalsMap();
            IImageTexture specular = material.getSpecularMap();
            CubeMapTexture cubeMapProgram = JGemsHelper.ENVIRONMENT.getWorldEnvironment().getSkyBox().getSky2DTexture();

            int texturing_code = 0;

            this.performCameraData();
            if (JGemsShaderManager.this.isUniformExist(new UniformString("ambient_cubemap"))) {
                this.performCubeMapProgram(new UniformString("ambient_cubemap"), cubeMapProgram.getTextureId());
            }

            if (diffuse != null) {
                if (diffuse instanceof IImageTexture) {
                    this.performUniformSampleNoWarn(new UniformString("diffuse_map"), diffuse);
                    texturing_code |= 1 << 2;
                } else {
                    if (diffuse instanceof RGBAColor) {
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

            JGemsShaderManager.this.performUniformNoWarn(new UniformString("texturing_code"), UniformFunctions.INTEGER(texturing_code));
        }

        public boolean performAnimationsInfo(IAnimated animated) {
            if (animated.hasAnimations()) {
                Matrix4f[] matrices = animated.getAnimationData().getCurrentAnimationFrame().getBoneMatrices();
                if (matrices != null) {
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        int length = matrices.length;
                        FloatBuffer fb = stack.mallocFloat(16 * length);
                        for (int i = 0; i < length; i++) {
                            matrices[i].get(16 * i, fb);
                        }
                        ShaderStorageBufferProgram.fillSSBOWithData(JGemsResourceManager.globalShaderAssets.Bones, 0, fb);
                    }
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("hasAnimations"), UniformFunctions.BOOLEAN(true));
                    return true;
                }
            }
            JGemsShaderManager.this.performUniformNoWarn(new UniformString("hasAnimations"), UniformFunctions.BOOLEAN(false));
            return false;
        }

        public void performCameraData() {
            JGemsShaderManager.this.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(JGemsHelper.CAMERA.getCurrentCamera().getCamPosition()));
        }

        public void performShadowsInfo() {
            JGemsScene scene = JGems3D.get().getScreen().getScene();
            for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
                CascadeShadow cascadeShadow = scene.getSceneRenderer().getSceneWorld().getEnvironment().getShadowScene().getCascadeShadows().get(i);
                if (JGemsShaderManager.this.isUniformExist(new UniformString("sun_shadow_map", i))) {
                    JGemsShaderManager.this.performUniformTexture(new UniformString("sun_shadow_map", i), scene.getSceneRenderer().getSceneWorld().getEnvironment().getShadowScene().getShadowPostFBO().getTextureIDByIndex(i), GL46.GL_TEXTURE_2D);
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("cascade_shadow", ".split_distance", i), UniformFunctions.FLOAT(cascadeShadow.getSplitDistance()));
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("cascade_shadow", ".projection_view", i), UniformFunctions.MAT4F(cascadeShadow.getLightProjectionViewMatrix()));
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_POSITIVE_EXPONENT));
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_NEGATIVE_EXPONENT));
                }
            }
            for (int i = 0; i < JGemsRenderingGlobalConstants.MAX_POINT_LIGHTS_SHADOWS; i++) {
                PointLightShadow pointLightShadow = scene.getSceneRenderer().getSceneWorld().getEnvironment().getShadowScene().getPointLightShadows().get(i);
                JGemsShaderManager.this.performUniformNoWarn(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
                if (JGemsShaderManager.this.isUniformExist(new UniformString("point_light_cubemap", i))) {
                    this.performCubeMapProgram(new UniformString("point_light_cubemap", i), pointLightShadow.getPointLightCubeMap().getCubeMapProgram().getTextureId());
                }
            }
        }

        public void performCubeMapProgram(UniformString uniform, int cubeMapTextureId) {
            JGemsShaderManager.this.performUniformTexture(uniform, cubeMapTextureId, GL46.GL_TEXTURE_CUBE_MAP);
        }

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Format3D format3D) {
            Matrix4f modelM = new Matrix4f(TransformationUtils.getModelMatrix(format3D));
            if (JGemsShaderManager.this.isUniformExist(new UniformString("model_matrix"))) {
                if (format3D.isOrientedToViewMatrix()) {
                    modelM = TransformationUtils.getOrientedToViewModelMatrix(format3D, viewMatrix);
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

        public void performPerspectiveMatrix(Matrix4f matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performOrthographicMatrix(Model<Format2D> model) {
            JGemsShaderManager.this.performUniform(new UniformString("projection_model_matrix"), UniformFunctions.MAT4F(TransformationUtils.getModelOrthographicMatrix(model.getFormat(), JGemsSceneUtils.getMainOrthographicMatrix())));
        }

        public void performModel3DViewMatrix(Model<Format3D> model, Matrix4f view) {
            JGemsShaderManager.this.performUniform(new UniformString("model_view_matrix"), UniformFunctions.MAT4F(TransformationUtils.getModelViewMatrix(model.getFormat(), view)));
        }

        public void performModel3DViewMatrix(Matrix4f model, Matrix4f view) {
            JGemsShaderManager.this.performUniform(new UniformString("model_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(view).mul(model)));
        }

        public void performModel3DViewMatrix(Matrix4f matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("model_view_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performViewMatrix(Matrix4f matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performModel3DMatrix(Model<Format3D> model) {
            this.performModel3DMatrix(TransformationUtils.getModelMatrix(model.getFormat()));
        }

        public void performModel3DMatrix(Matrix4f matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("model_matrix"), UniformFunctions.MAT4F(matrix4f));
        }
    }
}
