package ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class Format3D implements IFormat {
    private final Vector3d position;
    private final Vector3d rotation;
    private final Vector3d scaling;
    private boolean isOrientedToView;

    public Format3D(@NotNull Vector3d position, Vector3d rotation, Vector3d scaling) {
        this.position = position;
        this.rotation = rotation == null ? new Vector3d(0.0d) : rotation;
        this.scaling = scaling == null ? new Vector3d(1.0d) : scaling;
        this.isOrientedToView = false;
    }

    public Format3D(Vector3d position, Vector3d rotation) {
        this(position, rotation, new Vector3d(1.0d));
    }

    public Format3D(Vector3d position) {
        this(position, new Vector3d(0.0d), new Vector3d(1.0d));
    }

    public Format3D() {
        this(new Vector3d(0.0d), new Vector3d(0.0d), new Vector3d(1.0d));
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public void setPosition(Vector3d position) {
        this.getPosition().set(position);
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector3d rotation) {
        this.getRotation().set(rotation);
    }

    public Vector3d getScaling() {
        return this.scaling;
    }

    public void setScaling(Vector3d scale) {
        this.getScaling().set(scale);
    }

    @Override
    public IFormat copy() {
        return new Format3D(new Vector3d(this.getPosition()), new Vector3d(this.getRotation()), new Vector3d(this.getScaling()));
    }

    @Override
    public boolean isOrientedToViewMatrix() {
        return this.isOrientedToView;
    }

    public void setOrientedToView(boolean orientedToView) {
        this.isOrientedToView = orientedToView;
    }
}
