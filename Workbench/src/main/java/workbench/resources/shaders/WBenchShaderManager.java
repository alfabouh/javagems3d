package workbench.resources.shaders;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.scene.JGemsScene;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import workbench.resources.WBenchResourceManager;

import java.nio.FloatBuffer;

public class WBenchShaderManager extends ShaderManager {
    private final Utils shaderUtils;

    public WBenchShaderManager(ShadersContainer shadersContainer) {
        super(shadersContainer);
        this.shaderUtils = new Utils();
    }

    @Override
    public WBenchShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        return (WBenchShaderManager) super.attachUBOs(uniformBufferObjects);
    }

    public WBenchShaderManager copy() {
        return new WBenchShaderManager(this.getShadersContainer());
    }

    public Utils getUtils() {
        return this.shaderUtils;
    }

    public class Utils {
        public Utils() {
        }

        public void performUniformSampleNoWarn(UniformString uniform, ISample sample) {
            if (!WBenchShaderManager.this.isUniformExist(uniform)) {
                return;
            }
            this.performUniformSample(uniform, sample);
        }

        public void performUniformSample(UniformString uniform, ISample sample) {
            if (sample instanceof RGBAColor) {
                RGBAColor color = (RGBAColor) sample;
                WBenchShaderManager.this.performUniform(uniform, UniformFunctions.VEC4F(color.getColor()));
            } else {
                if (sample instanceof ImageTexture) {
                    ImageTexture textureSample = (ImageTexture) sample;
                    WBenchShaderManager.this.performUniformTexture(uniform, textureSample.getSamplerId(), textureSample.getTextureId(), textureSample.getTextureAttachment());
                }
            }
        }

        public void performRenderDataOnShader(RenderAttributes objectRenderingConfiguration) {
            if (!WBenchShaderManager.this.isUniformExist(new UniformString("lighting_code"))) {
                return;
            }
            int lighting_code = 0;
            if (objectRenderingConfiguration.isDefaultBrightLighted()) {
                lighting_code |= 1 << 2;
            }
            WBenchShaderManager.this.performUniform(new UniformString("lighting_code"), UniformFunctions.INTEGER(lighting_code));
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
            if (WBenchShaderManager.this.isUniformExist(new UniformString("ambient_cube_map"))) {
                WBenchShaderManager.this.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
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

            WBenchShaderManager.this.performUniformNoWarn(new UniformString("texturing_code"), UniformFunctions.INTEGER(texturing_code));
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
                        ShaderStorageBufferProgram.updateSubDataSSBO(WBenchResourceManager.globalShaderAssets.Bones, 0, fb);
                    }
                    WBenchShaderManager.this.performUniformNoWarn(new UniformString("hasAnimations"), UniformFunctions.BOOLEAN(true));
                    return true;
                }
            }
            WBenchShaderManager.this.performUniformNoWarn(new UniformString("hasAnimations"), UniformFunctions.BOOLEAN(false));
            return false;
        }

        public void performCameraData() {
            WBenchShaderManager.this.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(JGemsHelper.CAMERA.getCurrentCamera().getCamPosition()));
        }

        public void performShadowsInfo() {
            JGemsScene scene = JGems3D.get().getScreen().getScene();
            SceneWorld sceneWorld = (SceneWorld) scene.getSceneRenderer().getWorld();
            for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
                SunLightShadow.Cascade cascade = sceneWorld.getEnvironment().getShadowScene().getSunLightShadow().getCascades().get(i);
                if (WBenchShaderManager.this.isUniformExist(new UniformString("sun_shadow_map", i))) {
                    WBenchShaderManager.this.performUniformTexture(new UniformString("sun_shadow_map", i), sceneWorld.getEnvironment().getShadowScene().getSunLightShadow().getSunShadowFBO().getTextureByIndex(i));
                    WBenchShaderManager.this.performUniformNoWarn(new UniformString("cascade_shadow", ".split_distance", i), UniformFunctions.FLOAT(cascade.getSplitDistance()));
                    WBenchShaderManager.this.performUniformNoWarn(new UniformString("cascade_shadow", ".projection_view", i), UniformFunctions.MAT4F(cascade.getLightProjectionViewMatrix()));
                    WBenchShaderManager.this.performUniformNoWarn(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_POSITIVE_EXPONENT));
                    WBenchShaderManager.this.performUniformNoWarn(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_NEGATIVE_EXPONENT));
                }
            }
            for (int i = 0; i < JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS; i++) {
                PointLightShadow pointLightShadow = sceneWorld.getEnvironment().getShadowScene().getPointLightShadows().get(i);
                WBenchShaderManager.this.performUniformNoWarn(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
                if (WBenchShaderManager.this.isUniformExist(new UniformString("point_light_cubemap", i))) {
                    WBenchShaderManager.this.performUniformTexture(new UniformString("point_light_cubemap", i), pointLightShadow.getPointLightCubeMap().getCubeMapProgram());
                }
            }
        }

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Pose3D pose) {
            Matrix4f modelM = new Matrix4f(TransformUtils.getModelMatrix(pose));
            if (WBenchShaderManager.this.isUniformExist(new UniformString("model_matrix"))) {
                if (pose.isOrientedToViewMatrix()) {
                    modelM = TransformUtils.getOrientedToViewModelMatrix(pose, viewMatrix);
                }
                this.performModel3DMatrix(modelM);
            }
            if (WBenchShaderManager.this.isUniformExist(new UniformString("view_matrix"))) {
                this.performViewMatrix(viewMatrix);
            }
            if (WBenchShaderManager.this.isUniformExist(new UniformString("model_view_matrix"))) {
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
            WBenchShaderManager.this.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performOrthographicMatrix(Model2D model) {
            WBenchShaderManager.this.performUniform(new UniformString("projection_model_matrix"), UniformFunctions.MAT4F(TransformUtils.getModelOrthographicMatrix(model.getPose(), JGemsTransformManager.INSTANCE.getOrthographicMatrix())));
        }

        public void performModel3DViewMatrix(Model3D model, Matrix4f view) {
            WBenchShaderManager.this.performUniform(new UniformString("model_view_matrix"), UniformFunctions.MAT4F(TransformUtils.getModelViewMatrix(model.getPose(), view)));
        }

        public void performModel3DViewMatrix(Matrix4f model, Matrix4f view) {
            WBenchShaderManager.this.performUniform(new UniformString("model_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(view).mul(model)));
        }

        public void performModel3DViewMatrix(Matrix4f matrix4f) {
            WBenchShaderManager.this.performUniform(new UniformString("model_view_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performViewMatrix(Matrix4f matrix4f) {
            WBenchShaderManager.this.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(matrix4f));
        }

        public void performModel3DMatrix(Model3D model) {
            this.performModel3DMatrix(TransformUtils.getModelMatrix(model.getPose()));
        }

        public void performModel3DMatrix(Matrix4f matrix4f) {
            WBenchShaderManager.this.performUniform(new UniformString("model_matrix"), UniformFunctions.MAT4F(matrix4f));
        }
    }
}