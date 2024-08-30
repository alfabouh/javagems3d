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

package javagems3d.logger;

import org.jetbrains.annotations.NotNull;
import javagems3d.logger.managers.JGemsLogging;
import javagems3d.logger.managers.LoggingManager;

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
    }

    public @NotNull LoggingManager getLogManager() {
        return this.currentLogging;
    }
}
