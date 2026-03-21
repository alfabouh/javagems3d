package api.scripting.coding.env.internal.util.resources.init;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.resources.JSResourcesManager;

@JSCodingClass(binding = "JSAssetsInitializer", description = "...")
public abstract class JSAssetsInitializer {
    @JSCodingFunctionOrMethod(description = "...") public abstract void run(JSResourcesManager resourcesManager);

    @JSCodingFunctionOrMethod(description = "...") public abstract JSAssetsLoadMode loadMode();
    @JSCodingFunctionOrMethod(description = "...") public abstract JSAssetsLoadPriority loadPriority();
}
