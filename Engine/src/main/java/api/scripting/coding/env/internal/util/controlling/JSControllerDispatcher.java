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

package api.scripting.coding.env.internal.util.controlling;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.render.screen.JSWindow;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;

@JSCodingClass(binding = "JSControllerDispatcher", description = "JS wrapper for controller dispatcher. Handles attaching/detaching controllers and normalized input access.")
public class JSControllerDispatcher {
    @JSHideFromDoc
    private final JGemsControllerDispatcher dispatcher;

    @JSCodingConstructor(description = "Wraps a JGemsControllerDispatcher instance.", paramNames = {"dispatcher"})
    public JSControllerDispatcher(JGemsControllerDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @JSCodingFunctionOrMethod(description = "Returns the current controller.")
    public JSMouseKeyboardController getCurrentController() {
        IController c = this.dispatcher.getCurrentController();
        if (c instanceof MouseKeyboardController mouseCtrl) {
            return new JSMouseKeyboardController(mouseCtrl);
        }
        return null;
    }

    @JSCodingFunctionOrMethod(description = "Attaches a controller to a controllable object.")
    public void attachControllerTo(JSMouseKeyboardController controller, JSControllableItem controlledItem) {
        this.dispatcher.attachControllerTo(controller.getJavaController(), controlledItem.getJavaControllable());
    }

    @JSCodingFunctionOrMethod(description = "Detaches the currently controlled object.")
    public void detachController() {
        this.dispatcher.detachController();
    }

    @JSCodingFunctionOrMethod(description = "Returns true if controller is locked.")
    public boolean isControllerLocked() {
        return this.dispatcher.isControllerLocked();
    }

    @JSCodingFunctionOrMethod(description = "Sets lock state for the controller.")
    public void setLock(boolean lock) {
        this.dispatcher.setLock(lock);
    }

    @JSCodingFunctionOrMethod(description = "Returns the currently controlled item.")
    public JSWorldItem getCurrentControlledItem() {
        IControllable item = this.dispatcher.getCurrentControlledItem();
        if (item instanceof WorldItem worldItem) {
            return new JSWorldItem(worldItem);
        }
        return null;
    }

    @JSCodingFunctionOrMethod(description = "Updates the controller state for the given window.")
    public void updateController(JSWindow window) {
        this.dispatcher.updateController(window.getJavaWindow());
    }

    @JSHideFromDoc
    public JGemsControllerDispatcher getJavaDispatcher() {
        return this.dispatcher;
    }
}