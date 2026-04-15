package javagems3d.system.resources.assets.models.pose;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@SuppressWarnings("all")
public final class Pose3D implements IPose {
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scaling;
    private boolean isOrientedToView;

    public Pose3D(@NotNull Vector3f position, Vector3f rotation, Vector3f scaling) {
        this.position = position;
        this.rotation = rotation == null ? new Vector3f(0.0f) : rotation;
        this.scaling = scaling == null ? new Vector3f(1.0f) : scaling;
        this.isOrientedToView = false;
    }

    public Pose3D(Vector3f position, Vector3f rotation) {
        this(position, rotation, new Vector3f(1.0f));
    }

    public Pose3D(Vector3f position) {
        this(position, new Vector3f(0.0f), new Vector3f(1.0f));
    }

    public Pose3D() {
        this(new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f));
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Pose3D setPosition(Vector3f position) {
        this.getPosition().set(position);
        return this;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public Pose3D setRotation(Vector3f rotation) {
        this.getRotation().set(rotation);
        return this;
    }

    public Vector3f getScaling() {
        return this.scaling;
    }

    public Pose3D setScaling(Vector3f scale) {
        this.getScaling().set(scale);
        return this;
    }

    @Override
    public Pose3D copy() {
        return new Pose3D(new Vector3f(this.getPosition()), new Vector3f(this.getRotation()), new Vector3f(this.getScaling()));
    }

    public boolean isOrientedToViewMatrix() {
        return this.isOrientedToView;
    }

    public Pose3D setOrientedToView(boolean orientedToView) {
        this.isOrientedToView = orientedToView;
        return this;
    }
}
