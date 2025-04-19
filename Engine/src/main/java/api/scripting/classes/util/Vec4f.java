package api.scripting.classes.util;

import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import org.joml.Vector3f;
import org.joml.Vector4f;

@JSTypeDoc(description = "Vector with 4 floats", priority = JSTypeDoc.Priority.LOW)
public final class Vec4f {
    private final float x;
    private final float y;
    private final float z;
    private final float w;

    public Vec4f(Vector4f vector4f) {
        this.x = vector4f.x;
        this.y = vector4f.y;
        this.z = vector4f.z;
        this.w = vector4f.w;
    }

    public Vec4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f createJOML() {
        return new Vector4f(this.x, this.y, this.z, this.w);
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

    @JSMethodDoc(description = "get W(Fourth value)", args = {}, order = 3)
    public float getW() {
        return this.w;
    }
}
