package api.scripting.coding.env.internal.util.controlling;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.physics.entities.properties.controller.IControllable;

@JSCodingClass(binding = "JSControllableItem", description = "Controllable marker")
public interface JSControllableItem {
    @JSCodingFunctionOrMethod(description = "Real java object") IControllable getJavaControllable();
}
