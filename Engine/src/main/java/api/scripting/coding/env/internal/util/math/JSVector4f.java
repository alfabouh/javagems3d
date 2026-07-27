/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package api.scripting.coding.env.internal.util.math;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import org.joml.Vector4f;

@JSCodingClass(binding = "JSVector4f", description = "4D vector wrapper with useful operations.")
public class JSVector4f {
    private final Vector4f vector;

    @JSCodingConstructor(description = "Create zero vector")
    public JSVector4f() {
        this.vector = new Vector4f();
    }

    @JSCodingConstructor(description = "Create vector with components", paramNames = {"x", "y", "z", "w"})
    public JSVector4f(float x, float y, float z, float w) {
        this.vector = new Vector4f(x, y, z, w);
    }

    @JSCodingConstructor(description = "Create vector with integer components", paramNames = {"x", "y", "z", "w"})
    public JSVector4f(int x, int y, int z, int w) {
        this.vector = new Vector4f(x, y, z, w);
    }

    @JSCodingConstructor(description = "Create vector with same scalar for all components", paramNames = {"scalar"})
    public JSVector4f(float scalar) {
        this.vector = new Vector4f(scalar);
    }

    @JSCodingConstructor(description = "Copy vector", paramNames = {"other"})
    public JSVector4f(JSVector4f other) {
        this.vector = new Vector4f(other.vector);
    }

    @JSCodingConstructor(description = "Wrap JOML vector", paramNames = {"vec"})
    public JSVector4f(Vector4f vec) {
        this.vector = new Vector4f(vec);
    }

    @JSCodingFunctionOrMethod(description = "Get X component")
    public float x() { return this.vector.x; }

    @JSCodingFunctionOrMethod(description = "Get Y component")
    public float y() { return this.vector.y; }

    @JSCodingFunctionOrMethod(description = "Get Z component")
    public float z() { return this.vector.z; }

    @JSCodingFunctionOrMethod(description = "Get W component")
    public float w() { return this.vector.w; }

    @JSCodingFunctionOrMethod(description = "Set components", paramNames = {"x", "y", "z", "w"})
    public JSVector4f set(float x, float y, float z, float w) {
        this.vector.set(x, y, z, w);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy values from another vector", paramNames = {"other"})
    public JSVector4f set(JSVector4f other) {
        this.vector.set(other.vector);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Add vector", paramNames = {"other"})
    public JSVector4f add(JSVector4f other) {
        this.vector.add(other.vector);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Add components", paramNames = {"x", "y", "z", "w"})
    public JSVector4f add(float x, float y, float z, float w) {
        this.vector.add(x, y, z, w);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Subtract vector", paramNames = {"other"})
    public JSVector4f sub(JSVector4f other) {
        this.vector.sub(other.vector);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Multiply by scalar", paramNames = {"scalar"})
    public JSVector4f mul(float scalar) {
        this.vector.mul(scalar);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Divide by scalar", paramNames = {"scalar"})
    public JSVector4f div(float scalar) {
        this.vector.div(scalar);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Dot product", paramNames = {"other"})
    public float dot(JSVector4f other) {
        return this.vector.dot(other.vector);
    }

    @JSCodingFunctionOrMethod(description = "Vector length")
    public float length() {
        return this.vector.length();
    }

    @JSCodingFunctionOrMethod(description = "Distance to another vector", paramNames = {"other"})
    public float distance(JSVector4f other) {
        return this.vector.distance(other.vector);
    }

    @JSCodingFunctionOrMethod(description = "Normalize vector")
    public JSVector4f normalize() {
        this.vector.normalize();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Negate vector")
    public JSVector4f negate() {
        this.vector.negate();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy vector")
    public JSVector4f copy() {
        return new JSVector4f(this);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying JOML vector")
    public Vector4f getJavaVector4f() {
        return this.vector;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return this.vector.toString();
    }
}