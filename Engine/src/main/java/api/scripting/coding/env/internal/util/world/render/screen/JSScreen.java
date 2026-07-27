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
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.world.render.JSScene;
import api.scripting.coding.env.internal.util.world.render.screen.timer.JSTimerPool;
import javagems3d.graphics.screen.JGemsScreen;

@JSCodingClass(binding = "JSScreen", description = "Represents a screen with rendering, window, and timer functionality.")
public class JSScreen {
    @JSHideFromDoc private final JGemsScreen screen;
    private final JSTimerPool timerPool;
    private final JSScene jsScene;

    @JSHideFromDoc
    public JSScreen(JGemsScreen screen) {
        this.screen = screen;
        this.timerPool = new JSTimerPool(screen.getTimerPool());
        this.jsScene = new JSScene(screen);
    }

    @JSCodingFunctionOrMethod(description = "Switches between screen modes.")
    public void switchScreenMode() {
        this.screen.switchScreenMode();
    }

    @JSCodingFunctionOrMethod(description = "Refreshes scene resources.")
    public void refreshSceneResources() {
        this.screen.refreshSceneResources();
    }

    @JSCodingFunctionOrMethod(description = "Returns the current render ticks.")
    public float getRenderTicks() {
        return this.screen.getRenderTicks();
    }

    @JSCodingFunctionOrMethod(description = "Get window dimensions as a vector.")
    public JSVector2f getWindowDimensions() {
        return new JSVector2f(this.screen.getWindowDimensions().x, this.screen.getWindowDimensions().y);
    }

    @JSCodingFunctionOrMethod(description = "Get rendering scene.")
    public JSScene getJsScene() {
        return this.jsScene;
    }

    @JSCodingFunctionOrMethod(description = "Get window instance.")
    public JSWindow getWindow() {
        return new JSWindow(this.screen.getWindow());
    }

    @JSCodingFunctionOrMethod(description = "Get timer pool.")
    public JSTimerPool getTimerPool() {
        return this.timerPool;
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public JGemsScreen getJavaScreen() {
        return this.screen;
    }
}