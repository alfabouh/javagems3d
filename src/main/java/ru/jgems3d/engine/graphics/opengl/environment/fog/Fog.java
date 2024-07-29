package ru.jgems3d.engine.graphics.opengl.environment.fog;

import org.joml.Vector3f;

public final class Fog {
    private float density;
    private Vector3f color;

    public Fog() {
        this.density = -1.0f;
        this.color = new Vector3f(0.85f);
    }

    public Vector3f getColor() {
        return new Vector3f(this.color);
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void disable() {
        this.setDensity(-1.0f);
    }

    public float getDensity() {
        return this.density;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
