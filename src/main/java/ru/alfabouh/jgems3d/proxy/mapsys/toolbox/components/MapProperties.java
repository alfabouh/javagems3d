package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.components;

import org.joml.Vector3f;
import org.joml.Vector4d;

public class MapProperties {
    private Vector4d fog;
    private Vector3f sunColor;
    private float sunBrightness;
    private boolean skyCoveredByFog;

    public MapProperties(Vector4d fog, boolean skyCoveredByFog, float sunBrightness, Vector3f sunColor) {
        this.fog = fog;
        this.sunBrightness = sunBrightness;
        this.skyCoveredByFog = skyCoveredByFog;
        this.sunColor = sunColor;
    }

    public void setSunColor(Vector3f sunColor) {
        this.sunColor = sunColor;
    }

    public void setSkyCoveredByFog(boolean skyCoveredByFog) {
        this.skyCoveredByFog = skyCoveredByFog;
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    public void setFog(Vector4d fog) {
        this.fog = fog;
    }

    public boolean isSkyCoveredByFog() {
        return this.skyCoveredByFog;
    }

    public Vector4d getFog() {
        return this.fog;
    }

    public Vector3f getSunColor() {
        return this.sunColor;
    }

    public float getSunBrightness() {
        return this.sunBrightness;
    }
}
