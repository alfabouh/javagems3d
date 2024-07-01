package ru.alfabouh.jgems3d.mapsys.file.save.objects.map_prop;

import org.joml.Vector3f;

public class FogProp {
    private final Vector3f fogColor;
    private float fogDensity;
    private boolean fogEnabled;
    private boolean skyCoveredByFog;

    public FogProp() {
        this(new Vector3f(1.0f), 0.0f, false, true);
    }

    public FogProp(Vector3f fogColor, float fogDensity, boolean fogEnabled, boolean skyCoveredByFog) {
        this.fogColor = fogColor;
        this.fogDensity = fogDensity;
        this.fogEnabled = fogEnabled;
        this.skyCoveredByFog = skyCoveredByFog;
    }

    public void setFogColor(Vector3f fogColor) {
        this.fogColor.set(fogColor);
    }

    public void setFogEnabled(boolean fogEnabled) {
        this.fogEnabled = fogEnabled;
    }

    public void setFogDensity(float fogDensity) {
        this.fogDensity = fogDensity;
    }

    public void setSkyCoveredByFog(boolean skyCoveredByFog) {
        this.skyCoveredByFog = skyCoveredByFog;
    }

    public Vector3f getFogColor() {
        return new Vector3f(this.fogColor);
    }

    public boolean isFogEnabled() {
        return this.fogEnabled;
    }

    public float getFogDensity() {
        return this.fogDensity;
    }

    public boolean isSkyCoveredByFog() {
        return this.skyCoveredByFog;
    }
}
