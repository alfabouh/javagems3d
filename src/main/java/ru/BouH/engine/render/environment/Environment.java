package ru.BouH.engine.render.environment;

import org.joml.Vector3f;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.light.LightManager;
import ru.BouH.engine.render.environment.sky.Sky;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class Environment implements IEnvironment, IWorldDynamic {
    private LightManager lightManager;
    private Sky sky;

    public static Environment createEnvironment() {
        return new Environment();
    }

    @Override
    public void init(SceneWorld sceneWorld) {
        this.sky = new Sky(ResourceManager.renderAssets.skyboxCubeMap, new Vector3f(1.0f, 1.0f, 0.2f));
        this.lightManager = new LightManager(this);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.getSky().onUpdate(iWorld);
        this.getLightManager().updateBuffers((SceneWorld) iWorld, RenderManager.instance.getViewMatrix());
    }

    public LightManager getLightManager() {
        return this.lightManager;
    }

    public Sky getSky() {
        return this.sky;
    }
}
