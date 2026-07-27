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

package javagems3d.graphics.environment.skybox.background;

import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.scene.culling.SceneCulling;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class SkyBoxBackground implements ISkyBackground {
    protected final Set<SceneProp> toRenderSet;
    protected final FixedCamera scaledCameraBackground;
    private final IWorld world;
    protected float viewScaling;
    protected final SceneCulling sceneCulling;

    public SkyBoxBackground(IWorld world, float viewScaling) {
        this.toRenderSet = new HashSet<>();
        this.scaledCameraBackground = new FixedCamera(new Vector3f(), new Vector3f());
        this.sceneCulling = new SceneCulling(SceneCulling.FRUSTUM_CPU, null);
        this.viewScaling = viewScaling;
        this.world = world;
    }

    public void update(ICamera mainViewCamera) {
        this.getScaledCameraBackground().setCameraPosition(mainViewCamera.getCamPosition().mul(1.0f / this.getViewScaling()));
        this.getScaledCameraBackground().setCameraRotation(mainViewCamera.getCamRotation());
        this.updateIterator();
    }

    protected void updateIterator() {
        Iterator<SceneProp> scenePropIterator = this.getSkySceneObjects().iterator();
        while (scenePropIterator.hasNext()) {
            SceneProp sceneProp = scenePropIterator.next();
            sceneProp.onUpdate(this.getWorld());
            if (sceneProp.isDead()) {
                sceneProp.onDestroy(this.getWorld());
                scenePropIterator.remove();
            }
        }
    }

    public void setViewScaling(float viewScaling) {
        this.viewScaling = viewScaling;
    }

    public void clearBackGround() {
        this.getSkySceneObjects().forEach(e -> e.onDestroy(this.getWorld()));
        this.getSkySceneObjects().clear();
    }

    @Override
    public void addObject(SceneProp object) {
        object.onSpawn(this.getWorld());
        this.getSkySceneObjects().add(object);
    }

    @Override
    public void removeObject(SceneProp object) {
        object.onDestroy(this.getWorld());
        this.getSkySceneObjects().remove(object);
    }

    @Override
    public void destroy(IWorld world) {
        this.clearBackGround();
        this.getSceneCulling().destroyResources();
    }

    @Override
    public void create(IWorld world) {
        this.getSceneCulling().createResources();
    }

    public IWorld getWorld() {
        return this.world;
    }

    public @NotNull FixedCamera getScaledCameraBackground() {
        return this.scaledCameraBackground;
    }

    public float getViewScaling() {
        return this.viewScaling;
    }

    public SceneCulling getSceneCulling() {
        return this.sceneCulling;
    }

    public @NotNull Set<SceneProp> getSkySceneObjectsFiltered() {
        Set<SceneProp> set = new HashSet<>(this.getSkySceneObjects());
        @SuppressWarnings("unchecked") Collection<? extends ICulled>[] collections = new Collection[] { set };
        this.getSceneCulling().cull(JGemsTransformManager.INSTANCE.getPerspectiveMatrix(), this.getScaledCameraBackground(), collections);
        return set;
    }

    public @NotNull Set<SceneProp> getSkySceneObjects() {
        return this.toRenderSet;
    }
}
