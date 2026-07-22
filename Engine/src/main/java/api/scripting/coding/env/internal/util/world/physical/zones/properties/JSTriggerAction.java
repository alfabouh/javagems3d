package api.scripting.coding.env.internal.util.world.physical.zones.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.physics.world.triggers.ITriggerAction;

@JSCodingClass(binding = "JSContactAction", description = "Callbacks executed for physics contact events.")
public interface JSTriggerAction {

    @JSCodingFunctionOrMethod(description = "Called when a contact starts.", paramNames = {"object"})
    default void contactStarted(Object object, long pointId) {
    }

    @JSCodingFunctionOrMethod(description = "Called while objects remain in contact.", paramNames = {"object"})
    default void contactContinue(Object object, long manifoldId) {
    }

    @JSCodingFunctionOrMethod(description = "Called when a contact ends.", paramNames = {"object"})
    default void contactEnded(Object object, long manifoldId) {
    }

    @JSCodingFunctionOrMethod(description = "Called when a contact point is created. Return false to reject the contact.", paramNames = {"object", "pointID", "manifoldID"})
    default boolean contactPointCreated(Object object, long pointID, long manifoldID) {
        return true;
    }

    @JSHideFromDoc
    default ITriggerAction toJava() {
        return new ITriggerAction() {
            @Override
            public void contactContinue(Object userObject, long pointId) {
                JSTriggerAction.this.contactContinue(userObject, pointId);
            }

            @Override
            public void contactStarted(Object userObject, long manifoldID) {
                JSTriggerAction.this.contactStarted(userObject, manifoldID);
            }

            @Override
            public void contactEnded(Object userObject, long manifoldID) {
                JSTriggerAction.this.contactEnded(userObject, manifoldID);
            }

            @Override
            public boolean contractPointCreated(Object userObject, long pointID, long manifoldID) {
                return JSTriggerAction.this.contactPointCreated(userObject, pointID, manifoldID);
            }
        };
    }
}