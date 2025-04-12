package api.scripting.classes.init.logging;

import logger.Log;

public final class LogJS {
    public void info(String text) {
        Log.get().info(text);
    }

    public void trace(String text) {
        Log.get().trace(text);
    }

    public void warn(String text) {
        Log.get().warn(text);
    }

    public void error(String text) {
        Log.get().error(text);
    }
}
