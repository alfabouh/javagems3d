package logger;

import logger.translators.StreamOutputTranslation;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import logger.managers.JGemsLogging;
import logger.managers.LoggingManager;

import java.io.IOException;
import java.io.PrintStream;

public final class SystemLogging {
    private static final SystemLogging INSTANCE = new SystemLogging();
    private LoggingManager currentLogging;

    public SystemLogging() {
        this.currentLogging = null;
    }

    public static SystemLogging get() {
        return SystemLogging.INSTANCE;
    }

    public void setCurrentLogging(LoggingManager currentLogging) throws IOException {
        this.currentLogging = currentLogging;
        this.initStreams(currentLogging.getLog());
    }

    private void initStreams(final Logger log) throws IOException {
        try (StreamOutputTranslation streamOutputTranslation = new StreamOutputTranslation(false, log)) {
            System.setOut(new PrintStream(streamOutputTranslation, true));
        }
        try (StreamOutputTranslation streamOutputTranslation = new StreamOutputTranslation(true, log)) {
            System.setErr(new PrintStream(streamOutputTranslation, true));
        }
    }

    public @NotNull LoggingManager getLogManager() {
        return this.currentLogging;
    }
}
