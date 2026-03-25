package api.scripting.coding.env.internal.util.management;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import logger.Log;

@JSCodingClass(binding = "JSLogger", description = "Logging tools.")
public class JSLogger implements JSGlobalVarFactory<JSLogger> {
    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the INFO level.", paramNames = {"message"})
    public void info(String message) {
        Log.get().info(message);
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the ERROR level.", paramNames = {"message"})
    public void error(String message) {
        Log.get().error(message);
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the WARN level.", paramNames = {"message"})
    public void warn(String message) {
        Log.get().warn(message);
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the TRACE level.", paramNames = {"message"})
    public void trace(String message) {
        Log.get().trace(message);
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the DEBUG level.", paramNames = {"message"})
    public void debugMsg(String message) {
        Log.get().debug(message);
    }

    @JSHideFromDoc
    @Override
    public JSLogger newGlobalVar() {
        return new JSLogger();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "JSLog";
    }
}
