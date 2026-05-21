package javagems3d.graphics.environment.shadows.scene;

import javagems3d.JGems3D;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SpotLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
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
    private int sunShadowMapsBasicResolution;
    private int pointLightShadowMapsBasicResolution;
    private int spotLightShadowMapsBasicResolution;

    public JGemsShadowScene(IEnvironment environment) {
        super(environment, JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES, JGemsResourceManager.globalShaderAssets.blur5, JGemsResourceManager.globalShaderAssets.blur5);
    }

    @Override
    protected void preInit() {
        this.sunShadowMapsBasicResolution = JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
        this.pointLightShadowMapsBasicResolution = JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
        this.spotLightShadowMapsBasicResolution = JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
    }

    public static float qualityMultiplier() {
        int i = (int) JGemsHelper.math().clamp(JGems3D.get().getGameSettings().shadowQuality.getValue(), 0.0f, 2.0f);
        return i == 2 ? 1.0f : i == 1 ? 0.5f : 0.25f;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getSunIndirectSSBO() {
        return JGemsResourceManager.globalShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getSunPropertiesSSBO() {
        return JGemsResourceManager.globalShaderAssets.MainScenePropertiesData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getPointLightIndirectSSBO() {
        return JGemsResourceManager.globalShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getPointLightPropertiesSSBO() {
        return JGemsResourceManager.globalShaderAssets.MainScenePropertiesData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getSpotLightIndirectSSBO() {
        return JGemsResourceManager.globalShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getSpotLightPropertiesSSBO() {
        return JGemsResourceManager.globalShaderAssets.MainScenePropertiesData;
    }

    @Override
    protected @NotNull Vector2i getSunShadowResolution() {
        return new Vector2i((int) (this.sunShadowMapsBasicResolution * JGemsShadowScene.qualityMultiplier()));
    }

    @Override
    protected @NotNull Vector2i getPointLightShadowResolution() {
        return new Vector2i((int) (this.pointLightShadowMapsBasicResolution * JGemsShadowScene.qualityMultiplier()));
    }

    @Override
    protected @NotNull Vector2i getSpotLightShadowResolution() {
        return new Vector2i((int) (this.spotLightShadowMapsBasicResolution * JGemsShadowScene.qualityMultiplier()));
    }

    @Override
    protected int getMaxPointLightShadows() {
        return JGemsConfig.SYSTEM.MAX_POINT_LIGHTS_SHADOWS;
    }

    @Override
    protected int getMaxSpotLightShadows() {
        return JGemsConfig.SYSTEM.MAX_SPOT_LIGHTS_SHADOWS;
    }

    @Override
    protected boolean shouldNotRenderShadows() {
        return !JGemsConfig.SYSTEM.USE_SHADOWS || JGemsConfig.DEBUG.FULL_BRIGHT || JGemsConfig.DEBUG.WIREFRAME_RENDERING;
    }

    public int getSunShadowMapsBasicResolution() {
        return this.sunShadowMapsBasicResolution;
    }

    public JGemsShadowScene setSunShadowMapsBasicResolution(int sunShadowMapsBasicResolution) {
        this.sunShadowMapsBasicResolution = sunShadowMapsBasicResolution;
        return this;
    }

    public int getPointLightShadowMapsBasicResolution() {
        return this.pointLightShadowMapsBasicResolution;
    }

    public JGemsShadowScene setPointLightShadowMapsBasicResolution(int pointLightShadowMapsBasicResolution) {
        this.pointLightShadowMapsBasicResolution = pointLightShadowMapsBasicResolution;
        return this;
    }

    public int getSpotLightShadowMapsBasicResolution() {
        return this.spotLightShadowMapsBasicResolution;
    }

    public JGemsShadowScene setSpotLightShadowMapsBasicResolution(int spotLightShadowMapsBasicResolution) {
        this.spotLightShadowMapsBasicResolution = spotLightShadowMapsBasicResolution;
        return this;
    }

    @Override
    protected void blurShadows() {
        if (true) {
          //  return;
        }
        try (Model2D screenModel = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getSunLightShadow().getShadowMapResolution()), 0)) {
            this.getSunLightShadow().blur(screenModel, JGemsConfig.SYSTEM.MAX_CASCADE_SUN_SHADOWS_TO_BLUR);
        }

       this.getSpotLightShadows().forEach(e -> {
           try (Model2D screenModel = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(e.getShadowMapResolution()), 0)) {
               e.blur(screenModel);
           }
       });
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
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.POINT_LIGHT_FAR_PLANE), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.LIGHT_POS), UniformFunctions.VEC3F(pointLightShadow.getPointLight().getLightPosition()));
        };
    }

    @Override
    protected @NotNull Consumer<JGemsShaderManager> getUniformsConsumerSpotLightShadows(SpotLightShadow spotLightShadow, Matrix4f lightProjection) {
        return (shaderManager) -> {
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.SPOT_LIGHT_FAR_PLANE), UniformFunctions.FLOAT(spotLightShadow.farPlane()));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_VIEW_MATRIX), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.LIGHT_POS), UniformFunctions.VEC3F(spotLightShadow.getSpotLight().getLightPosition()));
        };
    }
}
