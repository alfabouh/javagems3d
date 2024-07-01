package ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.render.opengl.environment.shadow.CascadeShadow;
import ru.alfabouh.jgems3d.engine.render.opengl.environment.shadow.PointLightShadow;
import ru.alfabouh.jgems3d.engine.render.opengl.environment.shadow.ShadowScene;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.JGemsScene;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.programs.CubeMapProgram;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
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
import ru.alfabouh.jgems3d.logger.SystemLogging;

public final class JGemsShaderManager extends ShaderManager {
    private final JGemsShaderUtils shaderUtils;

    public JGemsShaderManager(ShaderGroup shaderGroup) {
        super(shaderGroup);
        this.shaderUtils = new JGemsShaderUtils();
    }

    @Override
    public JGemsShaderManager setUseForGBuffer(boolean useForGBuffer) {
        return (JGemsShaderManager) super.setUseForGBuffer(useForGBuffer);
    }

    @Override
    public JGemsShaderManager addUBO(UniformBufferObject uniformBufferObject) {
        return (JGemsShaderManager) super.addUBO(uniformBufferObject);
    }

    public JGemsShaderManager copy() {
        return new JGemsShaderManager(this.getShaderGroup());
    }

    public JGemsShaderUtils getUtils() {
        return this.shaderUtils;
    }

    public class JGemsShaderUtils {
        public JGemsShaderUtils() {
        }

        public void performConstraintsOnShader(ModelRenderParams modelRenderParams) {
            if (!JGemsShaderManager.this.checkUniformInGroup("lighting_code")) {
                return;
            }
            int lighting_code = 0;
            if (modelRenderParams.isBright()) {
                lighting_code |= 1 << 2;
            }
            JGemsShaderManager.this.performUniform("lighting_code", lighting_code);
        }

        public void performModelMaterialOnShader(Material material, boolean passShadows) {
            if (material == null) {
                return;
            }

            //TBoxShaderManager.this.performUniformNoWarn("show_cascades", JGemsSceneRender.CURRENT_DEBUG_MODE);

            ISample diffuse = material.getDiffuse();
            IImageSample emissive = material.getEmissive();
            IImageSample metallic = material.getMetallic();
            IImageSample normals = material.getNormals();
            IImageSample specular = material.getSpecular();
            CubeMapProgram cubeMapProgram = JGems.get().getScreen().getRenderWorld().getEnvironment().getSky().getSkyBox().cubeMapTexture();

            int texturing_code = 0;
            for (int i = 0; i < 12; i++) {
                JGemsScene.activeGlTexture(i);
                GL30.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            }

            this.performCameraData();
            this.performCubeMapProgram("ambient_cubemap", cubeMapProgram);

            if (diffuse != null) {
                if (diffuse instanceof IImageSample) {
                    final int code = 0;
                    IImageSample imageSample = ((IImageSample) diffuse);
                    JGemsScene.activeGlTexture(code);
                    imageSample.bindTexture();
                    JGemsShaderManager.this.performUniformNoWarn("diffuse_map", code);
                    texturing_code |= 1 << 2;
                } else {
                    if (diffuse instanceof ColorSample) {
                        JGemsShaderManager.this.performUniformNoWarn("diffuse_color", ((ColorSample) diffuse).getColor());
                    }
                }
            }
            if (emissive != null) {
                final int code = 1;
                JGemsScene.activeGlTexture(code);
                emissive.bindTexture();
                JGemsShaderManager.this.performUniformNoWarn("emissive_map", code);
                texturing_code |= 1 << 3;
            }
            if (metallic != null) {
                final int code = 2;
                JGemsScene.activeGlTexture(code);
                metallic.bindTexture();
                JGemsShaderManager.this.performUniformNoWarn("metallic_map", code);
                texturing_code |= 1 << 4;
            }
            if (normals != null) {
                final int code = 3;
                JGemsScene.activeGlTexture(code);
                normals.bindTexture();
                JGemsShaderManager.this.performUniformNoWarn("normals_map", code);
                texturing_code |= 1 << 5;
            }
            if (specular != null) {
                final int code = 4;
                JGemsScene.activeGlTexture(code);
                specular.bindTexture();
                JGemsShaderManager.this.performUniformNoWarn("specular_map", code);
                texturing_code |= 1 << 6;
            }
            if (passShadows) {
                this.performShadowsInfo();
            }

            JGemsShaderManager.this.performUniformNoWarn("texturing_code", texturing_code);
        }

        public void performCameraData() {
            JGemsShaderManager.this.performUniformNoWarn("camera_pos", JGems.get().getScreen().getScene().getCurrentCamera().getCamPosition());
        }

        public void performShadowsInfo() {
            JGemsScene scene = JGems.get().getScreen().getScene();
            for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
                int startCode = 6;
                CascadeShadow cascadeShadow = scene.getSceneRender().getShadowScene().getCascadeShadows().get(i);
                JGemsScene.activeGlTexture(startCode + i);
                scene.getSceneRender().getShadowScene().getShadowPostFBO().bindTexture(i);
                JGemsShaderManager.this.performUniformNoWarn("shadow_map" + i, startCode + i);
                JGemsShaderManager.this.performUniformNoWarn("cascade_shadow", ".split_distance", i, cascadeShadow.getSplitDistance());
                JGemsShaderManager.this.performUniformNoWarn("cascade_shadow", ".projection_view", i, cascadeShadow.getLightProjectionViewMatrix());
            }
            for (int i = 0; i < ShadowScene.MAX_POINT_LIGHTS_SHADOWS; i++) {
                PointLightShadow pointLightShadow = scene.getSceneRender().getShadowScene().getPointLightShadows().get(i);
                final int code = 9 + i;
                JGemsScene.activeGlTexture(code);
                pointLightShadow.getPointLightCubeMap().bindCubeMap();
                JGemsShaderManager.this.performUniformNoWarn("far_plane", pointLightShadow.farPlane());
                JGemsShaderManager.this.performUniformNoWarn("point_light_cubemap", i, code);
            }
        }

        public void performViewAndModelMatricesSeparately(Matrix4f viewMatrix, Model<Format3D> model) {
            if (JGemsShaderManager.this.checkUniformInGroup("model_matrix")) {
                this.performModel3DMatrix(model);
            }
            if (JGemsShaderManager.this.checkUniformInGroup("view_matrix")) {
                this.performViewMatrix(viewMatrix);
            }
            if (JGemsShaderManager.this.checkUniformInGroup("model_view_matrix")) {
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

        public void performCubeMapProgram(String name, CubeMapProgram cubeMapProgram) {
            if (cubeMapProgram != null) {
                final int code = 5;
                JGemsScene.activeGlTexture(code);
                cubeMapProgram.bindCubeMap();
                JGemsShaderManager.this.performUniformNoWarn(name, code);
            }
        }

        public void performCubeMap(CubeMapProgram cubeMapTexture) {
            this.performCubeMap(0, cubeMapTexture);
        }

        public void performCubeMap(int code, CubeMapProgram cubeMapTexture) {
            if (cubeMapTexture == null) {
                SystemLogging.get().getLogManager().warn("CubeMap is NULL!");
                return;
            }
            JGemsShaderManager.this.performUniform("cube_map_sampler", code);
            GL30.glActiveTexture(GL30.GL_TEXTURE0 + code);
            GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, cubeMapTexture.getTextureId());
        }
    }
}
