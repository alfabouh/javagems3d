package javagems3d.graphics.environment.lights;

import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SunLight extends Light {
    private Vector3f sunPos;
    private Vector3f sunColor;
    private float sunBrightness;

    public SunLight(@NotNull Vector3f sunPos, Vector3f sunColor, float sunBrightness) {
        this.sunPos = sunPos;
        this.sunColor = sunColor == null ? new Vector3f(1.0f) : sunColor;
        this.sunBrightness = sunBrightness;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
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
        return new Vector3f(this.sunPos);
    }

    public Vector3f getSunColor() {
        return new Vector3f(this.sunColor);
    }

    public float getSunBrightness() {
        return this.sunBrightness;
    }

    @Override
    public LightType getLightType() {
        return LightType.SUN;
    }
}
