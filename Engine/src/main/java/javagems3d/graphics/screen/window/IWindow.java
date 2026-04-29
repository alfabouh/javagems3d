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
