/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package logger;

import javagems3d.engine.system.service.exceptions.JGemsIOException;
import logger.translators.StreamOutputTranslation;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import logger.managers.JGemsLogging;
import logger.managers.LoggingManager;

import java.io.IOException;
import java.io.PrintStream;

public final class SystemLogging {
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
        try {
            this.initStreams(currentLogging.getLog());
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
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
