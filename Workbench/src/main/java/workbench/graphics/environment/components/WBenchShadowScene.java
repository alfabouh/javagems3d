package workbench.graphics.environment.components;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SpotLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.objects.WBenchCommonObject;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.project.map.settings.MapProjectSettings;
import workbench.resources.WBenchResourceManager;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WBenchShadowScene extends ShadowScene implements ISnapshotCompatible<WBenchShadowScene.WBenchShadowSceneSnapshotData> {
    public int sunShadowMapResolution;
    public int pointLightShadowMapResolution;
    public int spotLightShadowMapResolution;

    public WBenchShadowScene(IEnvironment environment) {
        super(environment, JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES);
    }

    @Override
    public void createResources(OpenGLRenderer openGLRenderer) {
        super.createResources(openGLRenderer);
        this.sunShadowMapResolution = JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
        this.pointLightShadowMapResolution = JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
        this.spotLightShadowMapResolution = JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;

    }

    protected @NotNull Vector2i getSunShadowResolution() {
        return new Vector2i(1024);
    }

    @Override
    protected @NotNull Vector2i getPointLightShadowResolution() {
        return new Vector2i(512);
    }

    @Override
    protected @NotNull Vector2i getSpotLightShadowResolution() {
        return new Vector2i(512);
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
        return !JGemsConfig.SYSTEM.USE_SHADOWS || JGemsConfig.DEBUG.FULL_BRIGHT || !WBench.get().getMapProjectManager().mapProjectSettings.VIEW_SHADOWS || WBenchOpenGLRenderer.isRenderingBackgroundScene();
    }

    @Override
    protected void blurShadows(FBOTexture2DProgram sunShadowFBO) {
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getSunIndirectSSBO() {
        return WBenchResourceManager.localShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getSunPropertiesSSBO() {
        return WBenchResourceManager.localShaderAssets.MainScenePropertiesData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getPointLightIndirectSSBO() {
        return WBenchResourceManager.localShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getPointLightPropertiesSSBO() {
        return WBenchResourceManager.localShaderAssets.MainScenePropertiesData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getSpotLightIndirectSSBO() {
        return WBenchResourceManager.localShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    protected @NotNull ShaderStorageBufferObject getSpotLightPropertiesSSBO() {
        return WBenchResourceManager.localShaderAssets.MainScenePropertiesData;
    }

    @Override
    protected Set<SceneObject> filterSet(Set<? extends SceneObject> modeledSceneObjectSet) {
        return super.filterSet(modeledSceneObjectSet).stream().filter(e -> (e instanceof WBenchCommonObject)).collect(Collectors.toSet());
    }

    @Override
    protected @NotNull Consumer<JGemsShaderManager> getUniformsConsumerSunShadows(SunLightShadow.Cascade cascade, Matrix4f lightProjection) {
        return (shaderManager) -> {
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_VIEW_MATRIX), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
            shaderManager.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.POS_EXP), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
            shaderManager.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.NEG_EXP), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
            shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.ANIMATIONS_MATRIX), WBenchResourceManager.getAnimationsTextureBuffer());
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

    @Override
    public WBenchShadowSceneSnapshotData takeSnapshot() {
        return new WBenchShadowSceneSnapshotData(new Vector3f(this.getSunLightShadow().getCascadeSplits()));
    }

    @Override
    public void fixSnapshot(WBenchShadowSceneSnapshotData wBenchShadowSceneSnapshotData) {
        this.getSunLightShadow().setCascadeSplits(wBenchShadowSceneSnapshotData.cascadeSplits);
    }

    public record WBenchShadowSceneSnapshotData(Vector3f cascadeSplits) implements SnapshotData {
    }
}
