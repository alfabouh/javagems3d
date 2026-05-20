package workbench.graphics.scene.world;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.ILightAttachable;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.screen.timer.TimerPool;
import javagems3d.graphics.world.IRenderWorld;
import logger.Log;
import org.jetbrains.annotations.Nullable;
import workbench.WBench;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.objects.WBenchObject;

import java.util.*;

public class WBenchWorld implements IRenderWorld, ISnapshotCompatible<WBenchWorld.WBenchWorldSnapshotData> {
    private final Queue<Integer> freeIds;
    private ICamera camera;
    private WBenchEnvironment environment;
    private Set<? extends SceneObject> endFrameVisibleObjects;
    private final Set<WBenchObject<?>> toRenderSet;
    private final Map<Integer, WBenchObject<?>> idMap;
    private int ticks;

    public WBenchWorld() {
        this.camera = null;
        this.endFrameVisibleObjects = new HashSet<>();
        this.toRenderSet = new HashSet<>();
        this.idMap = new HashMap<>();
        this.freeIds = new ArrayDeque<>();
    }

    @Override
    public void onWorldStart() {
        this.environment = new WBenchEnvironment(this);
        this.getEnvironment().getSkyBox().createSkyBox(this);
        WBench.get().getScreen().zeroRenderTick();
        this.ticks = 0;
    }

    @Override
    public void onWorldUpdate() {
        this.ticks += 1;
    }

    @Override
    public void onWorldEnd() {
        if (this.getEnvironment() != null) {
            this.getEnvironment().getSkyBox().destroySkyBox(this);
            this.getEnvironment().getParticlesScene().getParticlesManager().clear();
            this.getEnvironment().getDecalsScene().clear();
        }
        this.clearAll();
        this.environment = null;
    }

    public void updateWorldObjects(FrameTicking frameTicking) {
        Iterator<WBenchObject<?>> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            WBenchObject<?> sceneObject = iterator.next();
            if (sceneObject.isDead()) {
                Log.get().info("Removed object: " + sceneObject);
                int id = sceneObject.getListID();
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
            sceneObject.onUpdate(this);
        }
    }

    public void clearAll() {
        this.getFreeIds().clear();
        Iterator<WBenchObject<?>> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject sceneObject = iterator.next();
            sceneObject.onDestroy(this);
            iterator.remove();
        }
    }

    public void removeLight(Light light) {
        this.getEnvironment().getLightScene().removeLight(light);
        ILightAttachable l = (ILightAttachable) light;
        if (l.getAttachedTo() != null) {
            l.getAttachedTo().removeLightAttachment(l);
        }
    }

    public void addLight(Light light, @Nullable IObjectWithLights lighted) {
        this.getEnvironment().getLightScene().addLight(light);
        if (lighted != null) {
            lighted.addLightAttachment((ILightAttachable) light);
        }
    }

    public void addObject(SceneObject renderObject) {
        WBenchObject<?> wBenchObject = (WBenchObject<?>) renderObject;
        int id = wBenchObject.getListID();
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

    @Override
    public TimerPool getTimerPool() {
        return WBench.get().getScreen().getTimerPool();
    }

    public void removeObject(SceneObject renderObject) {
        WBenchObject<?> wBenchObject = (WBenchObject<?>) renderObject;
        int id = wBenchObject.getListID();
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
            WBenchObject<?> wBenchObject = (WBenchObject<?>) sceneObject;
            if (sceneObject != null) {
                int id = wBenchObject.getListID();
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

    public Set<? extends SceneObject> getEndFrameVisibleObjects() {
        return this.endFrameVisibleObjects;
    }

    public WBenchWorld setEndFrameVisibleObjects(Set<? extends SceneObject> endFrameVisibleObjects) {
        this.endFrameVisibleObjects = endFrameVisibleObjects;
        return this;
    }

    public Queue<Integer> getFreeIds() {
        return this.freeIds;
    }

    public void setCamera(@Nullable ICamera camera) {
        this.camera = camera;
    }

    public Map<Integer, WBenchObject<?>> getIdMap() {
        return this.idMap;
    }

    public WBenchEnvironment getEnvironment() {
        return this.environment;
    }

    public ICamera getCamera() {
        return this.camera;
    }

    public Set<WBenchObject<?>> getSceneObjects() {
        return this.toRenderSet;
    }

    public int getTicks() {
        return this.ticks;
    }

    @Override
    public WBenchWorldSnapshotData takeSnapshot() {
        return new WBenchWorldSnapshotData(new ArrayDeque<>(this.freeIds), new HashSet<>(this.getSceneObjects()), new HashMap<>(this.idMap), this.getEnvironment().takeSnapshot());
    }

    @SuppressWarnings("all")
    @Override
    public void fixSnapshot(WBenchWorldSnapshotData wBenchWorldSnapshotData) {
        this.freeIds.clear();
        this.freeIds.addAll(wBenchWorldSnapshotData.freeIds);
        this.idMap.clear();
        this.idMap.putAll(wBenchWorldSnapshotData.idMap);
        this.toRenderSet.forEach(e -> e.onDestroy(this));
        this.toRenderSet.clear();
        this.toRenderSet.addAll(wBenchWorldSnapshotData.toRenderSet);
        this.toRenderSet.forEach(e -> e.onSpawn(this));
        this.getEnvironment().fixSnapshot(wBenchWorldSnapshotData.environment);

        for (WBenchObject<?> wBenchObject : this.toRenderSet) {
            if (wBenchWorldSnapshotData.snapshotDataMap.containsKey(wBenchObject)) {
                WBenchObject<WBenchObject.WBenchObjectSnapshotData> wBenchObject1 = (WBenchObject<WBenchObject.WBenchObjectSnapshotData>) wBenchObject;
                wBenchObject1.fixSnapshot(wBenchWorldSnapshotData.snapshotDataMap.get(wBenchObject));
            }
        }
    }

    public static class WBenchWorldSnapshotData implements ISnapshotCompatible.SnapshotData {
        public final Queue<Integer> freeIds;
        public final Set<WBenchObject<?>> toRenderSet;
        public final Map<Integer, WBenchObject<?>> idMap;

        public final WBenchEnvironment.WBenchEnvironmentSnapshotData environment;
        public final Map<WBenchObject<?>, WBenchObject.WBenchObjectSnapshotData> snapshotDataMap;

        @SuppressWarnings("all")
        public WBenchWorldSnapshotData(Queue<Integer> freeIds, Set<WBenchObject<?>> toRenderSet, Map<Integer, WBenchObject<?>> idMap, WBenchEnvironment.WBenchEnvironmentSnapshotData environment) {
            this.freeIds = freeIds;
            this.toRenderSet = toRenderSet;
            this.idMap = idMap;
            this.environment = environment;

            this.snapshotDataMap = new HashMap<>();

            for (WBenchObject<?> wBenchObject : this.toRenderSet) {
                WBenchObject<? extends WBenchObject.WBenchObjectSnapshotData> wBenchObject1 = (WBenchObject<? extends WBenchObject.WBenchObjectSnapshotData>) wBenchObject;
                this.snapshotDataMap.put(wBenchObject, wBenchObject1.takeSnapshot());
            }
        }
    }
}
