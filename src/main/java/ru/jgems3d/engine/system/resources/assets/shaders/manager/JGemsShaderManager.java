package ru.jgems3d.engine.system.resources.assets.shaders.manager;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.environment.shadow.CascadeShadow;
import ru.jgems3d.engine.graphics.opengl.environment.shadow.PointLightShadow;
import ru.jgems3d.engine.graphics.opengl.environment.shadow.ShadowScene;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsScene;
import ru.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.CubeMapProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.transformation.Transformation;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.materials.samples.ColorSample;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.ISample;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.ShaderContainer;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;

public final class JGemsShaderManager extends ShaderManager {
    private final JGemsShaderUtils shaderUtils;

    public JGemsShaderManager(ShaderContainer shaderContainer) {
        super(shaderContainer);
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

        public void performUniformSampleNoWarn(String uniform, ISample sample) {
            if (!JGemsShaderManager.this.isUniformExist(uniform)) {
                return;
            }
            this.performUniformSample(uniform, sample);
        }

        public void performUniformSample(String uniform, ISample sample) {
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
            if (!JGemsShaderManager.this.isUniformExist("lighting_code")) {
                return;
            }
            int lighting_code = 0;
            if (meshRenderData.getRenderAttributes().isBright()) {
                lighting_code |= 1 << 2;
            }
            JGemsShaderManager.this.performUniform("lighting_code", lighting_code);
        }

        public void performModelMaterialOnShader(Material material) {
            if (material == null) {
                return;
            }

            ISample diffuse = material.getDiffuse();
            IImageSample emission = material.getEmissionMap();
            IImageSample metallic = material.getMetallicMap();
            IImageSample normals = material.getNormalsMap();
            IImageSample specular = material.getSpecularMap();
            CubeMapProgram cubeMapProgram = JGemsHelper.getWorldEnvironment().getSky().getSkyBox().cubeMapTexture();

            int texturing_code = 0;

            this.performCameraData();
            this.performCubeMapProgram("ambient_cubemap", cubeMapProgram);

            if (diffuse != null) {
                if (diffuse instanceof IImageSample) {
                    this.performUniformSampleNoWarn("diffuse_map", diffuse);
                    texturing_code |= 1 << 2;
                } else {
                    if (diffuse instanceof ColorSample) {
                        this.performUniformSampleNoWarn("diffuse_color", diffuse);
                    }
                }
            }
            if (emission != null) {
                this.performUniformSampleNoWarn("emission_map", emission);
                texturing_code |= 1 << 3;
            }
            if (metallic != null) {
                this.performUniformSampleNoWarn("metallic_map", metallic);
                texturing_code |= 1 << 4;
            }
            if (normals != null) {
                this.performUniformSampleNoWarn("normals_map", normals);
                texturing_code |= 1 << 5;
            }
            if (specular != null) {
                this.performUniformSampleNoWarn("specular_map", specular);
                texturing_code |= 1 << 6;
            }

            JGemsShaderManager.this.performUniformNoWarn("texturing_code", texturing_code);
        }

        public void performCameraData() {
            JGemsShaderManager.this.performUniformNoWarn("camera_pos", JGemsHelper.getCurrentCamera().getCamPosition());
        }

        public void performShadowsInfo() {
            JGemsScene scene = JGems3D.get().getScreen().getScene();
            for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
                CascadeShadow cascadeShadow = scene.getSceneRenderer().getShadowScene().getCascadeShadows().get(i);
                if (JGemsShaderManager.this.isUniformExist("shadow_map" + i)) {
                    JGemsShaderManager.this.performUniformTexture("shadow_map" + i, scene.getSceneRenderer().getShadowScene().getShadowPostFBO().getTextureIDByIndex(i), GL30.GL_TEXTURE_2D);
                    JGemsShaderManager.this.performUniformNoWarn("cascade_shadow", ".split_distance", i, cascadeShadow.getSplitDistance());
                    JGemsShaderManager.this.performUniformNoWarn("cascade_shadow", ".projection_view", i, cascadeShadow.getLightProjectionViewMatrix());
                }
            }
            for (int i = 0; i < ShadowScene.MAX_POINT_LIGHTS_SHADOWS; i++) {
                PointLightShadow pointLightShadow = scene.getSceneRenderer().getShadowScene().getPointLightShadows().get(i);
                JGemsShaderManager.this.performUniformNoWarn("far_plane", pointLightShadow.farPlane());
                if (JGemsShaderManager.this.isUniformExist("point_light_cubemap")) {
                    this.performCubeMapProgram("point_light_cubemap", i, pointLightShadow.getPointLightCubeMap().getCubeMapProgram());
                }
            }
        }

        public void performCubeMapProgram(String name, CubeMapProgram cubeMapProgram) {
            this.performCubeMapProgram(name, -1, cubeMapProgram);
        }

        public void performCubeMapProgram(String name, int arrayPos, CubeMapProgram cubeMapProgram) {
            if (cubeMapProgram == null) {
                JGemsHelper.getLogger().warn("CubeMap is NULL!");
                return;
            }
            JGemsShaderManager.this.performUniformTexture(name, arrayPos, cubeMapProgram.getTextureId(), GL30.GL_TEXTURE_CUBE_MAP);
        }

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Model<Format3D> model) {
            if (JGemsShaderManager.this.isUniformExist("model_matrix")) {
                this.performModel3DMatrix(model);
            }
            if (JGemsShaderManager.this.isUniformExist("view_matrix")) {
                this.performViewMatrix(viewMatrix);
            }
            if (JGemsShaderManager.this.isUniformExist("model_view_matrix")) {
                this.performModel3DViewMatrix(model, viewMatrix);
            }
        }

        public void performViewAndModelMatricesSeparately(Model<Format3D> model) {
            this.performViewAndModelMatricesSeparately(JGemsSceneUtils.getMainCameraViewMatrix(), model);
        }

        public void performPerspectiveMatrix() {
            this.performPerspectiveMatrix(JGemsSceneUtils.getMainPerspectiveMatrix());
        }

        public void performPerspectiveMatrix(Matrix4f Matrix4f) {
            JGemsShaderManager.this.performUniform("projection_matrix", Matrix4f);
        }

        public void performOrthographicMatrix(Model<Format2D> model) {
            JGemsShaderManager.this.performUniform("projection_model_matrix", Transformation.getModelOrthographicMatrix(model.getFormat(), JGemsSceneUtils.getMainOrthographicMatrix()));
        }

        public void performModel3DViewMatrix(Model<Format3D> model, Matrix4f view) {
            JGemsShaderManager.this.performUniform("model_view_matrix", Transformation.getModelViewMatrix(model.getFormat(), view));
        }

        public void performModel3DViewMatrix(Matrix4f Matrix4f) {
            JGemsShaderManager.this.performUniform("model_view_matrix", Matrix4f);
        }

        public void performViewMatrix(Matrix4f Matrix4f) {
            JGemsShaderManager.this.performUniform("view_matrix", Matrix4f);
        }

        public void performModel3DMatrix(Model<Format3D> model) {
            this.performModel3DMatrix(Transformation.getModelMatrix(model.getFormat()));
        }

        public void performModel3DMatrix(Matrix4f Matrix4f) {
            JGemsShaderManager.this.performUniform("model_matrix", Matrix4f);
        }
    }
}
