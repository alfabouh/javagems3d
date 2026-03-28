package api.scripting.coding.env.internal.util.world.render.lighting;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.graphics.environment.lights.Light;

@JSCodingClass(binding = "JSLightI", description = "General interface for Light wrappers")
public interface JSLightI {
    @JSCodingFunctionOrMethod(description = "Underlying Java Light object")
    Light getJavaLight();
}