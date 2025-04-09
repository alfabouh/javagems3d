package workbench.graphics.scene.world;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.ILighted;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.physics.world.basic.IWorldTicked;
import logger.Log;
import org.jetbrains.annotations.Nullable;
import workbench.WBench;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.objects.WBenchObject;

import java.util.*;

public class WBenchWorld implements IRenderWorld {
    private ICamera camera;
    private WBenchEnvironment environment;
    private final Set<SceneObject> toRenderSet;
    private final Map<Integer, WBenchObject> idMap;
    private int ticks;

    public WBenchWorld() {
        this.camera = null;
        this.toRenderSet = new HashSet<>();
        this.idMap = new HashMap<>();
    }

    @Override
    public void onWorldStart() {
        this.environment = new WBenchEnvironment(this);
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
        this.environment = null;
    }

    public void updateWorldObjects(FrameTicking frameTicking) {
        Iterator<SceneObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject sceneObject = iterator.next();
            if (sceneObject.isDead()) {
                Log.get().info("Removed object: " + sceneObject);
                iterator.remove();
                sceneObject.onDestroy(this);
                continue;
            }
            sceneObject.updateAnimation();
            IWorldTicked worldTicked = (IWorldTicked) sceneObject;
            worldTicked.onUpdate(this);
        }
    }

    private void clearAll() {
        Iterator<SceneObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject sceneObject = iterator.next();
            iterator.remove();
            sceneObject.onDestroy(this);
        }
    }

    public void removeLight(Light light) {
        this.getEnvironment().getLightScene().removeLight(light);
        Log.get().info("Removed light: " + light);
    }

    public void addLight(Light light) {
        this.getEnvironment().getLightScene().addLight(light);
        Log.get().info("Created new light: " + light);
    }

    public void addItemLight(ILighted keepLights, Light light) {
        keepLights.addLight(light);
        this.getEnvironment().getLightScene().addLight(light);
        Log.get().info("Added light: " + light + " to: " + keepLights);
    }

    public void removeItemLight(ILighted keepLights, Light light) {
        if (keepLights == null) {
            Log.get().error("Couldn't attach light. Invalid entity");
            return;
        }
        keepLights.removeLight(light);
        Log.get().info("Removed light: " + light + " from: " + keepLights);
    }

    public void addObjectInWorld(SceneObject renderObject) {
        if (renderObject instanceof WBenchObject) {
            WBenchObject wBenchObject = (WBenchObject) renderObject;
            int id = wBenchObject.getId() < 0 ? this.getSceneObjects().size() : wBenchObject.getId();
            wBenchObject.setId(id);
            this.getIdMap().put(id, wBenchObject);
        }
        this.getSceneObjects().add(renderObject);
        renderObject.onSpawn(this);
        Log.get().info("Created object: " + renderObject);
    }

    public void removeObjectFromWorld(SceneObject renderObject) {
        if (renderObject instanceof WBenchObject) {
            WBenchObject wBenchObject = (WBenchObject) renderObject;
            int id = wBenchObject.getId();
            if (!this.getIdMap().remove(id, wBenchObject)) {
                Log.get().warn("Couldn't remove object with id: " + id);
            }
        }
        this.getSceneObjects().remove(renderObject);
        renderObject.onDestroy(this);
        Log.get().info("Removed object: " + renderObject);
    }

    public void setCamera(@Nullable ICamera camera) {
        this.camera = camera;
    }

    public Map<Integer, WBenchObject> getIdMap() {
        return this.idMap;
    }

    public WBenchEnvironment getEnvironment() {
        return this.environment;
    }

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
