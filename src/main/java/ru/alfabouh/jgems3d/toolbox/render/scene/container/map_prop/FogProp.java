package ru.alfabouh.jgems3d.toolbox.render.scene.container.map_prop;

import org.joml.Vector3d;

public class FogProp {
    public final Vector3d fogColor;
    public float fogDensity;
    public boolean fogEnabled;
    public boolean skyCoveredByFog;

    public FogProp() {
        this(new Vector3d(1.0f), 0.0f, false, true);
    }

    public FogProp(Vector3d fogColor, float fogDensity, boolean fogEnabled, boolean skyCoveredByFog) {
        this.fogColor = fogColor;
        this.fogDensity = fogDensity;
        this.fogEnabled = fogEnabled;
        this.skyCoveredByFog = skyCoveredByFog;
    }

    public void setFogColor(Vector3d fogColor) {
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

    public Vector3d getFogColor() {
        return new Vector3d(this.fogColor);
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
