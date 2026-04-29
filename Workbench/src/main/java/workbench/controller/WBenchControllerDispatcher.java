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
}
