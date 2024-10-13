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

package javagems3d.graphics.opengl.environment;

import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.environment.skybox.SkyBox;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneData;
import org.lwjgl.system.MemoryStack;
import javagems3d.graphics.opengl.environment.fog.Fog;
import javagems3d.graphics.opengl.environment.light.LightManager;
import javagems3d.graphics.opengl.environment.shadow.ShadowManager;
import javagems3d.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.system.resources.manager.JGemsResourceManager;

import java.nio.FloatBuffer;

public class Environment implements IEnvironment {
    public static final int FOG_STRUCT_SIZE = 5;

    private final ShadowManager shadowManager;
    private final LightManager lightManager;
    private final SkyBox skyBox;
    private final Fog fog;

    public Environment() {
        this.skyBox = new SkyBox(JGemsResourceManager.globalTextureAssets.defaultSkyboxCubeMap);
        this.fog = new Fog();
        this.lightManager = new LightManager(this);
        this.shadowManager = new ShadowManager(this);
    }

    @Override
    public void createEnvironment(SceneWorld sceneWorld) {
        this.getShadowManager().createResources();
    }

    public void destroyEnvironment(SceneWorld sceneWorld) {
        this.getSkyBox().destroySkyBox(sceneWorld);
        this.getShadowManager().destroyResources();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.getLightManager().removeAllLights(stack);
        }
    }

    @Override
    public void updateEnvironment(SceneWorld sceneWorld, ICamera camera) {
        this.getSkyBox().updateSkyBox(sceneWorld, camera);
        this.getShadowManager().renderAllModelsInShadowMap(sceneWorld.getModeledSceneEntities());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.updateLightsUBO(sceneWorld, stack);
            this.updateFogUBO(stack);
        }
    }

    private void updateLightsUBO(SceneWorld world, MemoryStack stack) {
        this.getLightManager().updateBuffers(stack, world, JGemsSceneUtils.getMainCameraViewMatrix());
    }

    private void updateFogUBO(MemoryStack stack) {
        FloatBuffer value1Buffer = stack.mallocFloat(Environment.FOG_STRUCT_SIZE);
        value1Buffer.put(this.getFog().getColor().x * this.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(this.getFog().getColor().y * this.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(this.getFog().getColor().z * this.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(0.0f);
        value1Buffer.put(!JGemsDebugGlobalConstants.FULL_BRIGHT ? this.getFog().getDensity() : 0.0f);
        value1Buffer.flip();
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Fog, value1Buffer);
    }

    public ShadowManager getShadowManager() {
        return this.shadowManager;
    }

    public LightManager getLightManager() {
        return this.lightManager;
    }

    public Fog getFog() {
        return this.fog;
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }
}
