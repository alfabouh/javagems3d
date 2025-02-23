package workbench.graphics.scene.world;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.ILighted;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldTicked;
import logger.Log;
import workbench.WBench;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WBenchWorld implements IWorld {
    private ICamera camera;
   // private final JGemsEnvironment environment;
    private final Set<SceneObject> toRenderSet;

    private int ticks;

    public WBenchWorld() {
        this.camera = null;
        this.toRenderSet = new HashSet<>();
    }

    @Override
    public void onWorldStart() {
        WBench.get().getScreen().zeroRenderTick();
        this.ticks = 0;
    }

    @Override
    public void onWorldUpdate() {
        this.ticks += 1;
    }

    @Override
    public void onWorldEnd() {
        this.clearAll();
    }

    public void updateWorldObjects(FrameTicking frameTicking) {
        for (SceneObject sceneObject : this.getSceneObjects()) {
            sceneObject.updateAnimation();
            if (sceneObject instanceof IWorldTicked) {
                IWorldTicked worldTicked = (IWorldTicked) sceneObject;
                worldTicked.onUpdate(this);
            }
        }
    }

    //section WorldClean
    private void clearAll() {
        Iterator<SceneObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject modeledSceneObject = iterator.next();
            if (modeledSceneObject instanceof SceneEntity) {
                SceneEntity abstractSceneEntity = (SceneEntity) modeledSceneObject;
                abstractSceneEntity.onDestroy(this);
            }
            iterator.remove();
        }
    }

    public void removeLight(Light light) {
    //    this.getEnvironment().getLightManager().removeLight(light);
    }

    public void addLight(Light light) {
    //    this.getEnvironment().getLightManager().addLight(light);
    }

    public void addItemLight(ILighted keepLights, Light light) {
        keepLights.addLight(light);
    //    this.getEnvironment().getLightManager().addLight(light);
    }

    public void removeLightFromById(ILighted keepLights, int i) {
        if (keepLights == null) {
            Log.get().error("Couldn't attach light. Invalid entity");
            return;
        }
        keepLights.removeLightById(i);
    }

    public void removeLightFrom(ILighted keepLights, Light light) {
        if (keepLights == null) {
            Log.get().error("Couldn't attach light. Invalid entity");
            return;
        }
        keepLights.removeLight(light);
    }

    public void addObjectInWorld(SceneObject renderObject) {
        this.getSceneObjects().add(renderObject);
    }

    public void removeObjectFromWorld(SceneObject renderObject) {
        if (!this.getSceneObjects().remove(renderObject)) {
            Log.get().warn("Couldn't remove a render object from SceneWorld");
        }
    }

    public void setCamera(ICamera camera) {
        this.camera = camera;
    }

   //public JGemsEnvironment getEnvironment() {
   //    synchronized (this) {
   //        return this.environment;
   //    }
   //}

    public ICamera getCamera() {
        return this.camera;
    }

    public Set<SceneObject> getSceneObjects() {
        return this.toRenderSet;
    }

    public int getTicks() {
        return this.ticks;
    }
}
