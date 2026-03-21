package api.scripting.coding.env.internal.util.management;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.misc.JSString;
import logger.Log;

@JSCodingClass(binding = "JSLogger", description = "Logging tools.")
public class JSLogger implements JSGlobalVarFactory<JSLogger> {
    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the INFO level.", paramNames = {"message"})
    public void info(JSString message) {
        Log.get().info(message.string());
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the ERROR level.", paramNames = {"message"})
    public void error(JSString message) {
        Log.get().error(message.string());
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the WARN level.", paramNames = {"message"})
    public void warn(JSString message) {
        Log.get().warn(message.string());
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the TRACE level.", paramNames = {"message"})
    public void trace(JSString message) {
        Log.get().trace(message.string());
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the DEBUG level.", paramNames = {"message"})
    public void debug(JSString message) {
        Log.get().debug(message.string());
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
