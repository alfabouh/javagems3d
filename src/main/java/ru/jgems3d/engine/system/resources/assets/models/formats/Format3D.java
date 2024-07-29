package ru.jgems3d.engine.system.resources.assets.models.formats;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Format3D implements IFormat {
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scaling;
    private boolean isOrientedToView;

    public Format3D(@NotNull Vector3f position, Vector3f rotation, Vector3f scaling) {
        this.position = position;
        this.rotation = rotation == null ? new Vector3f(0.0f) : rotation;
        this.scaling = scaling == null ? new Vector3f(1.0f) : scaling;
        this.isOrientedToView = false;
    }

    public Format3D(Vector3f position, Vector3f rotation) {
        this(position, rotation, new Vector3f(1.0f));
    }

    public Format3D(Vector3f position) {
        this(position, new Vector3f(0.0f), new Vector3f(1.0f));
    }

    public Format3D() {
        this(new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f));
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f position) {
        this.getPosition().set(position);
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.getRotation().set(rotation);
    }

    public Vector3f getScaling() {
        return this.scaling;
    }

    public void setScaling(Vector3f scale) {
        this.getScaling().set(scale);
    }

    @Override
    public IFormat copy() {
        return new Format3D(new Vector3f(this.getPosition()), new Vector3f(this.getRotation()), new Vector3f(this.getScaling()));
    }

    @Override
    public boolean isOrientedToViewMatrix() {
        return this.isOrientedToView;
    }

    public void setOrientedToView(boolean orientedToView) {
        this.isOrientedToView = orientedToView;
    }
}
