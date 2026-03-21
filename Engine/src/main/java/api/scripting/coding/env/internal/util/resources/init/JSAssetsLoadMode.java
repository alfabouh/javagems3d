package api.scripting.coding.env.internal.util.resources.init;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;

@JSCodingClass(binding = "JSAssetsLoadPriority", description = "...")
public enum JSAssetsLoadMode {
    @JSCodingField(description = "ASYNC", paramName = "ASYNC") ASYNC,
    @JSCodingField(description = "REGULAR", paramName = "REGULAR") REGULAR;

    @JSHideFromDoc
    @Override
    public String toString() {
        return super.toString();
    }
}
