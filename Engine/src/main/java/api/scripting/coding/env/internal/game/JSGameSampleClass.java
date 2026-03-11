package api.scripting.coding.env.internal.game;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSEntryPointClass;

@JSEntryPointClass
@JSCodingClass(binding = "entrypoint", description = "entrypoint")
public class JSGameSampleClass {

    @JSCodingFunctionOrMethod(description = "StartUp")
    public void startUp() {
    }
}
