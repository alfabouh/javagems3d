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
import org.joml.Matrix3f;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSMatrix3f", description = "3x3 matrix for 2D transformations")
public class JSMatrix3f {
    private final Matrix3f matrix;

    @JSCodingConstructor(description = "Create identity matrix")
    public JSMatrix3f() {
        this.matrix = new Matrix3f();
    }

    @JSCodingConstructor(description = "Copy matrix", paramNames = {"other"})
    public JSMatrix3f(JSMatrix3f other) {
        this.matrix = new Matrix3f(other.matrix);
    }

    @JSCodingConstructor(description = "Wrap JOML matrix", paramNames = {"matrix"})
    public JSMatrix3f(Matrix3f matrix) {
        this.matrix = new Matrix3f(matrix);
    }

    @JSCodingFunctionOrMethod(description = "Set identity")
    public JSMatrix3f identity() {
        this.matrix.identity();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy matrix")
    public JSMatrix3f copy() {
        return new JSMatrix3f(this);
    }

    @JSCodingFunctionOrMethod(description = "Translate matrix", paramNames = {"vec"})
    public JSMatrix3f translate(JSVector3f vec) {
        this.matrix.transform(vec.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Rotate (radians)", paramNames = {"angle"})
    public JSMatrix3f rotateXYZ(JSVector3f angle) {
        this.matrix.rotateXYZ(angle.x(), angle.y(), angle.z());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Scale matrix", paramNames = {"vec"})
    public JSMatrix3f scale(JSVector3f vec) {
        this.matrix.scale(vec.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Multiply matrices", paramNames = {"other"})
    public JSMatrix3f mul(JSMatrix3f other) {
        this.matrix.mul(other.matrix);
        return this;
    }


    @JSCodingFunctionOrMethod(description = "Transform vector", paramNames = {"vec"})
    public JSVector2f transform(JSVector2f vec) {
        Vector3f tmp = new Vector3f(vec.x(), vec.y(), 1.0f);
        matrix.transform(tmp);
        return new JSVector2f(tmp.x, tmp.y);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying matrix")
    public Matrix3f getJavaMatrix3f() {
        return this.matrix;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return matrix.toString();
    }
}