package javagems3d.graphics.environment.shadows.scene;

import javagems3d.JGems3D;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.util.function.Consumer;

public class JGemsShadowScene extends ShadowScene {
    public JGemsShadowScene(IEnvironment environment) {
        super(environment, JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES);
    }

    public static float qualityMultiplier() {
        int i = (int) JGemsHelper.math().clamp(JGems3D.get().getGameSettings().shadowQuality.getValue(), 0.0f, 2.0f);
        return i == 2 ? 1.0f : i == 1 ? 0.5f : 0.25f;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getIndirectSSBO() {
        return JGemsResourceManager.globalShaderAssets.ShadowSceneIndirectBufferData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getPropertiesSSBO() {
        return JGemsResourceManager.globalShaderAssets.ShadowScenePropertiesData;
    }

    protected @NotNull Vector2i getShadowResolution() {
        return new Vector2i((int) (JGemsConfig.SYSTEM.MAX_SHADOW_RES * JGemsShadowScene.qualityMultiplier()));
    }

    @Override
    protected int getMaxPointLightShadows() {
        return JGemsConfig.SYSTEM.MAX_POINT_LIGHTS_SHADOWS;
    }

    @Override
    protected boolean shouldNotRenderShadows() {
        return !JGemsConfig.SYSTEM.USE_SHADOWS || JGemsConfig.DEBUG.FULL_BRIGHT;
    }

    protected void blurShadows(FBOTexture2DProgram sunShadowFBO) {
        try (Model2D screenModel = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getSunLightShadow().getShadowMapResolution()), 0)) {
            final JGemsShaderManager blurring = JGemsResourceManager.globalShaderAssets.blur_box;
            this.blurSunShadow(screenModel, sunShadowFBO, blurring, 1.0f);
        }
    }

    private void blurSunShadow(Model2D screenModel, FBOTexture2DProgram sunShadowFBO, final JGemsShaderManager blurring, float blurringConst) {
        sunShadowFBO.bindFBO();
        Vector2i resolution = this.getSunLightShadow().getShadowMapResolution();
        OpenGLRenderer.setViewPort(resolution);
        blurring.beginShading();
        blurring.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), UniformFunctions.MAT4F(TransformUtils.getModelOrthographicMatrix(screenModel.getPose(), TransformUtils.getOrthographic2DMatrix(0, resolution.x, resolution.y, 0))));
        for (int i = 0; i < this.getSunLightShadow().getTotalCascades(); i++) {
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
            sunShadowFBO.connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            blurring.performUniform(new UniformString(DefaultUniformDefinitions.BLUR), UniformFunctions.FLOAT(blurringConst));
            blurring.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), sunShadowFBO.getTextureByIndex(i));
            JGemsHelper.render().renderModel2D(screenModel, GL46.GL_TRIANGLES);
        }
        blurring.endShading();
        sunShadowFBO.unBindFBO();
    }

    @Override
    protected @NotNull Consumer<JGemsShaderManager> getUniformsConsumerSunShadows(SunLightShadow.Cascade cascade, Matrix4f lightProjection) {
        return (shaderManager) -> {
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_VIEW_MATRIX), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
            shaderManager.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.POS_EXP), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
            shaderManager.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.NEG_EXP), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
            shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.ANIMATIONS_MATRIX), JGemsHelper.resources().getAnimationsTextureBuffer());
        };
    }

    @Override
    protected @NotNull Consumer<JGemsShaderManager> getUniformsConsumerPointLightShadows(PointLightShadow pointLightShadow, Matrix4f lightProjection) {
        return (shaderManager) -> {
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_VIEW_MATRIX), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.FAR_PLANE), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.LIGHT_POS), UniformFunctions.VEC3F(pointLightShadow.getPointLight().getLightPosition()));
        };
    }
}
