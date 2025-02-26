package javagems3d.help;

import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import logger.Log;

public abstract class JGemsControllerHelper {
    public static boolean setCursorInCenter() {
        IController controller = getCurrentController();
        if (controller instanceof MouseKeyboardController) {
            MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
            mouseKeyboardController.setCursorInCenter();
            return true;
        }
        Log.get().warn("Couldn't find cursor. Check your controller");
        return false;
    }

    public static void attachControllerTo(IController controller, IControllable remoteController) {
        getControllerDispatcher().attachControllerTo(controller, remoteController);
    }

    public static JGemsControllerDispatcher getControllerDispatcher() {
        return JGemsCoreHelper.getScreen().getControllerDispatcher();
    }

    public static IController getCurrentController() {
        return getControllerDispatcher().getCurrentController();
    }

    public static void detachController() {
        getControllerDispatcher().detachController();
    }

    public static BindingManager bindingManager() {
        return getControllerDispatcher().getCurrentController().getBindingManager();
    }
}
