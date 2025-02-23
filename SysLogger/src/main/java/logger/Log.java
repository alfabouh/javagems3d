package logger;

import logger.managers.LoggingManager;

public abstract class Log {
    public static LoggingManager get() {
        return SystemLogging.get().getLogManager();
    }
}
