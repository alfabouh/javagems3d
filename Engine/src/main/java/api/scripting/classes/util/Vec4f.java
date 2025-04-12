package api.scripting.classes.util;

import org.joml.Vector4f;

public class Vec4f {
    private final float x;
    private final float y;
    private final float z;
    private final float w;

    public Vec4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f createJOML() {
        return new Vector4f(this.x, this.y, this.z, this.w);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float getW() {
        return this.w;
    }
}
