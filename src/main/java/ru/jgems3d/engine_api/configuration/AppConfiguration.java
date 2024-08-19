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

package ru.jgems3d.engine_api.configuration;

import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.system.service.path.JGemsPath;

/**
 * Game Configuration. You can configure this yourself, or create a default:
 * <pre>
 *     {@code
 *     AppConfiguration.createDefaultAppConfiguration()
 *     }
 * </pre>
 */
public class AppConfiguration {
    private final JGemsPath windowIcon;

    public AppConfiguration(JGemsPath windowIcon) {
        this.windowIcon = windowIcon;
    }

    public static AppConfiguration createDefaultAppConfiguration() {
        return new AppConfiguration(new JGemsPath("/assets/jgems/icons/icon.png"));
    }

    /**
     * Window icon.
     *
     * @return the window icon
     */
    public @Nullable JGemsPath getWindowIcon() {
        return this.windowIcon;
    }
}
