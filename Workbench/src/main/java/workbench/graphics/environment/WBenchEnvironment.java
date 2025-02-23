package workbench.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.fog.IFogManager;
import javagems3d.graphics.environment.lights.scene.ILightsScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.physics.world.IWorld;

public class WBenchEnvironment implements IEnvironment {
    @Override
    public void createEnvironment(OpenGLRenderer openGLRenderer) {

    }

    @Override
    public void updateEnvironment(ICamera camera) {

    }

    @Override
    public void destroyEnvironment() {

    }

    @Override
    public IWorld getWorld() {
        return null;
    }

    @Override
    public IShadowScene getShadowScene() {
        return null;
    }

    @Override
    public ILightsScene getLightManager() {
        return null;
    }

    @Override
    public IFogManager getFogManager() {
        return null;
    }

    @Override
    public ISkyBox getSkyBox() {
        return null;
    }
}
