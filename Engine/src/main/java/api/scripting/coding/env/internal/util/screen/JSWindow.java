package api.scripting.coding.env.internal.util.screen;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.service.files.source.ISource;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSWindow", description = "...")
public class JSWindow {
    @JSHideFromDoc private final IWindow window;

    @JSHideFromDoc
    public JSWindow(IWindow window) {
        this.window = window;
    }

    @JSCodingFunctionOrMethod(description = "Returns native window descriptor (platform-dependent handle).")
    public long getDescriptor() {
        return this.window.getDescriptor();
    }

    @JSCodingFunctionOrMethod(description = "Returns current window size as a 2D vector (width, height).")
    public JSVector2f getWindowSize() {
        return new JSVector2f(this.window.getWindowSize().x, this.window.getWindowSize().y);
    }

    @JSCodingFunctionOrMethod(description = "Checks if the window is currently focused (has input focus).")
    public boolean isWindowInFocus() {
        return this.window.isWindowInFocus();
    }

    @JSCodingFunctionOrMethod(description = "Checks if the window is in fullscreen mode.")
    public boolean isFullScreen() {
        return this.window.isFullScreen();
    }

    @JSCodingFunctionOrMethod(description = "Checks if the window is active (not minimized and responsive).")
    public boolean isWindowActive() {
        return this.window.isWindowActive();
    }

    @JSCodingFunctionOrMethod(description = "Sets the window icon from a file path. Path must point to an image outside the JAR.")
    public void setIcon(JSPath iconPath) {
        this.window.setIcon(iconPath.getJavaPath(), ISource.Source.OUTSIDE_JAR);
    }

    @JSCodingFunctionOrMethod(description = "Sets the window title text.")
    public void setTitle(@NotNull String title) {
        this.window.setTitle(title);
    }
}
