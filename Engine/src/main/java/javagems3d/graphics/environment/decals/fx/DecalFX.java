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

package javagems3d.graphics.environment.decals.fx;

import javagems3d.graphics.environment.decals.DecalMaterial;
import javagems3d.graphics.environment.decals.DecalTextureProperties;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.system.resources.managing.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

public abstract class DecalFX implements IWorldObject, IWorldTicked, ICulled {
    private final DecalMaterial material;
    private final DecalTextureProperties decalTextureProperties;

    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;

    private final int terrainLayerID;
    private SceneObject attachedTo;

    private Matrix4f localDecalMatrix;
    private Matrix4f orientationMatrix;

    public DecalFX(@NotNull DecalMaterial material, @NotNull DecalTextureProperties decalTextureProperties, int terrainLayerID) {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);

        this.material = material;
        this.decalTextureProperties = decalTextureProperties;
        this.terrainLayerID = terrainLayerID;
        this.attachedTo = null;
        this.localDecalMatrix = null;
    }

    private Matrix4f buildLocalModelMatrix() {
        return new Matrix4f().translate(this.position).rotateXYZ(-this.rotation.x, -this.rotation.y, -this.rotation.z).scale(this.scale);
    }

    public Matrix4f getInverseModelMatrix() {
        return this.getModelMatrix().invert();
    }

    private Matrix4f getOrigModelMatrix() {
        return this.buildLocalModelMatrix();
    }

    public Matrix4f getModelMatrix() {
        if (this.attachedTo != null) {
            Matrix4f modelMat = TransformUtils.getModelMatrix(Objects.requireNonNull(this.attachedTo).getModel().getPose());
            return modelMat.mul(this.localDecalMatrix);
        }
        Matrix4f orientation = this.getOrientationMatrix();
        if (orientation == null) {
            return this.getOrigModelMatrix();
        }
        return new Matrix4f(orientation).mul(this.getOrigModelMatrix());
    }

    @Override
    public CullingAABB getCullingData() {
        return ResourceManager.DEFAULT_CUBE_MESHGROUP().getMeshAABBData().getNormalizedAABB(this.getModelMatrix());
    }

    @Override
    public @NotNull CullingRules getCullingRules() {
        return new CullingRules(false, false);
    }

    public int getTerrainLayerID() {
        return this.terrainLayerID;
    }

    public DecalMaterial getMaterial() {
        return this.material;
    }

    public DecalTextureProperties getDecalTextureProperties() {
        return this.decalTextureProperties;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }

    public DecalFX setPosition(Vector3f position) {
        if (attachedTo != null) {
            return this;
        }
        this.position.set(position);
        return this;
    }

    public DecalFX setRotation(Vector3f rotation) {
        if (attachedTo != null) {
            return this;
        }
        this.rotation.set(rotation);
        return this;
    }

    public DecalFX setScale(Vector3f scale) {
        if (attachedTo != null) {
            return this;
        }
        this.scale.set(scale);
        return this;
    }

    public abstract boolean unDestructible();

    public Vector3f getPosition() {
        return new Vector3f(this.position);
    }

    public Vector3f getRotation() {
        return new Vector3f(this.rotation);
    }

    public Vector3f getScale() {
        return new Vector3f(this.scale);
    }

    public SceneObject getAttachedTo() {
        return this.attachedTo;
    }

    public Matrix4f getOrientationMatrix() {
        return this.orientationMatrix == null ? null :
                new Matrix4f(this.orientationMatrix).identity()
                        .translate(this.getPosition())
                        .mul(this.orientationMatrix)
                        .translate(this.getPosition().negate());
    }

    public DecalFX updateBasis(@Nullable Matrix4f orientationMatrix, @Nullable SceneObject attachOnObject) {
        this.orientationMatrix = orientationMatrix;
        if (attachOnObject == null || !attachOnObject.hasModel()) {
            this.attachedTo = null;
            this.localDecalMatrix = null;
            return this;
        }
        final Matrix4f mat = TransformUtils.getModelMatrix(Objects.requireNonNull(attachOnObject).getModel().getPose());
        this.attachedTo = attachOnObject;
        if (orientationMatrix == null) {
            this.localDecalMatrix = new Matrix4f(mat.invert().mul(this.getOrigModelMatrix()));
            return this;
        }
        final Matrix4f basisOnPoint = new Matrix4f().identity().translate(this.getPosition()).mul(orientationMatrix).translate(this.getPosition().negate());
        this.localDecalMatrix = new Matrix4f(mat.invert().mul(basisOnPoint.mul(this.getOrigModelMatrix())));
        return this;
    }
}