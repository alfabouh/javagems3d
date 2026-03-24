package api.scripting.coding.env.internal.sample;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import logger.Log;

@JSCodingClass(binding = "JSSample", description = "...")
public class JSSample {
    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public void fun() {
    }
}
