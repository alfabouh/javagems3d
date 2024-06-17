package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.map_prop;

import org.joml.Vector3d;

public class SkyProp {
    public final Vector3d sunPos;
    public final Vector3d sunColor;
    public final float sunBrightness;

    public SkyProp(Vector3d sunPos, Vector3d sunColor, float sunBrightness) {
        this.sunPos = sunPos;
        this.sunColor = sunColor;
        this.sunBrightness = sunBrightness;
    }
}
