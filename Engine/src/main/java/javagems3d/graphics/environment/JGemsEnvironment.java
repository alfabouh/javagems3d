package javagems3d.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.fog.JGemsFogScene;
import javagems3d.graphics.environment.lights.scene.JGemsLightScene;
import javagems3d.graphics.environment.shadows.scene.JGemsShadowScene;
import javagems3d.graphics.environment.skybox.JGemsSkyBox;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import org.lwjgl.system.MemoryStack;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class JGemsEnvironment implements IEnvironment {
    private final JGemsShadowScene shadowScene;
    private final JGemsLightScene lightManager;
    private final JGemsSkyBox skyBox;
    private final JGemsFogScene fogManager;
    private final IWorld world;

    public JGemsEnvironment(IWorld world) {
        this.skyBox = new JGemsSkyBox(world, 4.0f, null);
        this.fogManager = new JGemsFogScene();
        this.lightManager = new JGemsLightScene(JGemsResourceManager.globalShaderAssets.SunLightData, JGemsResourceManager.globalShaderAssets.PointLightsData,this);
        this.shadowScene = new JGemsShadowScene(this);
        this.world = world;
    }

    @Override
    public void createEnvironment(OpenGLRenderer openGLRenderer) {
        this.getShadowScene().createResources(openGLRenderer);
    }

    public void destroyEnvironment() {
        this.getShadowScene().destroyResources();
        this.clearPointLightsBuffer();
    }

    public void clearPointLightsBuffer() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.getLightScene().clearPointLightsBuffer(stack);
            this.getLightScene().getPointLights().clear();
        }
    }

    @Override
    public void updateEnvironment(ICamera camera) {
        this.getSkyBox().getSun().onUpdateWithEvent(this.getWorld());
        this.getSkyBox().updateSkyBox(this.getWorld(), camera);
        this.getShadowScene().renderAllModelsInShadowMap(this.getWorld().getSceneObjects());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.updateLightsUBO(this.getWorld(), stack);
            this.getFogScene().updateFogBuffer(JGemsResourceManager.globalShaderAssets.FogData, this.getSkyBox(), stack);
        }
    }

    protected void updateLightsUBO(IWorld world, MemoryStack stack) {
        this.getLightScene().updateBuffers(stack, world, JGemsTransformManager.INSTANCE.getCameraViewMatrix());
    }

    @Override
    public SceneWorld getWorld() {
        return (SceneWorld) this.world;
    }

    public JGemsShadowScene getShadowScene() {
        return this.shadowScene;
    }

    public JGemsLightScene getLightScene() {
        return this.lightManager;
    }

    public JGemsFogScene getFogScene() {
        return this.fogManager;
    }

    public JGemsSkyBox getSkyBox() {
        return this.skyBox;
    }
}