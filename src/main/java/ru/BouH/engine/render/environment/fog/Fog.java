package ru.BouH.engine.render.environment.fog;

import org.joml.Vector3d;

public final class Fog {
    private float density;
    private Vector3d color;

    public Fog() {
        this.density = -1.0f;
        this.color = new Vector3d(0.85d);
    }

    public void setColor(Vector3d color) {
        this.color = color;
    }

    public Vector3d getColor() {
        return new Vector3d(this.color);
    }

    public void disable() {
        this.setDensity(-1.0f);
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getDensity() {
        return this.density;
    }
}
