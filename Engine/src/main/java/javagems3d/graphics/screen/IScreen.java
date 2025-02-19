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

package javagems3d.graphics.screen;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IScreen {
    void createObjects(IWindow window);
    void createScreenAndContext();
    void runRenderThread();

    IWindow getWindow();

    default void setIcon(@Nullable JGemsPath icon) {
        this.getWindow().setIcon(icon);
    }

    default void setTitle(@NotNull String title) {
        this.getWindow().setTitle(title);
    }
}
