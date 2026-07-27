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

package workbench.controller;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.dispatcher.IControllerDispatcher;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.base.MouseKeyboardController;
import logger.SystemLogging;
import org.joml.Vector2f;
import org.joml.Vector3f;
import workbench.controller.binding.WBenchBindingManager;
import workbench.controller.objects.WBenchMouseKeyboardController;

public class WBenchControllerDispatcher implements IControllerDispatcher {
    private final MouseKeyboardController mouseKeyboardController;

    public WBenchControllerDispatcher(IWindow window) {
        this.mouseKeyboardController = new WBenchMouseKeyboardController(window, new WBenchBindingManager());
        SystemLogging.get().getLogManager().info("Created controller dispatcher");
    }

    public static Vector2f getNormalizedRotationInput(IController iController) {
        return iController.getNormalizedRotationInput();
    }

    public static Vector3f getNormalizedPositionInput(IController iController) {
        return iController.getNormalizedPositionInput();
    }

    public void updateController(IWindow window) {
        if (this.getCurrentController() != null) {
            this.getCurrentController().updateControllerState(window);
        }
    }

    public MouseKeyboardController getCurrentController() {
        return this.mouseKeyboardController;
    }

    @Override
    public void setController(IController iController) {
    }
}
