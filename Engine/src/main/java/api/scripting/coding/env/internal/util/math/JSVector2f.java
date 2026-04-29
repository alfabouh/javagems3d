package api.scripting.coding.env.internal.util.math;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSVector2f", description = "Wrapper for a 2D float vector, used for positions, sizes, and other 2D coordinates in scripting.")
public class JSVector2f {
    private final Vector2f vector2f;

    @JSCodingConstructor(description = "Create zero vector")
    public JSVector2f() {
        this.vector2f = new Vector2f();
    }

    @JSCodingConstructor(description = "Create vector with components", paramNames = {"x", "y"})
    public JSVector2f(float x, float y) {
        this.vector2f = new Vector2f(x, y);
    }

    @JSCodingConstructor(description = "Create vector with integer components", paramNames = {"x", "y"})
    public JSVector2f(int x, int y) {
        this.vector2f = new Vector2f(x, y);
    }

    @JSCodingConstructor(description = "Create vector with same scalar", paramNames = {"scalar"})
    public JSVector2f(float scalar) {
        this.vector2f = new Vector2f(scalar);
    }

    @JSCodingConstructor(description = "Copy vector", paramNames = {"other"})
    public JSVector2f(JSVector2f other) {
        this.vector2f = new Vector2f(other.vector2f);
    }

    @JSCodingConstructor(description = "Wrap JOML vector", paramNames = {"vec"})
    public JSVector2f(Vector2f vec) {
        this.vector2f = new Vector2f(vec);
    }

    @JSCodingFunctionOrMethod(description = "Get X component")
    public float x() {
        return this.vector2f.x;
    }

    @JSCodingFunctionOrMethod(description = "Get Y component")
    public float y() {
        return this.vector2f.y;
    }

    @JSHideFromDoc
    public Vector2f toVec2f() {
        return new Vector2f(this.vector2f.x, this.vector2f.y);
    }

    @JSHideFromDoc
    public Vector2i toVec2i() {
        return new Vector2i((int) this.vector2f.x, (int) this.vector2f.y);
    }

    @JSCodingFunctionOrMethod(description = "Set components", paramNames = {"x", "y"})
    public JSVector2f set(float x, float y) {
        this.vector2f.set(x, y);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy values from another vector", paramNames = {"other"})
    public JSVector2f set(JSVector2f other) {
        this.vector2f.set(other.vector2f);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Add vector", paramNames = {"other"})
    public JSVector2f add(JSVector2f other) {
        this.vector2f.add(other.vector2f);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Add components", paramNames = {"x", "y"})
    public JSVector2f add(float x, float y) {
        this.vector2f.add(x, y);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Subtract vector", paramNames = {"other"})
    public JSVector2f sub(JSVector2f other) {
        this.vector2f.sub(other.vector2f);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Multiply by scalar", paramNames = {"scalar"})
    public JSVector2f mul(float scalar) {
        this.vector2f.mul(scalar);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Multiply by vector", paramNames = {"other"})
    public JSVector2f mul(JSVector2f other) {
        this.vector2f.mul(other.vector2f);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Divide by scalar", paramNames = {"scalar"})
    public JSVector2f div(float scalar) {
        this.vector2f.div(scalar);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Dot product", paramNames = {"other"})
    public float dot(JSVector2f other) {
        return this.vector2f.dot(other.vector2f);
    }

    @JSCodingFunctionOrMethod(description = "Vector length")
    public float length() {
        return this.vector2f.length();
    }

    @JSCodingFunctionOrMethod(description = "Distance to another vector", paramNames = {"other"})
    public float distance(JSVector2f other) {
        return this.vector2f.distance(other.vector2f);
    }

    @JSCodingFunctionOrMethod(description = "Normalize vector")
    public JSVector2f normalize() {
        this.vector2f.normalize();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Negate vector")
    public JSVector2f negate() {
        this.vector2f.negate();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy vector")
    public JSVector2f copy() {
        return new JSVector2f(this);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying JOML vector")
    public Vector2f getJavaVector2f() {
        return this.vector2f;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return this.vector2f.toString();
    }
}