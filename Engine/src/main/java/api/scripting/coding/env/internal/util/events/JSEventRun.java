package api.scripting.coding.env.internal.util.events;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;

@JSCodingClass(binding = "JSEventRun", description = "Event run enum.")
public enum JSEventRun {
    @JSCodingField(description = "PRE") PRE,
    @JSCodingField(description = "POST") POST;
}