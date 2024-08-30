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

package javagems3d.engine.graphics.opengl.environment;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import javagems3d.engine.graphics.opengl.environment.fog.Fog;
import javagems3d.engine.graphics.opengl.environment.light.LightManager;
import javagems3d.engine.graphics.opengl.environment.shadow.ShadowManager;
import javagems3d.engine.graphics.opengl.environment.sky.Sky;
import javagems3d.engine.graphics.opengl.environment.sky.skybox.SkyBox2D;
import javagems3d.engine.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.engine.graphics.opengl.world.SceneWorld;
import javagems3d.engine.physics.world.IWorld;
import javagems3d.engine.physics.world.basic.IWorldTicked;
import javagems3d.engine.system.resources.manager.JGemsResourceManager;

import java.nio.FloatBuffer;

public class Environment implements IEnvironment, IWorldTicked {
    public static final int FG_STRUCT_SIZE = 5;

    private final ShadowManager shadowManager;
    private final LightManager lightManager;
    private Sky sky;
    private Fog fog;

    public Environment(SceneWorld sceneWorld) {
        this.sky = new Sky(new SkyBox2D(JGemsResourceManager.globalTextureAssets.defaultSkyboxCubeMap), new Vector3f(0.95f, 1.0f, 0.98f), new Vector3f(0.0f, 1.0f, -1.0f), 1.0f);
        this.fog = new Fog();
        this.lightManager = new LightManager(this);
        this.shadowManager = new ShadowManager(sceneWorld);
    }

    @Override
    public void init(SceneWorld sceneWorld) {
        this.getShadowManager().createResources();
    }

    @Override
    public void destroy(SceneWorld sceneWorld) {
        this.getShadowManager().destroyResources();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.getLightManager().removeAllLights(stack);
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        SceneWorld sceneWorld = (SceneWorld) iWorld;
        this.getSky().onUpdate(sceneWorld);
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
        FloatBuffer value1Buffer = stack.mallocFloat(Environment.FG_STRUCT_SIZE);
        value1Buffer.put(this.getFog().getColor().x * this.getSky().getSunBrightness());
        value1Buffer.put(this.getFog().getColor().y * this.getSky().getSunBrightness());
        value1Buffer.put(this.getFog().getColor().z * this.getSky().getSunBrightness());
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

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public Sky getSky() {
        return this.sky;
    }

    public void setSky(Sky sky) {
        this.sky = sky;
    }
}
