package workbench.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import workbench.graphics.environment.components.WBenchFogScene;
import workbench.graphics.environment.components.WBenchLightScene;
import workbench.graphics.environment.components.WBenchShadowScene;
import workbench.graphics.environment.components.WBenchSkyBox;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

import java.util.HashMap;

public class WBenchEnvironment implements IEnvironment, ISnapshotCompatible<WBenchEnvironment.WBenchEnvironmentSnapshotData> {
    private final WBenchShadowScene shadowScene;
    private final WBenchLightScene lightManager;
    private final WBenchSkyBox skyBox;
    private final WBenchFogScene fogManager;
    private final IWorld world;

    public WBenchEnvironment(IWorld world) {
        this.skyBox = new WBenchSkyBox(world,4.0f, null);
        this.fogManager = new WBenchFogScene();
        this.lightManager = new WBenchLightScene(WBenchResourceManager.localShaderAssets.SunLightData, WBenchResourceManager.localShaderAssets.PointLightsData,this);
        this.shadowScene = new WBenchShadowScene(this);
        this.world = world;
    }

    @Override
    public void createEnvironment(OpenGLRenderer openGLRenderer) {
        this.getShadowScene().createResources(openGLRenderer);
    }

    public void destroyEnvironment() {
        this.getShadowScene().destroyResources();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.getLightScene().clearPointLightsBuffer(stack);
        }
    }

    @Override
    public void updateEnvironment(ICamera camera) {
        final HashMap<PointLight, Integer> lightIdxHashMap = this.getShadowScene().getSortedPointLightMapReadyToBind(camera.getCamPosition(), this.getLightScene().getPointLights());
        this.getSkyBox().updateSkyBox(this.getWorld(), camera);
        this.getShadowScene().renderAllModelsInShadowMap(this.getWorld().getSceneObjects());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.updateLightsUBO(this.getWorld(), lightIdxHashMap, stack);
            this.getFogScene().updateFogBuffer(WBenchResourceManager.localShaderAssets.FogData, this.getSkyBox(), stack);
        }
    }

    protected void updateLightsUBO(IWorld world, HashMap<PointLight, Integer> lightIdxHashMap, MemoryStack stack) {
        this.getLightScene().updateBuffers(stack, lightIdxHashMap, world, JGemsTransformManager.INSTANCE.getCameraViewMatrix());
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
    public WBenchEnvironmentSnapshotData takeSnapshot() {
        return new WBenchEnvironmentSnapshotData(this.getShadowScene().takeSnapshot(), this.getSkyBox().takeSnapshot(), this.getFogScene().takeSnapshot());
    }

    @Override
    public void fixSnapshot(WBenchEnvironmentSnapshotData wBenchEnvironmentSnapshotData) {
        this.getShadowScene().fixSnapshot(wBenchEnvironmentSnapshotData.shadowScene);
        this.getSkyBox().fixSnapshot(wBenchEnvironmentSnapshotData.skyBox);
        this.getFogScene().fixSnapshot(wBenchEnvironmentSnapshotData.fogManager);
    }

    public record WBenchEnvironmentSnapshotData(WBenchShadowScene.WBenchShadowSceneSnapshotData shadowScene,
                                                WBenchSkyBox.WBenchSkyBoxSnapshotData skyBox,
                                                WBenchFogScene.WBenchFogSceneSnapshotData fogManager) implements SnapshotData {
    }
}