package ru.BouH.engine.game.resources.assets.models.formats;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class Format2D implements IFormat {
    private final Vector2d position;
    private final Vector2d rotation;
    private final Vector2d scale;
    private boolean isOrientedToView;

    public Format2D(@NotNull Vector2d position, Vector2d rotation, Vector2d scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.isOrientedToView = false;
    }

    public Format2D(Vector2d position, Vector2d rotation) {
        this(position, rotation, new Vector2d(1.0d));
    }

    public Format2D(Vector2d position) {
        this(position, new Vector2d(0.0d), new Vector2d(1.0d));
    }

    public Format2D() {
        this(new Vector2d(0.0d), new Vector2d(0.0d), new Vector2d(1.0d));
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public void setPosition(Vector2d position) {
        this.getPosition().set(position);
    }

    public Vector2d getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector2d rotation) {
        this.getRotation().set(rotation);
    }

    public Vector2d getScale() {
        return this.scale;
    }

    public void setScale(Vector2d scale) {
        this.getScale().set(scale);
    }

    @Override
    public IFormat copy() {
        return new Format2D(new Vector2d(this.getPosition()), new Vector2d(this.getRotation()), new Vector2d(this.getScale()));
    }

    public void setOrientedToView(boolean orientedToView) {
        isOrientedToView = orientedToView;
    }

    @Override
    public boolean isOrientedToViewMatrix() {
        return this.isOrientedToView;
    }
}
