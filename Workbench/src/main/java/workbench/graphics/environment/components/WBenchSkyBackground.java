/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package workbench.graphics.environment.components;

import javagems3d.graphics.environment.skybox.background.SkyBoxBackground;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;

import java.util.*;

public class WBenchSkyBackground extends SkyBoxBackground implements ISnapshotCompatible<WBenchSkyBackground.WBenchSkyBackgroundSnapshotData> {
    private final Queue<Integer> freeIds;

    public WBenchSkyBackground(IWorld world, float viewScaling) {
        super(world, viewScaling);
        this.freeIds = new ArrayDeque<>();
    }

    @Override
    protected void updateIterator() {
        Iterator<SceneProp> scenePropIterator = this.getSkySceneObjects().iterator();
        while (scenePropIterator.hasNext()) {
            WBenchObject<?> wBenchObject = (WBenchObject<?>) scenePropIterator.next();
            wBenchObject.onUpdate(this.getWorld());
            if (wBenchObject.isDead()) {
                this.getFreeIds().add(wBenchObject.getListID());
                wBenchObject.onDestroy(this.getWorld());
                scenePropIterator.remove();
            }
        }
    }

    @Override
    public void addObject(SceneProp object) {
        WBenchObject<?> wBenchObject = (WBenchObject<?>) object;
        int id = wBenchObject.getListID();
        if (id < 0) {
            if (!this.getFreeIds().isEmpty()) {
                Integer freeId = this.getFreeIds().poll();
                if (freeId != null) {
                    id = freeId;
                }
            } else {
                id = this.getSkySceneObjects().size();
            }
            wBenchObject.setId(id);
        }

        super.addObject(object);
    }

    @Override
    public void removeObject(SceneProp object) {
        int id = ((WBenchObject<?>) object).getListID();
        this.getFreeIds().add(id);
        super.removeObject(object);
    }

    @Override
    public @NotNull Set<SceneProp> getSkySceneObjectsFiltered() {
        Set<SceneProp> set = new HashSet<>(this.getSkySceneObjects());
        if (!WBenchOpenGLRenderer.isRenderingBackgroundScene()) {
            @SuppressWarnings("unchecked") Collection<? extends ICulled>[] collections = new Collection[] { set };
            this.getSceneCulling().cull(JGemsTransformManager.INSTANCE.getPerspectiveMatrix(), this.getScaledCameraBackground(), collections);
        }
        return set;
    }

    public Queue<Integer> getFreeIds() {
        return this.freeIds;
    }

    @Override
    public WBenchSkyBackgroundSnapshotData takeSnapshot() {
        return new WBenchSkyBackgroundSnapshotData(new ArrayDeque<>(this.freeIds), new HashSet<>(this.getSkySceneObjects()), this.getViewScaling());
    }

    @SuppressWarnings("all")
    @Override
    public void fixSnapshot(WBenchSkyBackgroundSnapshotData wBenchSkyBackgroundSnapshotData) {
        this.freeIds.clear();
        this.freeIds.addAll(wBenchSkyBackgroundSnapshotData.freeIds);
        this.toRenderSet.clear();
        this.toRenderSet.addAll(wBenchSkyBackgroundSnapshotData.toRenderSet);
        this.setViewScaling(wBenchSkyBackgroundSnapshotData.viewScaling);

        for (SceneProp sceneProp : this.toRenderSet) {
            if (wBenchSkyBackgroundSnapshotData.snapshotDataMap.containsKey(sceneProp)) {
                WBenchObject<WBenchObject.WBenchObjectSnapshotData> wBenchObject = (WBenchObject<WBenchObject.WBenchObjectSnapshotData>) sceneProp;
                wBenchObject.fixSnapshot(wBenchSkyBackgroundSnapshotData.snapshotDataMap.get(sceneProp));
            }
        }
    }

    public static class WBenchSkyBackgroundSnapshotData implements ISnapshotCompatible.SnapshotData {
        public final Queue<Integer> freeIds;
        public final Set<SceneProp> toRenderSet;
        public final float viewScaling;
        public final Map<SceneProp, WBenchObject.WBenchObjectSnapshotData> snapshotDataMap;

        @SuppressWarnings("all")
        public WBenchSkyBackgroundSnapshotData(Queue<Integer> freeIds, Set<SceneProp> toRenderSet, float viewScaling) {
            this.freeIds = freeIds;
            this.toRenderSet = toRenderSet;
            this.viewScaling = viewScaling;
            this.snapshotDataMap = new HashMap<>();

            for (SceneProp sceneProp : toRenderSet) {
                WBenchObject<? extends WBenchObject.WBenchObjectSnapshotData> wBenchObject = (WBenchObject<? extends WBenchObject.WBenchObjectSnapshotData>) sceneProp;
                this.snapshotDataMap.put(sceneProp, wBenchObject.takeSnapshot());
            }
        }
    }
}