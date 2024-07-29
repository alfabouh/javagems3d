package ru.jgems3d.logger;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.logger.managers.JGemsLogging;
import ru.jgems3d.logger.managers.LoggingManager;

public class SystemLogging {
    public static final LoggingManager jGemsLogging = new JGemsLogging("JGemsLogger");
    public static final LoggingManager toolBoxLogging = new JGemsLogging("ToolBoxLogger");
    private static final SystemLogging INSTANCE = new SystemLogging();
    private LoggingManager currentLogging;

    public SystemLogging() {
        this.currentLogging = null;
    }

    public static SystemLogging get() {
        return SystemLogging.INSTANCE;
    }

    public void setCurrentLogging(LoggingManager currentLogging) {
        this.currentLogging = currentLogging;
    }

    public @NotNull LoggingManager getLogManager() {
        return this.currentLogging;
    }
}
