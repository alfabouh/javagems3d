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

package api.scripting.coding.env.internal.util.world.render.screen;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.service.files.source.ISource;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSWindow", description = "Wrapper for window operations and properties.")
public class JSWindow {
    @JSHideFromDoc
    private final IWindow window;

    @JSCodingConstructor(description = "Create a JSWindow wrapper from a native IWindow instance", paramNames = {"window"})
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

    @JSCodingFunctionOrMethod(description = "Sets the window icon from a given path. Path must point to an image outside the JAR.", paramNames = {"iconPath"})
    public void setIcon(JSPath iconPath) {
        this.window.setIcon(iconPath.getJavaPath(), ISource.Source.OUTSIDE_JAR);
    }

    @JSCodingFunctionOrMethod(description = "Sets the window title text.", paramNames = {"title"})
    public void setTitle(@NotNull String title) {
        this.window.setTitle(title);
    }

    @JSCodingFunctionOrMethod(description = "Real java object.")
    public IWindow getJavaWindow() {
        return this.window;
    }
}