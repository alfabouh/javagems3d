package javagems3d.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.physics.world.IWorld;
import org.joml.Vector3f;

public interface IEnvironment {
    void createEnvironment(OpenGLRenderer openGLRenderer);
    void updateEnvironment(ICamera camera);
    void destroyEnvironment();

    default void setEnvironmentDefaults() {
        this.getSkyBox().setSky2DTexture(null);
        this.getSkyBox().setSkyCoveredByFog(true);

        this.getSkyBox().getSun().setLightColor(new Vector3f(1.0f));
        this.getSkyBox().getSun().setLightPosition(new Vector3f(1.0f));
        this.getSkyBox().getSun().setSunBrightness(1.0f);

        this.getFogScene().setFogColor(new Vector3f(0.85f));
        this.getFogScene().setFogDensity(0.0f);
    }

    IWorld getWorld();
    IShadowScene getShadowScene();
    ILightScene getLightScene();
    IFogScene getFogScene();
    ISkyBox getSkyBox();
}