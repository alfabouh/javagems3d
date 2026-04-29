package api.scripting.coding.env.internal.util.math;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@JSCodingClass(binding = "JSMatrix4f", description = "4x4 matrix for 3D transformations")
public class JSMatrix4f {
    private final Matrix4f matrix;

    @JSCodingConstructor(description = "Create identity matrix")
    public JSMatrix4f() {
        this.matrix = new Matrix4f();
    }

    @JSCodingConstructor(description = "Copy matrix", paramNames = {"other"})
    public JSMatrix4f(JSMatrix4f other) {
        this.matrix = new Matrix4f(other.matrix);
    }

    @JSCodingConstructor(description = "Wrap JOML Matrix4f", paramNames = {"matrix"})
    public JSMatrix4f(Matrix4f matrix) {
        this.matrix = new Matrix4f(matrix);
    }

    @JSCodingFunctionOrMethod(description = "Set identity")
    public JSMatrix4f identity() {
        this.matrix.identity();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy matrix")
    public JSMatrix4f copy() {
        return new JSMatrix4f(this);
    }

    @JSCodingFunctionOrMethod(description = "Translate matrix", paramNames = {"x", "y", "z"})
    public JSMatrix4f translate(float x, float y, float z) {
        this.matrix.translate(x, y, z);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Translate matrix", paramNames = {"vec"})
    public JSMatrix4f translate(JSVector3f vec) {
        this.matrix.translate(vec.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Rotate around X axis", paramNames = {"angle"})
    public JSMatrix4f rotateX(float angle) {
        this.matrix.rotateX(angle);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Rotate around Y axis", paramNames = {"angle"})
    public JSMatrix4f rotateY(float angle) {
        this.matrix.rotateY(angle);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Rotate around Z axis", paramNames = {"angle"})
    public JSMatrix4f rotateZ(float angle) {
        this.matrix.rotateZ(angle);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Rotate around axis", paramNames = {"axis", "angle"})
    public JSMatrix4f rotate(JSVector3f axis, float angle) {
        this.matrix.rotate(angle, axis.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Scale matrix", paramNames = {"x", "y", "z"})
    public JSMatrix4f scale(float x, float y, float z) {
        this.matrix.scale(x, y, z);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Scale matrix", paramNames = {"vec"})
    public JSMatrix4f scale(JSVector3f vec) {
        this.matrix.scale(vec.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Multiply matrices", paramNames = {"other"})
    public JSMatrix4f mul(JSMatrix4f other) {
        this.matrix.mul(other.matrix);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Invert matrix")
    public JSMatrix4f invert() {
        this.matrix.invert();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Transpose matrix")
    public JSMatrix4f transpose() {
        this.matrix.transpose();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Determinant of the matrix")
    public float determinant() {
        return this.matrix.determinant();
    }

    @JSCodingFunctionOrMethod(description = "Transform vector", paramNames = {"vec"})
    public JSVector3f transform(JSVector3f vec) {
        Vector4f tmp = new Vector4f(vec.getJavaVector3f(), 1.0f);
        this.matrix.transform(tmp);
        return new JSVector3f(tmp.x, tmp.y, tmp.z);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Matrix4f")
    public Matrix4f getJavaMatrix4f() {
        return this.matrix;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return this.matrix.toString();
    }
}