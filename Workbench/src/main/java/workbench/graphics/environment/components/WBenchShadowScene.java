package workbench.graphics.environment.components;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.EditorInterface;
import workbench.resources.WBenchResourceManager;

import java.util.function.Consumer;

public class WBenchShadowScene extends ShadowScene {
    public WBenchShadowScene(IEnvironment environment) {
        super(environment, JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES);
    }

    protected @NotNull Vector2i getShadowResolution() {
        return new Vector2i((int) (JGemsConfig.SYSTEM.MAX_SHADOW_RES * 0.5f));
    }

    @Override
    protected int getMaxPointLightShadows() {
        return JGemsConfig.SYSTEM.MAX_POINT_LIGHTS_SHADOWS;
    }

    @Override
    protected boolean shouldNotRenderShadows() {
        return !JGemsConfig.SYSTEM.USE_SHADOWS || JGemsConfig.DEBUG.FULL_BRIGHT || !EditorInterface.VIEW_SHADOWS || WBenchOpenGLRenderer.isRenderingBackgroundScene();
    }

    @Override
    protected void blurShadows(FBOTexture2DProgram sunShadowFBO) {
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getIndirectSSBO() {
        return WBenchResourceManager.localShaderAssets.IndirectBufferData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getPropertiesSSBO() {
        return WBenchResourceManager.localShaderAssets.PropertiesData;
    }

    @Override
    protected @NotNull Consumer<JGemsShaderManager> getUniformsConsumerSunShadows(SunLightShadow.Cascade cascade, Matrix4f lightProjection) {
        return (shaderManager) -> {
            shaderManager.performUniform(new UniformString("projection_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
            shaderManager.performUniformNoWarn(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
            shaderManager.performUniformNoWarn(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
            shaderManager.performUniformTexture(new UniformString("animations_matrix"), WBenchResourceManager.getAnimationsTextureBuffer());
        };
    }

    @Override
    protected @NotNull Consumer<JGemsShaderManager> getUniformsConsumerPointLightShadows(PointLightShadow pointLightShadow, Matrix4f lightProjection) {
        return (shaderManager) -> {
            shaderManager.performUniform(new UniformString("projection_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
            shaderManager.performUniform(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
            shaderManager.performUniform(new UniformString("lightPos"), UniformFunctions.VEC3F(pointLightShadow.getPointLight().getLightPosition()));
        };
    }
}
