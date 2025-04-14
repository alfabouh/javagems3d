package workbench.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import org.lwjgl.system.MemoryStack;
import workbench.graphics.environment.components.WBenchFogScene;
import workbench.graphics.environment.components.WBenchLightScene;
import workbench.graphics.environment.components.WBenchShadowScene;
import workbench.graphics.environment.components.WBenchSkyBox;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

public class WBenchEnvironment implements IEnvironment {
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
        this.getSkyBox().destroySkyBox(this.getWorld());
        this.getShadowScene().destroyResources();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.getLightScene().clearPointLightsBuffer(stack);
        }
    }

    @Override
    public void updateEnvironment(ICamera camera) {
        this.getSkyBox().updateSkyBox(this.getWorld(), camera);
        this.getShadowScene().renderAllModelsInShadowMap(this.getWorld().getSceneObjects());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.updateLightsUBO(this.getWorld(), stack);
            this.getFogManager().updateFogBuffer(WBenchResourceManager.localShaderAssets.FogData, this.getSkyBox(), stack);
        }
    }

    protected void updateLightsUBO(IWorld world, MemoryStack stack) {
        this.getLightScene().updateBuffers(stack, world, JGemsTransformManager.INSTANCE.getCameraViewMatrix());
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

    public WBenchFogScene getFogManager() {
        return this.fogManager;
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }
}