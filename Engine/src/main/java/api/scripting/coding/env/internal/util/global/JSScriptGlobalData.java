package api.scripting.coding.env.internal.util.global;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.screen.JSScreen;
import api.scripting.coding.env.internal.util.settings.JSGameSettings;
import javagems3d.system.service.files.JGemsPath;

@JSCodingClass(binding = "JSScriptGlobalData", description = "...")
public final class JSScriptGlobalData implements JSGlobalVarFactory<JSScriptGlobalData> {
    @JSHideFromDoc public static JSPath absPath;
    @JSHideFromDoc public static JSScreen jsScreen;
    @JSHideFromDoc public static JSGameSettings jsGameSettings;

    //@JSHideFromDoc
    public JSScriptGlobalData() {
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSPath getGameFolderPath() {
        return JSScriptGlobalData.absPath;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public static JSGameSettings getGameSettings() {
        return JSScriptGlobalData.jsGameSettings;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSScreen getScreen() {
        return JSScriptGlobalData.jsScreen;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isScreenValid() {
        return JSScriptGlobalData.jsScreen != null;
    }

    @JSHideFromDoc
    public static void setSettings(JSGameSettings settings) {
        JSScriptGlobalData.jsGameSettings = settings;
    }

    @JSHideFromDoc
    public static void setScreen(JSScreen jsScreen) {
        JSScriptGlobalData.jsScreen = jsScreen;
    }

    @JSHideFromDoc
    public static void dispose() {
        JSScriptGlobalData.jsScreen = null;
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
