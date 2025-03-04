package javagems3d.graphics.environment.lights.scene;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;

public class JGemsLightScene extends LightScene {
    public JGemsLightScene(ShaderStorageBufferObject sunBuffer, ShaderStorageBufferObject pointLightsBuffer, IEnvironment environment) {
        super(sunBuffer, pointLightsBuffer, environment);
    }

    @Override
    public int getMaxPointLights() {
        return JGemsConfig.SYSTEM.MAX_POINT_LIGHTS;
    }
}
