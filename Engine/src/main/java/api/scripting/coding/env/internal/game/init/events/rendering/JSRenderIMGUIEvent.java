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

package api.scripting.coding.env.internal.game.init.events.rendering;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.controlling.JSController;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.misc.JSFrameTicking;
import api.scripting.coding.env.internal.util.ui.JSImGui;
import api.scripting.coding.env.internal.util.ui.JSUIDrawer;
import api.scripting.coding.env.internal.util.world.render.screen.JSScreen;
import imgui.ImGui;

@JSCodingClass(binding = "JSRenderIMGUIEvent", description = "...")
public class JSRenderIMGUIEvent implements JSEventI {
    @JSCodingField(description = "Cancellation flag")
    private boolean cancel;

    @JSCodingField(description = "...")
    public JSScreen screen;

    @JSCodingField(description = "...")
    public JSFrameTicking frameTicking;

    @JSCodingField(description = "...")
    public JSController controller;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSRenderIMGUIEvent() {
    }

    public JSRenderIMGUIEvent(JSScreen screen, JSFrameTicking frameTicking, JSController controller) {
        this.screen = screen;
        this.frameTicking = frameTicking;
        this.controller = controller;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSScreen getScreen() {
        return this.screen;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSFrameTicking getFrameTicking() {
        return this.frameTicking;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSController getController() {
        return this.controller;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSRenderUIEvent";
    }
}
