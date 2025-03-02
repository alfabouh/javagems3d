package javagems3d.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.scene.LightsScene;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.lwjgl.system.MemoryStack;
import javagems3d.graphics.environment.fog.FogManager;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.managing.JGemsResourceManager;

import java.nio.FloatBuffer;

public class JGemsEnvironment implements IEnvironment {
    private final ShadowScene shadowScene;
    private final LightsScene lightManager;
    private final SkyBox skyBox;
    private final FogManager fogManager;

    private final IWorld world;

    public JGemsEnvironment(IWorld world) {
        this.skyBox = new SkyBox(4.0f, world, JGemsResourceManager.globalTextureAssets.defaultSkyboxCubeMap);
        this.fogManager = new FogManager();
        this.lightManager = new LightsScene(JGemsResourceManager.globalShaderAssets.SunLightData, JGemsResourceManager.globalShaderAssets.PointLightsData,this);
        this.shadowScene = new ShadowScene(this);
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
            this.getLightManager().clearPointLightsBuffer(stack);
        }
    }

    @Override
    public void updateEnvironment(ICamera camera) {
        this.getSkyBox().updateSkyBox(this.getWorld(), camera);
        this.getShadowScene().renderAllModelsInShadowMap(this.getWorld().getSceneObjects());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.updateLightsUBO(this.getWorld(), stack);
            if (this.getFogManager().update) {
                this.updateFogBuffer(JGemsResourceManager.globalShaderAssets.FogData, stack);
                this.getFogManager().update = false;
            }
        }
    }

    private void updateLightsUBO(IWorld world, MemoryStack stack) {
        this.getLightManager().updateBuffers(stack, world, JGemsTransformManager.INSTANCE.getCameraViewMatrix());
    }

    private void updateFogBuffer(ShaderStorageBufferObject shaderStorageBufferObject, MemoryStack stack) {
        FloatBuffer buffer = stack.mallocFloat(JGemsConfig.SYSTEM.FOG_BUFFER_PACK_SIZE);
        buffer.put(this.getFogManager().getColor().x * this.getSkyBox().getSun().getSunBrightness());
        buffer.put(this.getFogManager().getColor().y * this.getSkyBox().getSun().getSunBrightness());
        buffer.put(this.getFogManager().getColor().z * this.getSkyBox().getSun().getSunBrightness());
        buffer.put(!JGemsConfig.DEBUG.FULL_BRIGHT ? this.getFogManager().getDensity() : 0.0f);
        buffer.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(shaderStorageBufferObject, 0L, buffer);
    }

    @Override
    public SceneWorld getWorld() {
        return (SceneWorld) this.world;
    }

    public ShadowScene getShadowScene() {
        return this.shadowScene;
    }

    public LightsScene getLightManager() {
        return this.lightManager;
    }

    public FogManager getFogManager() {
        return this.fogManager;
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }
}