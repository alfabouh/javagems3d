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
import javagems3d.graphics.transformation.JGemsTransformation;
import org.lwjgl.system.MemoryStack;
import javagems3d.graphics.environment.fog.FogManager;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.global.JGemsDebugGlobalConstants;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.managing.JGemsResourceManager;

import java.nio.FloatBuffer;

public class Environment implements IEnvironment {
    public static final int FOG_STRUCT_SIZE = 5;

    private final ShadowScene shadowScene;
    private final LightsScene lightManager;
    private final SkyBox skyBox;
    private final FogManager fogManager;

    public Environment() {
        this.skyBox = new SkyBox(JGemsResourceManager.globalTextureAssets.defaultSkyboxCubeMap);
        this.fogManager = new FogManager(this);
        this.lightManager = new LightsScene(this);
        this.shadowScene = new ShadowScene(this);
    }

    @Override
    public void createEnvironment(SceneWorld sceneWorld) {
        this.getShadowScene().createResources();
    }

    public void destroyEnvironment(SceneWorld sceneWorld) {
        this.getSkyBox().destroySkyBox(sceneWorld);
        this.getShadowScene().destroyResources();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.getLightManager().removeAllLights(stack);
        }
    }

    @Override
    public void updateEnvironment(SceneWorld sceneWorld, ICamera camera) {
        this.getSkyBox().updateSkyBox(sceneWorld, camera);
        this.getShadowScene().renderAllModelsInShadowMap(sceneWorld.getSceneObjects());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.updateLightsUBO(sceneWorld, stack);
            this.updateFogUBO(stack);
        }
    }

    private void updateLightsUBO(SceneWorld world, MemoryStack stack) {
        this.getLightManager().updateBuffers(stack, world, JGemsTransformation.INSTANCE.getCameraViewMatrix());
    }

    private void updateFogUBO(MemoryStack stack) {
        FloatBuffer value1Buffer = stack.mallocFloat(Environment.FOG_STRUCT_SIZE);
        value1Buffer.put(this.getFog().getColor().x * this.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(this.getFog().getColor().y * this.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(this.getFog().getColor().z * this.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(0.0f);
        value1Buffer.put(!JGemsDebugGlobalConstants.FULL_BRIGHT ? this.getFog().getDensity() : 0.0f);
        value1Buffer.flip();
        JGemsOpenGLRenderer.UBOShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Fog, value1Buffer);
    }

    public ShadowScene getShadowScene() {
        return this.shadowScene;
    }

    public LightsScene getLightManager() {
        return this.lightManager;
    }

    public FogManager getFog() {
        return this.fogManager;
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }
}
