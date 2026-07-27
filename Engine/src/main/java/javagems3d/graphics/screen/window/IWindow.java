/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.screen.window;

import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public interface IWindow {
    long getDescriptor();
    Vector2i getWindowSize();
    boolean isWindowInFocus();
    void setTextCursor();
    void setArrowCursor();
    default boolean isFullScreen() {
        return GLFW.glfwGetWindowMonitor(this.getDescriptor()) != 0;
    }

    default boolean isWindowActive() {
        if (this.getWindowSize().x == 0 || this.getWindowSize().y == 0) {
            return false;
        }
        return GLFW.glfwGetWindowAttrib(this.getDescriptor(), GLFW.GLFW_ICONIFIED) == 0;
    }

    void setIcon(@Nullable JGemsPath iconPath, ISource.Source source);
    void setTitle(@NotNull String title);

    interface ResizeEvent {
        void onWindowResize(IWindow window);
    }
}
