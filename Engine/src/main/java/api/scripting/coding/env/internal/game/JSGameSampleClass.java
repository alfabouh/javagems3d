package api.scripting.coding.env.internal.game;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSEntryPointSampleClass;
import api.scripting.coding.env.functions.JavaToJSFunctionsList;

@JSEntryPointSampleClass
@JSCodingClass(binding = "entrypoint", description = "entrypoint")
public class JSGameSampleClass {

    @JSCodingFunctionOrMethod(description = JavaToJSFunctionsList.ENTRY_POINT_FUNCTION_DESC)
    public void JsInit() {
    }
}
