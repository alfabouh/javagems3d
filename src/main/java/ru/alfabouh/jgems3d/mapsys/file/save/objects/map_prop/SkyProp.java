package ru.alfabouh.jgems3d.mapsys.file.save.objects.map_prop;

import org.joml.Vector3f;

public class SkyProp {
    private final Vector3f sunPos;
    private final Vector3f sunColor;
    private float sunBrightness;
    private String skyBoxName;

    public SkyProp() {
        this("default", new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f), 1.0f);
    }

    public SkyProp(String skyBoxName, Vector3f sunPos, Vector3f sunColor, float sunBrightness) {
        this.skyBoxName = skyBoxName;
        this.sunPos = sunPos;
        this.sunColor = sunColor;
        this.sunBrightness = sunBrightness;
    }

    public void setSkyBoxName(String skyBoxName) {
        this.skyBoxName = skyBoxName;
    }

    public void setSunColor(Vector3f sunColor) {
        this.sunColor.set(sunColor);
    }

    public void setSunPos(Vector3f sunPos) {
        this.sunPos.set(sunPos);
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    public String getSkyBoxName() {
        return this.skyBoxName;
    }

    public float getSunBrightness() {
        return this.sunBrightness;
    }

    public Vector3f getSunColor() {
        return new Vector3f(this.sunColor);
    }

    public Vector3f getSunPos() {
        return new Vector3f(this.sunPos);
    }
}
