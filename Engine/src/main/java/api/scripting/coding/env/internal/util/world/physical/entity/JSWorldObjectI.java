package api.scripting.coding.env.internal.util.world.physical.entity;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.physics.world.basic.IWorldObject;

@JSCodingClass(binding = "JSWorldObjectI", description = "Interface for world objects, exposing access to the underlying Java world object.")
public interface JSWorldObjectI {
    @JSCodingFunctionOrMethod(description = "Get the underlying Java world item (IWorldObject).")
    IWorldObject getJavaWorldObject();
}