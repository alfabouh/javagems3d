package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.map_prop;

import org.joml.Vector3d;

public class FogProp {
    public final Vector3d fogColor;
    public final float fogDensity;
    public final boolean fogEnabled;
    public final boolean skyCoveredByFog;

    public FogProp(Vector3d fogColor, float fogDensity, boolean fogEnabled, boolean skyCoveredByFog) {
        this.fogColor = fogColor;
        this.fogDensity = fogDensity;
        this.fogEnabled = fogEnabled;
        this.skyCoveredByFog = skyCoveredByFog;
    }
}
