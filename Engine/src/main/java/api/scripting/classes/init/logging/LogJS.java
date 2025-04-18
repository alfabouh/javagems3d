package api.scripting.classes.init.logging;

import api.scripting.doc.annotations.JSGlobalVar;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import logger.Log;

@JSGlobalVar(varName = "log")
@JSTypeDoc(description = "Logging manager", priority = JSTypeDoc.Priority.HIGH)
public final class LogJS {
    @JSMethodDoc(description = "Regular message", args = {"message"}, order = 0)
    public void info(String text) {
        Log.get().info(text);
    }

    @JSMethodDoc(description = "Trace message", args = {"message"}, order = 1)
    public void trace(String text) {
        Log.get().trace(text);
    }

    @JSMethodDoc(description = "Warning message", args = {"message"}, order = 2)
    public void warn(String text) {
        Log.get().warn(text);
    }

    @JSMethodDoc(description = "Error message", args = {"message"}, order = 3)
    public void error(String text) {
        Log.get().error(text);
    }
}