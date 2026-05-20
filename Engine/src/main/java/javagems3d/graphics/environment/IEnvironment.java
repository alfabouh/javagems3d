package javagems3d.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.decals.scene.IDecalsScene;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.particles.scene.IParticlesScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.physics.world.IWorld;
import javagems3d.system.global.JGemsConfig;
import org.joml.Vector3f;

public interface IEnvironment {
    void createEnvironment(OpenGLRenderer openGLRenderer);
    void updateEnvironment(ICamera camera);
    void destroyEnvironment();

    default void setEnvironmentDefaults() {
        this.getSkyBox().setSky2DTexture(null);
        this.getSkyBox().setSkyCoveredByFog(true);

        this.getLightScene().getSunLight().setLightColor(new Vector3f(1.0f));
        this.getLightScene().getSunLight().setLightPosition(new Vector3f(1.0f));
        this.getLightScene().getSunLight().setSunBrightness(1.0f);
        this.getShadowScene().getSunLightShadow().setDefaultCascadeSplits();

        this.getLightScene().setBloomEnabled(true);
        this.getLightScene().setHdrExposure(JGemsConfig.SYSTEM.HDR_EXPOSURE_DEFAULT);
        this.getLightScene().setHdrGamma(JGemsConfig.SYSTEM.HDR_GAMMA_DEFAULT);

        this.getLightScene().setSsaoRange(JGemsConfig.SYSTEM.SSAO_RANGE);
        this.getLightScene().setSsaoBias(JGemsConfig.SYSTEM.SSAO_BIAS);
        this.getLightScene().setSsaoRadius(JGemsConfig.SYSTEM.SSAO_RADIUS);

        this.getFogScene().setFogColor(new Vector3f(0.85f));
        this.getFogScene().setFogDensity(0.0f);
    }

    IParticlesScene getParticlesScene();
    IDecalsScene getDecalsScene();
    IWorld getWorld();
    IShadowScene getShadowScene();
    ILightScene getLightScene();
    IFogScene getFogScene();
    ISkyBox getSkyBox();
}