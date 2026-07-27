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

package javagems3d.system.controller.dispatcher;

import api.system.JGemsAPI;
import javagems3d.system.controller.JGemsMouseKeyboardController;
import javagems3d.system.controller.binding.BindingManager;
import logger.Log;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.base.MouseKeyboardController;

public class JGemsControllerDispatcher implements IControllerDispatcher {
    private static MouseKeyboardController mouseKeyboardController = null;
    private IController currentController;
    private IControllable currentControlledItem;
    private boolean lockController;

    public JGemsControllerDispatcher(IWindow window, BindingManager bindingManager) {
        JGemsControllerDispatcher.mouseKeyboardController = new JGemsMouseKeyboardController(window, bindingManager);
        this.setController(JGemsControllerDispatcher.defaultController());
        Log.get().info("Created controller dispatcher");
    }

    public static IController defaultController() {
        return JGemsControllerDispatcher.mouseKeyboardController;
    }

    public static Vector2f getNormalizedRotationInput(IController iController) {
        return iController.getNormalizedRotationInput();
    }

    public static Vector3f getNormalizedPositionInput(IController iController) {
        return iController.getNormalizedPositionInput();
    }

    public void setController(IController iController) {
        this.currentController = iController;
    }

    public IController getCurrentController() {
        return this.currentController;
    }

    public void attachControllerTo(IController controller, IControllable remoteController) {
        Log.get().debug("Attached controller to: " + ((WorldItem) remoteController).getItemName());
        remoteController.setController(controller);
        this.currentControlledItem = remoteController;
    }

    public void detachController() {
        if (this.getCurrentControlledItem() != null) {
            Log.get().debug("Detached Controller From: " + ((WorldItem) this.getCurrentControlledItem()).getItemName());
            this.getCurrentControlledItem().setController(null);
            this.currentControlledItem = null;
        }
    }

    public boolean isControllerLocked() {
        return this.lockController;
    }

    public void setLock(boolean lockController) {
        this.lockController = lockController;
    }

    public IControllable getCurrentControlledItem() {
        return this.currentControlledItem;
    }

    public void updateController(IWindow window) {
        if (this.isControllerLocked()) {
            if (this.getCurrentControlledItem() != null) {
                this.getCurrentControlledItem().performController(new Vector2f(0.0f), new Vector3f(0.0f), false);
            }
            return;
        }
        if (this.getCurrentController() != null) {
            this.getCurrentController().updateControllerState(window);
            if (!JGems3D.get().isPaused()) {
                if (this.getCurrentControlledItem() != null) {
                    this.performControllerToItem(window, this.getCurrentController(), this.getCurrentControlledItem());
                }
            }
        }
    }

    private void performControllerToItem(IWindow window, IController iController, IControllable iControllable) {
        Vector2f d1 = JGemsControllerDispatcher.getNormalizedRotationInput(iController);
        Vector3f d2 = JGemsControllerDispatcher.getNormalizedPositionInput(iController);
        iControllable.performController(d1, d2, window.isWindowInFocus());
    }
}
