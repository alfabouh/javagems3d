package api.scripting.coding.env.internal.util.world.physical.entity;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.WorldItem;

@JSCodingClass(binding = "JSWorldItemI", description = "...")
public interface JSWorldItemI {
    @JSCodingFunctionOrMethod(description = "...")
    IWorldObject getJavaWorldItem();
}
