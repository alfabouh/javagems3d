package api.scripting.coding.env.internal.util.world.physical.zones.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.physics.world.triggers.ITriggerAction;

@JSCodingClass(binding = "JSTriggerAction", description = "Callback executed when object enters trigger zone.")
@FunctionalInterface
public interface JSTriggerAction {

    @JSCodingFunctionOrMethod(description = "Called when collision happens", paramNames = {"object"})
    void action(Object object);

    @JSHideFromDoc
    default ITriggerAction toJava() {
        return this::action;
    }
}