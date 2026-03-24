package api.scripting.coding.env.internal.map;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSEntryPointSampleClass;
import api.system.scripting.JavaToJsFunctionsList;

@JSEntryPointSampleClass
@JSCodingClass(binding = "entrypoint", description = "entrypoint")
public class JSMapSampleClass {

    @JSCodingFunctionOrMethod(description = JavaToJsFunctionsList.ENTRY_POINT_FUNCTION_DESC)
    public void JsInit() {
    }
}
