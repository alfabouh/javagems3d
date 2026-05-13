package workbench.graphics.environment.components;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.scene.LightScene;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.joml.Vector3f;
import workbench.graphics.environment.components.light.WBenchSunLight;

public class WBenchLightScene extends LightScene implements ISnapshotCompatible<WBenchLightScene.WBenchLightSceneSnapshotData>  {
    public WBenchLightScene(ShaderStorageBufferObject sunBuffer, ShaderStorageBufferObject pointLightsBuffer, ShaderStorageBufferObject spotLightsBuffer, IEnvironment environment) {
        super(sunBuffer, pointLightsBuffer, spotLightsBuffer, environment);
    }

    @Override
    protected void initSun() {
        this.sunLight = new WBenchSunLight(new Vector3f(1.0f), new Vector3f(1.0f), 1.0f);

    }

    @Override
    public int getMaxPointLights() {
        return JGemsConfig.SYSTEM.MAX_POINT_LIGHTS;
    }

    @Override
    public int getMaxSpotLights() {
        return JGemsConfig.SYSTEM.MAX_SPOT_LIGHTS;
    }

    @Override
    public WBenchLightScene.WBenchLightSceneSnapshotData takeSnapshot() {
        return new WBenchLightScene.WBenchLightSceneSnapshotData(((WBenchSunLight) (this.getSunLight())).takeSnapshot());
    }

    @Override
    public void fixSnapshot(WBenchLightScene.WBenchLightSceneSnapshotData wBenchSkyBoxSnapshotData) {
        ((WBenchSunLight) this.getSunLight()).fixSnapshot(wBenchSkyBoxSnapshotData.sunLightSnapshotData);
    }

    public record WBenchLightSceneSnapshotData(WBenchSunLight.WBenchSunLightSnapshotData sunLightSnapshotData) implements SnapshotData {
    }
}
