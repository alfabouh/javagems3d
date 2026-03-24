package api.scripting.coding.env.internal.util.events;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;

@JSCodingClass(binding = "JSEventI", description = "...")
public interface JSEventI {
    @JSHideFromDoc String name();
}
