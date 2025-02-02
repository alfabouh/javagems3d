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

import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.joml.Matrix4f;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.rendering.scene.JGemsScene;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;

import javagems3d.system.resources.assets.models.pose.Pose2D;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public final class JGemsShaderManager extends ShaderManager {
    private final JGemsShaderUtils shaderUtils;

    public JGemsShaderManager(ShadersContainer shadersContainer) {
        super(shadersContainer);
        this.shaderUtils = new JGemsShaderUtils();
    }

    @Override
    public JGemsShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        return (JGemsShaderManager) super.attachUBOs(uniformBufferObjects);
    }

    public JGemsShaderManager copy() {
        return new JGemsShaderManager(this.getShadersContainer());
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
                RGBAColor color = (RGBAColor) sample;
                JGemsShaderManager.this.performUniform(uniform, UniformFunctions.VEC4F(color.getColor()));
            } else {
                if (sample instanceof ImageTexture) {
                    ImageTexture textureSample = (ImageTexture) sample;
                    JGemsShaderManager.this.performUniformTexture(uniform, textureSample.getSamplerId(), textureSample.getTextureId(), textureSample.getTextureAttachment());
                }
            }
        }

        public void performRenderDataOnShader(RenderAttributes objectRenderingConfiguration) {
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
            ImageBasedTexture emission = material.getEmissionMap();
            ImageBasedTexture metallic = material.getMetallicMap();
            ImageBasedTexture normals = material.getNormalsMap();
            ImageBasedTexture specular = material.getSpecularMap();
            CubeMapTexture cubeMapProgram = JGemsHelper.ENVIRONMENT.getWorldEnvironment().getSkyBox().getSky2DTexture();

            int texturing_code = 0;

            this.performCameraData();
            if (JGemsShaderManager.this.isUniformExist(new UniformString("ambient_cube_map"))) {
                JGemsShaderManager.this.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
            }

            if (diffuse != null) {
                if (diffuse instanceof ImageBasedTexture) {
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
            if (animated.hasAnimationData()) {
                Matrix4f[] matrices = animated.getAnimationData().getCurrentAnimationFrame().getBoneMatrices();
                if (matrices != null) {
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        int length = matrices.length;
                        FloatBuffer fb = stack.mallocFloat(16 * length);
                        for (int i = 0; i < length; i++) {
                            matrices[i].get(16 * i, fb);
                        }
                        ShaderStorageBufferProgram.updateSubDataSSBO(JGemsResourceManager.globalShaderAssets.Bones, 0, fb);
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
                SunLightShadow.Cascade cascade = scene.getSceneRenderer().getSceneWorld().getEnvironment().getShadowScene().getSunLightShadow().getCascades().get(i);
                if (JGemsShaderManager.this.isUniformExist(new UniformString("sun_shadow_map", i))) {
                    JGemsShaderManager.this.performUniformTexture(new UniformString("sun_shadow_map", i), scene.getSceneRenderer().getSceneWorld().getEnvironment().getShadowScene().getSunLightShadow().getSunShadowFBO().getTextureByIndex(i));
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("cascade_shadow", ".split_distance", i), UniformFunctions.FLOAT(cascade.getSplitDistance()));
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("cascade_shadow", ".projection_view", i), UniformFunctions.MAT4F(cascade.getLightProjectionViewMatrix()));
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_POSITIVE_EXPONENT));
                    JGemsShaderManager.this.performUniformNoWarn(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_NEGATIVE_EXPONENT));
                }
            }
            for (int i = 0; i < JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS; i++) {
                PointLightShadow pointLightShadow = scene.getSceneRenderer().getSceneWorld().getEnvironment().getShadowScene().getPointLightShadows().get(i);
                JGemsShaderManager.this.performUniformNoWarn(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
                if (JGemsShaderManager.this.isUniformExist(new UniformString("point_light_cubemap", i))) {
                    JGemsShaderManager.this.performUniformTexture(new UniformString("point_light_cubemap", i), pointLightShadow.getPointLightCubeMap().getCubeMapProgram());
                }
            }
        }

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Pose3D pose) {
            Matrix4f modelM = new Matrix4f(TransformUtils.getModelMatrix(pose));
            if (JGemsShaderManager.this.isUniformExist(new UniformString("model_matrix"))) {
                if (pose.isOrientedToViewMatrix()) {
                    modelM = TransformUtils.getOrientedToViewModelMatrix(pose, viewMatrix);
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

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Model3D model) {
            this.performViewAndModelMatricesSeparately(viewMatrix, model.getPose());
        }

        public void performViewAndModelMatricesSeparately(Model3D model) {
            this.performViewAndModelMatricesSeparately(JGemsTransformManager.INSTANCE.getCameraViewMatrix(), model);
        }

        public void performPerspectiveMatrix() {
            this.performPerspectiveMatrix(JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
        }

        public void performPerspectiveMatrix(Matrix4f matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performOrthographicMatrix(Model2D model) {
            JGemsShaderManager.this.performUniform(new UniformString("projection_model_matrix"), UniformFunctions.MAT4F(TransformUtils.getModelOrthographicMatrix(model.getPose(), JGemsTransformManager.INSTANCE.getOrthographicMatrix())));
        }

        public void performModel3DViewMatrix(Model3D model, Matrix4f view) {
            JGemsShaderManager.this.performUniform(new UniformString("model_view_matrix"), UniformFunctions.MAT4F(TransformUtils.getModelViewMatrix(model.getPose(), view)));
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

        public void performModel3DMatrix(Model3D model) {
            this.performModel3DMatrix(TransformUtils.getModelMatrix(model.getPose()));
        }

        public void performModel3DMatrix(Matrix4f matrix4f) {
            JGemsShaderManager.this.performUniform(new UniformString("model_matrix"), UniformFunctions.MAT4F(matrix4f));
        }
    }
}
