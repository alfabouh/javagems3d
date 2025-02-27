package toolbox.controller;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.dispatcher.IControllerDispatcher;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.base.MouseKeyboardController;
import logger.SystemLogging;
import toolbox.ToolBox;
import toolbox.controller.binding.TBoxBindingManager;
import toolbox.controller.objects.AlternateMouseKeyboardController;

public class TBoxControllerDispatcher implements IControllerDispatcher {
    public static final float CAM_SENS = 0.001f;
    private final MouseKeyboardController mouseKeyboardController;

    public TBoxControllerDispatcher(IWindow window) {
        this.mouseKeyboardController = new AlternateMouseKeyboardController(window, new TBoxBindingManager());
        SystemLogging.get().getLogManager().info("Created controller dispatcher");
    }

    public static TBoxBindingManager bindingManager() {
        return (TBoxBindingManager) ToolBox.get().getScreen().getControllerDispatcher().getCurrentController().getBindingManager();
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
}
