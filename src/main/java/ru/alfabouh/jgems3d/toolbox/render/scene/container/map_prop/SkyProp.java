package ru.alfabouh.jgems3d.toolbox.render.scene.container.map_prop;

import org.joml.Vector3d;

public class SkyProp {
    public final Vector3d sunPos;
    public final Vector3d sunColor;
    public float sunBrightness;

    public SkyProp() {
        this(new Vector3d(0.0f, 1.0f, 0.0f), new Vector3d(1.0f), 1.0f);
    }

    public SkyProp(Vector3d sunPos, Vector3d sunColor, float sunBrightness) {
        this.sunPos = sunPos;
        this.sunColor = sunColor;
        this.sunBrightness = sunBrightness;
    }

    public void setSunColor(Vector3d sunColor) {
        this.sunColor.set(sunColor);
    }

    public void setSunPos(Vector3d sunPos) {
        this.sunPos.set(sunPos);
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    public float getSunBrightness() {
        return this.sunBrightness;
    }

    public Vector3d getSunColor() {
        return new Vector3d(this.sunColor);
    }

    public Vector3d getSunPos() {
        return new Vector3d(this.sunPos);
    }
}
