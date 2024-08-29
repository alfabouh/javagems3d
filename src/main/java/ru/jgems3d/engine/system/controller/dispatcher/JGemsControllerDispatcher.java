/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.system.controller.dispatcher;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine.system.inventory.IInventoryOwner;
import ru.jgems3d.engine.physics.entities.properties.controller.IControllable;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.screen.window.IWindow;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.controller.objects.IController;
import ru.jgems3d.engine.system.controller.objects.MouseKeyboardController;

public class JGemsControllerDispatcher implements IControllerDispatcher {
    public static float CAM_SENS = 0.0015f;
    public static MouseKeyboardController mouseKeyboardController = null;
    private IController currentController;
    private IControllable currentControlledItem;
    private boolean lockController;

    public JGemsControllerDispatcher(Window window) {
        JGemsControllerDispatcher.mouseKeyboardController = new MouseKeyboardController(window, APIContainer.get().getApiGameInfo().getAppManager().createBindingManager());
        this.setController(JGemsControllerDispatcher.defaultController());
        JGemsHelper.getLogger().log("Created controller dispatcher!");
    }

    public static BindingManager bindingManager() {
        return JGems3D.get().getScreen().getControllerDispatcher().getCurrentController().getBindingManager();
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
        JGemsHelper.getLogger().log("Attached controller to: " + ((WorldItem) remoteController).getItemName());
        remoteController.setController(controller);
        this.currentControlledItem = remoteController;
    }

    public void detachController() {
        if (this.getCurrentControlledItem() != null) {
            JGemsHelper.getLogger().log("Detached Controller From: " + ((WorldItem) this.getCurrentControlledItem()).getItemName());
            this.getCurrentControlledItem().setController(null);
            this.currentControlledItem = null;
        }
    }

    public void setLockController(boolean lockController) {
        this.lockController = lockController;
    }

    public boolean isLockController() {
        return this.lockController;
    }

    public IControllable getCurrentControlledItem() {
        return this.currentControlledItem;
    }

    public void updateController(IWindow window) {
        if (this.isLockController()) {
            if (this.getCurrentControlledItem() != null) {
                this.getCurrentControlledItem().performController(new Vector2f(0.0f), new Vector3f(0.0f), false);
            }
            return;
        }
        if (this.getCurrentController() != null) {
            this.getCurrentController().updateControllerState(window);
            if (!JGems3D.get().isPaused()) {
                if (this.getCurrentControlledItem() != null) {
                    if (window.isInFocus() && this.getCurrentControlledItem() instanceof IInventoryOwner) {
                        this.getCurrentController().updateItemWithInventory(((IInventoryOwner) this.getCurrentControlledItem()));
                    }
                    this.performControllerToItem(window, this.getCurrentController(), this.getCurrentControlledItem());
                }
            }
        }
    }

    private void performControllerToItem(IWindow window, IController iController, IControllable iControllable) {
        Vector2f d1 = JGemsControllerDispatcher.getNormalizedRotationInput(iController);
        Vector3f d2 = JGemsControllerDispatcher.getNormalizedPositionInput(iController);
        iControllable.performController(d1, d2, window.isInFocus());
    }
}
