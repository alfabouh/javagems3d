package javagems3d.graphics.environment;

import api.events.EventBus;
import api.events.EventLauncher;
import api.scripting.coding.env.internal.game.init.events.rendering.environment.JSCreateRenderEnvironmentEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.environment.JSDestroyRenderEnvironmentEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.environment.JSUpdateRenderEnvironmentEvent;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCamera;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;
import api.scripting.JavaToJsAPI;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.decals.scene.IDecalsScene;
import javagems3d.graphics.environment.decals.scene.JGemsDecalsScene;
import javagems3d.graphics.environment.fog.JGemsFogScene;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.lights.SpotLight;
import javagems3d.graphics.environment.lights.scene.JGemsLightScene;
import javagems3d.graphics.environment.particles.JGemsParticlesManager;
import javagems3d.graphics.environment.particles.ParticlesManager;
import javagems3d.graphics.environment.particles.scene.JGemsParticlesScene;
import javagems3d.graphics.environment.shadows.scene.JGemsShadowScene;
import javagems3d.graphics.environment.skybox.JGemsSkyBox;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.collections.Pair;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.managing.JGemsResourceManager;

import java.util.HashMap;

public class JGemsEnvironment implements IEnvironment {
    private final JGemsShadowScene shadowScene;
    private final JGemsLightScene lightManager;
    private final JGemsSkyBox skyBox;
    private final JGemsFogScene fogManager;
    private final JGemsParticlesScene particlesScene;
    private final JGemsDecalsScene decalsScene;
    private final IWorld world;

    public JGemsEnvironment(IWorld world) {
        this.skyBox = new JGemsSkyBox(world, 4.0f, null);
        this.fogManager = new JGemsFogScene();
        this.lightManager = new JGemsLightScene(JGemsResourceManager.globalShaderAssets.SunLightData, JGemsResourceManager.globalShaderAssets.PointLightsData, JGemsResourceManager.globalShaderAssets.SpotLightsData,this);
        this.shadowScene = new JGemsShadowScene(this);
        this.particlesScene = new JGemsParticlesScene(this, new JGemsParticlesManager(this));
        this.decalsScene = new JGemsDecalsScene(this);
        this.world = world;
    }

    @Override
    public void setEnvironmentDefaults() {
        IEnvironment.super.setEnvironmentDefaults();
        this.getShadowScene().setSunShadowMapsBasicResolution(JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES);
        this.getShadowScene().setPointLightShadowMapsBasicResolution(JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES);
    }

    @Override
    public void createEnvironment(OpenGLRenderer openGLRenderer) {
        EventLauncher.pushEvent(new EventBus.CreateRenderEnvironmentEvent(this, openGLRenderer), new Pair<>(new JSCreateRenderEnvironmentEvent(new JSEnvironment(this), new JSOpenGLRenderer(openGLRenderer)), JavaToJsAPI.Target.Game));
        this.getShadowScene().createResources(openGLRenderer);
        this.getParticlesScene().createResources(openGLRenderer);
    }

    public void destroyEnvironment() {
        EventLauncher.pushEvent(new EventBus.DestroyRenderEnvironmentEvent(this), new Pair<>(new JSDestroyRenderEnvironmentEvent(new JSEnvironment(this)), JavaToJsAPI.Target.Game));
        this.getShadowScene().destroyResources();
        this.getParticlesScene().destroyResources();
    }

    public void clearPointLightsBuffer() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.getLightScene().clearPointLightsBuffer(stack, this.getLightScene().getPointLightsBuffer());
            this.getLightScene().clearSpotLightsBuffer(stack, this.getLightScene().getSpotLightsBuffer());
            this.getLightScene().getPointLights().clear();
        }
    }

    @Override
    public void updateEnvironment(ICamera camera) {
        EventLauncher.pushEvent(new EventBus.UpdateRenderEnvironmentEvent(this, camera, EventBus.Run.PRE), new Pair<>(new JSUpdateRenderEnvironmentEvent(new JSEnvironment(this), new JSCamera(camera), JSEventRun.PRE), JavaToJsAPI.Target.Game));
        this.getLightScene().getSunLight().onUpdate(this.getWorld());
        this.getSkyBox().updateSkyBox(this.getWorld(), camera);
        this.getShadowScene().renderAllModelsInShadowMap(this.getWorld(), this.getWorld().getSceneObjects());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.updateLightsBuffer(this.getWorld(), this.getShadowScene().getPointLightIdsHashMap(), this.getShadowScene().getSpotLightIdsHashMap(), stack);
            this.getFogScene().updateFogBuffer(JGemsResourceManager.globalShaderAssets.FogData, this.getSkyBox(), this.getLightScene(), stack);
        }
        this.getParticlesScene().update(this.getWorld());
        this.getDecalsScene().update(this.getWorld());
        EventLauncher.pushEvent(new EventBus.UpdateRenderEnvironmentEvent(this, camera, EventBus.Run.POST), new Pair<>(new JSUpdateRenderEnvironmentEvent(new JSEnvironment(this), new JSCamera(camera), JSEventRun.POST), JavaToJsAPI.Target.Game));
    }

    protected void updateLightsBuffer(IWorld world, HashMap<PointLight, Integer> pointLightIdxHashMap, HashMap<SpotLight, Integer> spotLightIntegerHashMap, MemoryStack stack) {
        this.getLightScene().updateBuffers(stack, pointLightIdxHashMap, spotLightIntegerHashMap, world, JGemsTransformManager.INSTANCE.getCameraViewMatrix());
    }

    @Override
    public SceneWorld getWorld() {
        return (SceneWorld) this.world;
    }

    @Override
    public JGemsParticlesScene getParticlesScene() {
        return this.particlesScene;
    }

    @Override
    public JGemsDecalsScene getDecalsScene() {
        return this.decalsScene;
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