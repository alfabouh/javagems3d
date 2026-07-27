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

package api.scripting.coding.env.internal.util.resources.instances.shaders;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.*;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformProgram;

@JSCodingClass(binding = "JSUniformFunc", description = "Wrapper for setting shader uniform values.")
public class JSUniformFunc {
    private final UniformProgram.UFunction uFunction;

    private JSUniformFunc(UniformProgram.UFunction uFunction) {
        this.uFunction = uFunction;
    }

    @JSCodingFunctionOrMethod(description = "Create integer uniform function", paramNames = {"value"})
    public static JSUniformFunc integer(int value) {
        return new JSUniformFunc(UniformFunctions.INTEGER(value));
    }

    @JSCodingFunctionOrMethod(description = "Create float uniform function", paramNames = {"value"})
    public static JSUniformFunc floatVal(float value) {
        return new JSUniformFunc(UniformFunctions.FLOAT(value));
    }

    @JSCodingFunctionOrMethod(description = "Create vec2 uniform function", paramNames = {"value"})
    public static JSUniformFunc vec2(JSVector2f value) {
        return new JSUniformFunc(UniformFunctions.VEC2F(value.getJavaVector2f()));
    }

    @JSCodingFunctionOrMethod(description = "Create vec3 uniform function", paramNames = {"value"})
    public static JSUniformFunc vec3(JSVector3f value) {
        return new JSUniformFunc(UniformFunctions.VEC3F(value.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Create vec4 uniform function", paramNames = {"value"})
    public static JSUniformFunc vec4(JSVector4f value) {
        return new JSUniformFunc(UniformFunctions.VEC4F(value.getJavaVector4f()));
    }

    @JSCodingFunctionOrMethod(description = "Create mat4 uniform function", paramNames = {"value"})
    public static JSUniformFunc mat4(JSMatrix4f value) {
        return new JSUniformFunc(UniformFunctions.MAT4F(value.getJavaMatrix4f()));
    }

    @JSCodingFunctionOrMethod(description = "Create mat3 uniform function", paramNames = {"value"})
    public static JSUniformFunc mat3(JSMatrix3f value) {
        return new JSUniformFunc(UniformFunctions.MAT3F(value.getJavaMatrix3f()));
    }

    @JSCodingFunctionOrMethod(description = "Get underlying UFunction instance")
    public UniformProgram.UFunction getJavaUFunction() {
        return this.uFunction;
    }
}