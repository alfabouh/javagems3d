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

package api.scripting.coding.env.internal.util.world.render;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.world.render.screen.JSWindow;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import javagems3d.graphics.rendering.scene.JGemsScene;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.graphics.world.SceneWorld;

@JSCodingClass(binding = "JSScene", description = "Wrapper for JGemsScene providing access to rendering and window.")
public class JSScene {
    private final JGemsScreen gemsScreen;

    @JSHideFromDoc
    public JSScene(JGemsScreen screen) {
        this.gemsScreen = screen;
    }

    @JSCodingFunctionOrMethod(description = "Get associated window")
    public JSSceneWorld getSceneWorld() {
        return new JSSceneWorld((SceneWorld) this.gemsScreen.getSceneWorld());
    }

    @JSCodingFunctionOrMethod(description = "Get associated window")
    public JSWindow getWindow() {
        return new JSWindow(this.gemsScreen.getWindow());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java scene object")
    public JGemsScene getJavaScene() {
        return this.gemsScreen.getScene();
    }
}