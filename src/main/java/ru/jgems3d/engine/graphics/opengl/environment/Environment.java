package ru.jgems3d.engine.graphics.opengl.environment;

import org.joml.Vector3f;
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
    private final Fog worldFog;
    private final Fog waterFog;
    private LightManager lightManager;
    private Sky sky;
    private Fog fog;

    public Environment() {
        this.worldFog = new Fog();
        this.waterFog = new Fog();
        this.setFog(this.getWorldFog());
    }

    public static Environment createEnvironment() {
        return new Environment();
    }

    @Override
    public void init(SceneWorld sceneWorld) {
        this.sky = new Sky(new SkyBox2D(JGemsResourceManager.globalTextureAssets.defaultSkyboxCubeMap), new Vector3f(0.95f, 1.0f, 0.98f), new Vector3f(0.0f, 1.0f, -1.0f), 1.0f);
        this.lightManager = new LightManager(this);
        this.getWaterFog().setDensity(0.5f);
        this.getWaterFog().setColor(new Vector3f(0.05f, 0.1f, 0.6f));
    }

    public Fog getWaterFog() {
        return this.waterFog;
    }

    public Fog getWorldFog() {
        return this.worldFog;
    }

    public Fog getFog() {
        return this.fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.getSky().onUpdate(iWorld);
        this.getLightManager().updateBuffers((SceneWorld) iWorld, JGemsSceneUtils.getMainCameraViewMatrix());
    }

    public LightManager getLightManager() {
        return this.lightManager;
    }

    public Sky getSky() {
        return this.sky;
    }
}
