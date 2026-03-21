package api.scripting.coding.env.internal.map;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSEntryPointSampleClass;
import api.scripting.coding.env.functions.JavaToJSFunctionsList;

@JSEntryPointSampleClass
@JSCodingClass(binding = "entrypoint", description = "entrypoint")
public class JSMapSampleClass {

    @JSCodingFunctionOrMethod(description = JavaToJSFunctionsList.ENTRY_POINT_FUNCTION_DESC)
    public void JsInit() {
    }
}
