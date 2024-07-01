package ru.alfabouh.jgems3d.logger;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.logger.managers.JGemsLogging;
import ru.alfabouh.jgems3d.logger.managers.LoggingManager;

public class SystemLogging {
    private static final SystemLogging INSTANCE = new SystemLogging();

    public static final LoggingManager jGemsLogging = new JGemsLogging("JGemsLogger");
    public static final LoggingManager toolBoxLogging = new JGemsLogging("ToolBoxLogger");


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
