package api.scripting.coding.env.internal.util.events;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;

@JSCodingClass(binding = "JSObjectState", description = "Event object state enum.")
public enum JSObjectState {
    @JSCodingField(description = "SPAWN") SPAWN,
    @JSCodingField(description = "POST") DESTROY;
}