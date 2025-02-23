/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.scene.LightsScene;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import org.lwjgl.system.MemoryStack;
import javagems3d.graphics.environment.fog.FogManager;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.global.JGemsDebugGlobalConstants;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.managing.JGemsResourceManager;

import java.nio.FloatBuffer;

public class JGemsEnvironment implements IEnvironment {
    public static final int FOG_STRUCT_SIZE = 5;

    private final ShadowScene shadowScene;
    private final LightsScene lightManager;
    private final SkyBox skyBox;
    private final FogManager fogManager;

    private final IWorld world;

    public JGemsEnvironment(IWorld world) {
        this.skyBox = new SkyBox(4.0f, world, JGemsResourceManager.globalTextureAssets.defaultSkyboxCubeMap);
        this.fogManager = new FogManager();
        this.lightManager = new LightsScene(this);
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
            this.getLightManager().removeAllLights(stack);
        }
    }

    @Override
    public void updateEnvironment(ICamera camera) {
        this.getSkyBox().updateSkyBox(this.getWorld(), camera);
        this.getShadowScene().renderAllModelsInShadowMap(this.getWorld().getSceneObjects());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.updateLightsUBO(this.getWorld(), stack);
            this.updateFogUBO(stack);
        }
    }

    private void updateLightsUBO(IWorld world, MemoryStack stack) {
        this.getLightManager().updateBuffers(stack, world, JGemsTransformManager.INSTANCE.getCameraViewMatrix());
    }

    private void updateFogUBO(MemoryStack stack) {
        FloatBuffer value1Buffer = stack.mallocFloat(JGemsEnvironment.FOG_STRUCT_SIZE);
        value1Buffer.put(this.getFogManager().getColor().x * this.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(this.getFogManager().getColor().y * this.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(this.getFogManager().getColor().z * this.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(0.0f);
        value1Buffer.put(!JGemsDebugGlobalConstants.FULL_BRIGHT ? this.getFogManager().getDensity() : 0.0f);
        value1Buffer.flip();
        JGemsOpenGLRenderer.UBOShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Fog, value1Buffer);
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