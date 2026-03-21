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