package workbench.graphics.environment.components;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.scene.LightScene;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;

public class WBenchLightScene extends LightScene {
    public WBenchLightScene(ShaderStorageBufferObject sunBuffer, ShaderStorageBufferObject pointLightsBuffer, IEnvironment environment) {
        super(sunBuffer, pointLightsBuffer, environment);
    }

    @Override
    public int getMaxPointLights() {
        return JGemsConfig.SYSTEM.MAX_POINT_LIGHTS;
    }
}
