package ru.jgems3d.engine.graphics.opengl.environment;

import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.environment.shadow.ShadowManager;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.jgems3d.engine.graphics.opengl.environment.fog.Fog;
import ru.jgems3d.engine.graphics.opengl.environment.light.LightManager;
import ru.jgems3d.engine.graphics.opengl.environment.sky.Sky;
import ru.jgems3d.engine.graphics.opengl.environment.sky.skybox.SkyBox2D;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class Environment implements IEnvironment, IWorldTicked {
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
        this.getLightManager().removeAllLights();
    }

    public void setSky(Sky sky) {
        this.sky = sky;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        SceneWorld sceneWorld = (SceneWorld) iWorld;
        this.getSky().onUpdate(sceneWorld);
        this.getShadowManager().renderAllModelsInShadowMap(sceneWorld.getModeledSceneEntities());
        this.getLightManager().updateBuffers(sceneWorld, JGemsSceneUtils.getMainCameraViewMatrix());
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

    public Sky getSky() {
        return this.sky;
    }
}
