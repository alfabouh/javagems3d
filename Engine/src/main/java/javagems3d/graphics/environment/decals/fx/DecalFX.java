package javagems3d.graphics.environment.decals.fx;

import javagems3d.graphics.environment.decals.DecalMaterial;
import javagems3d.graphics.environment.decals.DecalTextureProperties;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

public abstract class DecalFX implements IWorldObject, IWorldTicked {
    private final DecalMaterial material;
    private final DecalTextureProperties decalTextureProperties;

    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;

    private final int terrainLayerID;
    private SceneObject attachedTo;

    private Matrix4f localDecalMatrix;

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

    public Matrix4f getInverseModelMatrix() {
        return this.getModelMatrix().invert();
    }

    public Matrix4f getModelMatrix() {
        if (this.attachedTo != null) {
            Matrix4f mat = TransformUtils.getModelMatrix(Objects.requireNonNull(this.attachedTo).getModel().getPose());
            return mat.mul(this.localDecalMatrix);
        }
        return new Matrix4f().identity()
                .translate(this.getPosition())
                .rotateXYZ(this.getRotation().negate())
                .scale(this.getScale());
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

    public DecalFX setAttachedTo(SceneObject attachedTo) {
        if (attachedTo == null || !attachedTo.hasModel()) {
            this.localDecalMatrix = null;
        }
        Matrix4f mat = TransformUtils.getModelMatrix(Objects.requireNonNull(attachedTo).getModel().getPose());
        this.localDecalMatrix = new Matrix4f(mat.invert().mul(this.getModelMatrix()));
        this.attachedTo = attachedTo;
        return this;
    }
}
