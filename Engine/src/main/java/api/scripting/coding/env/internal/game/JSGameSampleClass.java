package api.scripting.coding.env.internal.game;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSEntryPointSampleClass;
import api.scripting.coding.env.internal.game.init.JSGameRegistry;
import api.scripting.JavaToJsFunctionsList;

@JSEntryPointSampleClass
@JSCodingClass(binding = "entrypoint", description = "entrypoint")
public class JSGameSampleClass {

    @JSCodingFunctionOrMethod(description = JavaToJsFunctionsList.ENTRY_POINT_FUNCTION_DESC)
    public void JsInit() {
    }

    @JSCodingFunctionOrMethod(description = JavaToJsFunctionsList.SUBSCRIBE_EVENTS_FUNCTION_DESC, paramNames = {"jsGameRegistry"})
    public void JsSubscribeEvents(JSGameRegistry jsGameRegistry) {
    }

    @JSCodingFunctionOrMethod(description = JavaToJsFunctionsList.ENTRY_ENDPOINT_FUNCTION_DESC)
    public void JsEnd() {
    }
}
