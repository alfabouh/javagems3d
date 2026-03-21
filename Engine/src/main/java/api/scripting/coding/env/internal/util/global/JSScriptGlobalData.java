package api.scripting.coding.env.internal.util.global;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.management.JSPath;
import javagems3d.system.service.files.JGemsPath;

@JSCodingClass(binding = "JSScriptGlobalData", description = "...")
public final class JSScriptGlobalData implements JSGlobalVarFactory<JSScriptGlobalData> {
    @JSHideFromDoc
    private static JSPath absPath;

    //@JSHideFromDoc
    public JSScriptGlobalData() {
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSPath getGameFolderPath() {
        return JSScriptGlobalData.absPath;
    }

    @JSHideFromDoc
    public static void setAbsoluteSystemPath(JGemsPath path) {
        JSScriptGlobalData.absPath = new JSPath(path);
    }

    @JSHideFromDoc
    @Override
    public JSScriptGlobalData newGlobalVar() {
        return new JSScriptGlobalData();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "JSGlobal";
    }
}
