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

package api.scripting.coding.env.internal.game.init.events.rendering.ui;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.screen.JSScreen;
import api.scripting.coding.env.internal.util.ui.JSUIPanelWrapper;
import api.scripting.JavaToJsAPI;
import javagems3d.system.service.collections.Pair;

@JSCodingClass(binding = "JSRegisterUiEvent", description = "Event for registering UI panels in the game, including main menu and custom panels.")
public class JSRegisterUiEvent implements JSEventI {
    @JSHideFromDoc private JSScreen jsScreen;

    @JSCodingConstructor(description = "Creates a new JSRegisterUiEvent instance.")
    public JSRegisterUiEvent() {
    }

    @JSHideFromDoc
    public JSRegisterUiEvent(JSScreen jsScreen) {
        this.jsScreen = jsScreen;
    }

    @JSCodingFunctionOrMethod(description = "Register a new UI panel by its unique name.", paramNames = {"uniqueName"})
    public void registerPanel(String uniqueName) {
        JavaToJsAPI.uiContainer.setPanelUI(uniqueName, new JSUIPanelWrapper(uniqueName));
    }

    @JSCodingFunctionOrMethod(description = "Register the main menu panel with a unique name.", paramNames = {"uniqueName"})
    public void registerMainMenuPanel(String uniqueName) {
        JavaToJsAPI.uiContainer.setMainMenuPanel(new Pair<>(uniqueName, new JSUIPanelWrapper(uniqueName)));
    }

    @JSCodingFunctionOrMethod(description = "Get the screen associated with this UI registration event.")
    public JSScreen getScreen() {
        return this.jsScreen;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSRegisterUiEvent";
    }
}