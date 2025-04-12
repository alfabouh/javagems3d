package api.scripting.classes.util;

import org.joml.Vector3f;

public class Vec3f {
    private final float x;
    private final float y;
    private final float z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f createJOML() {
        return new Vector3f(this.x, this.y, this.z);
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
}
