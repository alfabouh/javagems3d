package ru.jgems3d.engine.system.resources.assets.material.samples;

import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.ISample;

public class ColorSample implements ISample {
    private final Vector4f color;

    private ColorSample(Vector4f color) {
        this.color = color;
    }

    public static ColorSample createColor(Vector4f Vector4f) {
        return new ColorSample(new Vector4f(Vector4f));
    }

    public static ColorSample createColor(Vector3f vector3f) {
        return new ColorSample(new Vector4f(vector3f, 1.0f));
    }

    public Vector4f getColor() {
        return this.color;
    }
}
