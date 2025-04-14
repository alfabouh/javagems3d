package workbench.graphics.scene.world;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.ILightAttached;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.ILighted;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
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
    private final Queue<Integer> freeIds;
    private ICamera camera;
    private WBenchEnvironment environment;
    private final Set<WBenchObject> toRenderSet;
    private final Map<Integer, WBenchObject> idMap;
    private int ticks;

    public WBenchWorld() {
        this.camera = null;
        this.toRenderSet = new HashSet<>();
        this.idMap = new HashMap<>();
        this.freeIds = new ArrayDeque<>();
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
        Iterator<WBenchObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            WBenchObject sceneObject = iterator.next();
            if (sceneObject.isDead()) {
                Log.get().info("Removed object: " + sceneObject);
                int id = sceneObject.getId();
                if (this.getIdMap().remove(id) != null) {
                    this.getFreeIds().add(id);
                } else {
                    Log.get().warn("Couldn't remove object with id: " + id);
                }
                sceneObject.onDestroy(this);
                iterator.remove();
                continue;
            }
            sceneObject.updateAnimation();
            ((IWorldTicked) sceneObject).onUpdate(this);
        }
    }

    private void clearAll() {
        this.getFreeIds().clear();
        Iterator<WBenchObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject sceneObject = iterator.next();
            sceneObject.onDestroy(this);
            iterator.remove();
        }
    }

    public void removeLight(Light light, @Nullable ILighted lighted) {
        this.getEnvironment().getLightScene().removeLight(light);
        if (lighted != null) {
            lighted.removeLightAttachment((ILightAttached) light);
        }
    }

    public void addLight(Light light, @Nullable ILighted lighted) {
        this.getEnvironment().getLightScene().addLight(light);
        if (lighted != null) {
            lighted.addLightAttachment((ILightAttached) light);
        }
    }

    public void addObject(SceneObject renderObject) {
        WBenchObject wBenchObject = (WBenchObject) renderObject;
        int id = wBenchObject.getId();
        if (id < 0) {
            if (!this.getFreeIds().isEmpty()) {
                Integer freeId = this.getFreeIds().poll();
                if (freeId != null) {
                    id = freeId;
                }
            } else {
                id = this.getSceneObjects().size();
            }
            wBenchObject.setId(id);
        }
        this.getIdMap().put(id, wBenchObject);

        this.getSceneObjects().add(wBenchObject);
        wBenchObject.onSpawn(this);
        Log.get().info("Created object: " + wBenchObject);
    }

    public void removeObject(SceneObject renderObject) {
        WBenchObject wBenchObject = (WBenchObject) renderObject;
        int id = wBenchObject.getId();
        if (this.getIdMap().remove(id) != null) {
            this.getFreeIds().add(id);
        } else {
            Log.get().warn("Couldn't remove object with id: " + id);
        }

        this.getSceneObjects().remove(wBenchObject);
        wBenchObject.onDestroy(this);
        Log.get().info("Removed object: " + wBenchObject);
    }

    public static void calcFreeIds(Queue<Integer> free, Set<? extends SceneProp> wBenchObjects) {
        free.clear();

        BitSet usedIds = new BitSet();
        int maxId = -1;

        for (SceneProp sceneObject : wBenchObjects) {
            WBenchObject wBenchObject = (WBenchObject) sceneObject;
            if (sceneObject != null) {
                int id = wBenchObject.getId();
                if (id >= 0) {
                    usedIds.set(id);
                    maxId = Math.max(maxId, id);
                }
            }
        }

        for (int i = 0; i <= maxId; i++) {
            if (!usedIds.get(i)) {
                free.add(i);
            }
        }
    }

    public Queue<Integer> getFreeIds() {
        return this.freeIds;
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

    public Set<WBenchObject> getSceneObjects() {
        return this.toRenderSet;
    }

    public int getTicks() {
        return this.ticks;
    }
}
