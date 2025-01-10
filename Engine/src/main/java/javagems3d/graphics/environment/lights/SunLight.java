package javagems3d.graphics.environment.lights;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SunLight {
    private Vector3f sunPos;
    private Vector3f sunColor;
    private float sunBrightness;

    public SunLight(@NotNull Vector3f sunPos, Vector3f sunColor, float sunBrightness) {
        this.sunPos = sunPos;
        this.sunColor = sunColor == null ? new Vector3f(1.0f) : sunColor;
        this.sunBrightness = sunBrightness;
    }

    public void setSunColor(Vector3f sunColor) {
        this.sunColor = sunColor;
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    public void setSunPosition(@NotNull Vector3f sunPos) {
        this.sunPos = sunPos;
    }

    public Vector3f getSunPosition() {
        return this.sunPos;
    }

    public Vector3f getSunColor() {
        return this.sunColor;
    }

    public float getSunBrightness() {
        return this.sunBrightness;
    }
}
