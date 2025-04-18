package api.scripting.classes.util;

import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import org.joml.Vector3f;

@JSTypeDoc(description = "Vector with 3 floats", priority = JSTypeDoc.Priority.LOW)
public final class Vec3f {
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

    @JSMethodDoc(description = "get X", args = {}, order = 0)
    public float getX() {
        return this.x;
    }

    @JSMethodDoc(description = "get Y", args = {}, order = 1)
    public float getY() {
        return this.y;
    }

    @JSMethodDoc(description = "get Z", args = {}, order = 2)
    public float getZ() {
        return this.z;
    }
}
