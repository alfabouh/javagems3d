package api.scripting.coding.env.internal.util.events;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;

@JSCodingClass(binding = "JSEventCancellableI", description = "Base interface for all JavaScript events used in the game scripting system. Can be cancelled. Cancellation turns off original code run")
public interface JSEventCancellableI extends JSEventI {
    @JSCodingFunctionOrMethod(description = "Check whether this event is cancelled.")
    boolean isCancelled();
    @JSCodingFunctionOrMethod(description = "Set event cancellation state. If true, default behavior will not execute.", paramNames = {"cancelled"})
    void setCancelled(boolean cancelled);
}