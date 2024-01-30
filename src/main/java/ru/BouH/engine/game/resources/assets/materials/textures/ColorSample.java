package ru.BouH.engine.game.resources.assets.materials.textures;

import org.joml.Vector3d;
import org.joml.Vector4d;

public class ColorSample implements ISample {
    private final Vector4d color;

    private ColorSample(Vector4d color) {
        this.color = color;
    }

    public static ColorSample createColor(Vector4d vector4d) {
        return new ColorSample(new Vector4d(vector4d));
    }

    public static ColorSample createColor(Vector3d vector3d) {
        return new ColorSample(new Vector4d(vector3d, 1.0d));
    }

    public Vector4d getColor() {
        return this.color;
    }
}
