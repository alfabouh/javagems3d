package api.scripting.coding.env.internal.util.math;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSVector3f", description = "...")
public class JSVector3f {
    private final Vector3f vector3f;

    @JSCodingConstructor(description = "Create zero vector")
    public JSVector3f() {
        this.vector3f = new Vector3f();
    }

    @JSCodingConstructor(description = "Create vector with components", paramNames = {"x", "y", "z"})
    public JSVector3f(float x, float y, float z) {
        this.vector3f = new Vector3f(x, y, z);
    }

    @JSCodingConstructor(description = "Create vector with integer components", paramNames = {"x", "y", "z"})
    public JSVector3f(int x, int y, int z) {
        this.vector3f = new Vector3f(x, y, z);
    }

    @JSCodingConstructor(description = "Create vector with same scalar for all components", paramNames = {"scalar"})
    public JSVector3f(float scalar) {
        this.vector3f = new Vector3f(scalar);
    }

    @JSCodingConstructor(description = "Copy vector", paramNames = {"other"})
    public JSVector3f(JSVector3f other) {
        this.vector3f = new Vector3f(other.vector3f);
    }

    @JSCodingConstructor(description = "Wrap JOML vector", paramNames = {"vec"})
    public JSVector3f(Vector3f vec) {
        this.vector3f = new Vector3f(vec);
    }

    @JSCodingFunctionOrMethod(description = "Get X component")
    public float x() { return this.vector3f.x; }

    @JSCodingFunctionOrMethod(description = "Get Y component")
    public float y() { return this.vector3f.y; }

    @JSCodingFunctionOrMethod(description = "Get Z component")
    public float z() { return this.vector3f.z; }

    @JSCodingFunctionOrMethod(description = "Set components", paramNames = {"x", "y", "z"})
    public JSVector3f set(float x, float y, float z) {
        this.vector3f.set(x, y, z);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy values from another vector", paramNames = {"other"})
    public JSVector3f set(JSVector3f other) {
        this.vector3f.set(other.vector3f);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Add vector", paramNames = {"other"})
    public JSVector3f add(JSVector3f other) {
        this.vector3f.add(other.vector3f);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Add components", paramNames = {"x", "y", "z"})
    public JSVector3f add(float x, float y, float z) {
        this.vector3f.add(x, y, z);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Subtract vector", paramNames = {"other"})
    public JSVector3f sub(JSVector3f other) {
        this.vector3f.sub(other.vector3f);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Multiply by scalar", paramNames = {"scalar"})
    public JSVector3f mul(float scalar) {
        this.vector3f.mul(scalar);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Divide by scalar", paramNames = {"scalar"})
    public JSVector3f div(float scalar) {
        this.vector3f.div(scalar);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Dot product", paramNames = {"other"})
    public float dot(JSVector3f other) {
        return this.vector3f.dot(other.vector3f);
    }

    @JSCodingFunctionOrMethod(description = "Cross product", paramNames = {"other"})
    public JSVector3f cross(JSVector3f other) {
        this.vector3f.cross(other.vector3f);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Vector length")
    public float length() {
        return this.vector3f.length();
    }

    @JSCodingFunctionOrMethod(description = "Distance to another vector", paramNames = {"other"})
    public float distance(JSVector3f other) {
        return this.vector3f.distance(other.vector3f);
    }

    @JSCodingFunctionOrMethod(description = "Normalize vector")
    public JSVector3f normalize() {
        this.vector3f.normalize();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Negate vector")
    public JSVector3f negate() {
        this.vector3f.negate();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy vector")
    public JSVector3f copy() {
        return new JSVector3f(this);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying JOML vector")
    public Vector3f getJavaVector3f() {
        return this.vector3f;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return this.vector3f.toString();
    }
}