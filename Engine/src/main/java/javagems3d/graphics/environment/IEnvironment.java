package javagems3d.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.physics.world.IWorld;

public interface IEnvironment {
    void createEnvironment(OpenGLRenderer openGLRenderer);
    void updateEnvironment(ICamera camera);
    void destroyEnvironment();

    IWorld getWorld();
    IShadowScene getShadowScene();
    ILightScene getLightScene();
    IFogScene getFogScene();
    ISkyBox getSkyBox();
}