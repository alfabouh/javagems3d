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

package javagems3d.graphics.objects.entities;

import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldTicked;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SceneProp extends SceneObject implements IWorldTicked {
    private final String name;
    protected final IModelConstructor<Void, ? extends IMesh> propModelConstructor;
    protected boolean isVisible;
    protected boolean isDead;

    public SceneProp(@NotNull String name, @NotNull IRenderWorld world, @NotNull PropRenderData propRenderData) {
        super(world, new Model3D(new Pose3D(), propRenderData.getMeshDataGroup()), propRenderData.getObjectRenderAttributes());
        this.propModelConstructor = propRenderData.getPropModelConstructor();
        this.name = name;
        this.isVisible = true;
        this.isDead = false;
    }

    public Vector3f getPosition() {
        return new Vector3f(this.getModel().getPose().getPosition());
    }

    public Vector3f getRotation() {
        return new Vector3f(this.getModel().getPose().getRotation());
    }

    public Vector3f getScaling() {
        return new Vector3f(this.getModel().getPose().getScaling());
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        Log.get().debug("[ " + this + " ]" + " - PreRender");
        super.onSpawn(iWorld);
        if (this.canBeRendered()) {
            if (!this.hasModel() && this.getPropModelConstructor() != null) {
                this.setModel(new Model3D(new Pose3D(), this.getPropModelConstructor().constructMeshDataGroup(null)));
            }
            this.getRenderFabricsSet().forEach(e -> e.createResources(this));
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        Log.get().debug("[ " + this + " ]" + " - PostRender");
        super.onDestroy(iWorld);
        if (this.canBeRendered()) {
            this.getRenderFabricsSet().forEach(e -> e.destroyResources(this));
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        final CullingAABB calcAABB = this.pickAABBDataFromMesh();
        this.setCullingData(calcAABB);
      // if (calcAABB != null && this.hasModel()) {
      //     //this.getModel().getPose().setCenterOffset((this.getRenderAttributes() != null && this.getRenderAttributes().getProperties().getBool(JGemsRenderProperties.KEY_NORMALIZE_MODEL_CENTER)) ? calcAABB.getCenter() : new Vector3f());
      // }
    }

    @Override
    public Vector3f getRotationAngle() {
        return this.hasModel() ? this.getModel().getPose().getRotation() : new Vector3f(0.0f, 1.0f, 0.0f);
    }

    @Override
    public Vector3f getPositionToAttachLights() {
        return this.hasModel() ? this.getModel().getPose().getPosition() : new Vector3f(0.0f);
    }

    public IModelConstructor<Void, ? extends IMesh> getPropModelConstructor() {
        return this.propModelConstructor;
    }

    @Override
    public IRenderWorld getWorld() {
        return super.getWorld();
    }

    public void setDead() {
        this.isDead = true;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public boolean isDead() {
        return this.isDead;
    }

    @Override
    public boolean canBeRendered() {
        return super.canBeRendered() && this.isVisible;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName() + " - " + this.getModel().getPose().getPosition();
    }
}
