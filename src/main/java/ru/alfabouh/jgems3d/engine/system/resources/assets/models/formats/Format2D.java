package ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;

public class Format2D implements IFormat {
    private final Vector2d position;
    private double rotation;
    private final Vector2d scale;
    private boolean isOrientedToView;

    public Format2D(@NotNull Vector2d position, double rotation, Vector2d scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.isOrientedToView = false;
    }

    public Format2D(Vector2d position, float rotation) {
        this(position, rotation, new Vector2d(1.0d));
    }

    public Format2D(Vector2d position) {
        this(position, 0.0d, new Vector2d(1.0d));
    }

    public Format2D() {
        this(new Vector2d(0.0d),0.0d, new Vector2d(1.0d));
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public void setPosition(Vector2d position) {
        this.getPosition().set(position);
    }

    public double getRotation() {
        return this.rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public Vector2d getScale() {
        return this.scale;
    }

    public void setScale(Vector2d scale) {
        this.getScale().set(scale);
    }

    @Override
    public IFormat copy() {
        return new Format2D(new Vector2d(this.getPosition()), this.getRotation(), new Vector2d(this.getScale()));
    }

    @Override
    public boolean isOrientedToViewMatrix() {
        return this.isOrientedToView;
    }

    public void setOrientedToView(boolean orientedToView) {
        isOrientedToView = orientedToView;
    }
}
