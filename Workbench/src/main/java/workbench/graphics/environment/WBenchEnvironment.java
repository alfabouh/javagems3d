package workbench.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.lights.SpotLight;
import javagems3d.graphics.environment.particles.ParticlesManager;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import org.lwjgl.system.MemoryStack;
import workbench.graphics.environment.components.*;
import workbench.graphics.environment.components.particles.WBenchParticlesManager;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

import java.util.HashMap;

public class WBenchEnvironment implements IEnvironment, ISnapshotCompatible<WBenchEnvironment.WBenchEnvironmentSnapshotData> {
    private final WBenchShadowScene shadowScene;
    private final WBenchLightScene lightManager;
    private final WBenchSkyBox skyBox;
    private final WBenchFogScene fogManager;
    private final WBenchParticlesScene particlesScene;
    private final IWorld world;

    public WBenchEnvironment(IWorld world) {
        this.skyBox = new WBenchSkyBox(world,4.0f, null);
        this.fogManager = new WBenchFogScene();
        this.lightManager = new WBenchLightScene(WBenchResourceManager.localShaderAssets.SunLightData, WBenchResourceManager.localShaderAssets.PointLightsData, WBenchResourceManager.localShaderAssets.SpotLightsData,this);
        this.shadowScene = new WBenchShadowScene(this);
        this.particlesScene = new WBenchParticlesScene(this, new WBenchParticlesManager(this));
        this.world = world;
    }

    @Override
    public void createEnvironment(OpenGLRenderer openGLRenderer) {
        this.getShadowScene().createResources(openGLRenderer);
        this.getParticlesScene().createResources(openGLRenderer);
    }

    public void destroyEnvironment() {
        this.getShadowScene().destroyResources();
        this.getParticlesScene().destroyResources();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.getLightScene().clearPointLightsBuffer(stack, this.getLightScene().getPointLightsBuffer());
            this.getLightScene().clearSpotLightsBuffer(stack, this.getLightScene().getSpotLightsBuffer());
        }
    }

    @Override
    public void updateEnvironment(ICamera camera) {
        this.getSkyBox().updateSkyBox(this.getWorld(), camera);
        this.getShadowScene().renderAllModelsInShadowMap(this.getWorld(), this.getWorld().getSceneObjects());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.updateLightsBuffer(this.getWorld(), this.getShadowScene().getPointLightIdsHashMap(), this.getShadowScene().getSpotLightIdsHashMap(), stack);
            this.getFogScene().updateFogBuffer(WBenchResourceManager.localShaderAssets.FogData, this.getSkyBox(), this.getLightScene(), stack);
        }
        this.getParticlesScene().update(this.getWorld());
    }

    protected void updateLightsBuffer(IWorld world, HashMap<PointLight, Integer> pointLightIdxHashMap, HashMap<SpotLight, Integer> spotLightIntegerHashMap, MemoryStack stack) {
        this.getLightScene().updateBuffers(stack, pointLightIdxHashMap, spotLightIntegerHashMap, world, JGemsTransformManager.INSTANCE.getCameraViewMatrix());
    }

    @Override
    public WBenchWorld getWorld() {
        return (WBenchWorld) this.world;
    }

    public WBenchShadowScene getShadowScene() {
        return this.shadowScene;
    }

    public WBenchLightScene getLightScene() {
        return this.lightManager;
    }

    public WBenchFogScene getFogScene() {
        return this.fogManager;
    }

    public WBenchSkyBox getSkyBox() {
        return this.skyBox;
    }

    @Override
    public WBenchParticlesScene getParticlesScene() {
        return this.particlesScene;
    }

    @Override
    public WBenchEnvironmentSnapshotData takeSnapshot() {
        return new WBenchEnvironmentSnapshotData(this.getShadowScene().takeSnapshot(), this.getSkyBox().takeSnapshot(), this.getFogScene().takeSnapshot(), this.getLightScene().takeSnapshot());
    }

    @Override
    public void fixSnapshot(WBenchEnvironmentSnapshotData wBenchEnvironmentSnapshotData) {
        this.getShadowScene().fixSnapshot(wBenchEnvironmentSnapshotData.shadowScene);
        this.getSkyBox().fixSnapshot(wBenchEnvironmentSnapshotData.skyBox);
        this.getFogScene().fixSnapshot(wBenchEnvironmentSnapshotData.fogManager);
        this.getLightScene().fixSnapshot(wBenchEnvironmentSnapshotData.lightSceneSnapshotData);
    }

    public record WBenchEnvironmentSnapshotData(WBenchShadowScene.WBenchShadowSceneSnapshotData shadowScene,
                                                WBenchSkyBox.WBenchSkyBoxSnapshotData skyBox,
                                                WBenchFogScene.WBenchFogSceneSnapshotData fogManager,
                                                WBenchLightScene.WBenchLightSceneSnapshotData lightSceneSnapshotData) implements SnapshotData {
    }
}