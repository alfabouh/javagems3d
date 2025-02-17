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

package javagems3d.graphics.screen.window;

import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public interface IWindow {
    long getDescriptor();
    Vector2i getWindowSize();
    boolean isWindowInFocus();

    default boolean isWindowActive() {
        if (this.getWindowSize().x == 0 || this.getWindowSize().y == 0) {
            return false;
        }
        return GLFW.glfwGetWindowAttrib(this.getDescriptor(), GLFW.GLFW_ICONIFIED) == 0;
    }

    void setIcon(@Nullable JGemsPath iconPath);
    void setTitle(@NotNull String title);

    interface ResizeEvent {
        void onWindowResize(IWindow window);
    }
}
