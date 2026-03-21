package api.scripting.coding.env.internal.util.resources.instances.shaders;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;

@JSCodingClass(binding = "JSShaderVar", description = "Wrapper for shader uniform variable.")
public class JSShaderVar {
    private final UniformString uniformString;

    @JSCodingConstructor(description = "Create shader uniform variable from name", paramNames = {"name"})
    public JSShaderVar(String name) {
        this.uniformString = new UniformString(name);
    }

    @JSCodingConstructor(description = "Create shader uniform variable with array index", paramNames = {"name", "index"})
    public JSShaderVar(String name, int index) {
        this.uniformString = new UniformString(name, index);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying UniformString instance")
    public UniformString getJavaUniformString() {
        return this.uniformString;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return this.uniformString.toString();
    }
}