package ru.alfabouh.jgems3d.toolbox.controller;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.graphics.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.system.controller.dispatcher.IControllerDispatcher;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.controller.binding.TBoxBindingManager;
import ru.alfabouh.jgems3d.toolbox.controller.objects.AlternateMouseKeyboardController;

public class TBoxControllerDispatcher implements IControllerDispatcher {
    public static final float CAM_SENS = 0.1f;
    private final MouseKeyboardController mouseKeyboardController;

    public TBoxControllerDispatcher(IWindow window) {
        this.mouseKeyboardController = new AlternateMouseKeyboardController(window, new TBoxBindingManager());
        SystemLogging.get().getLogManager().log("Created controller dispatcher!");
    }

    public static TBoxBindingManager bindingManager() {
        return (TBoxBindingManager) ToolBox.get().getScreen().getControllerDispatcher().getMouseKeyboardController().getBindingManager();
    }

    public static Vector2f getNormalizedRotationInput(IController iController) {
        return iController.getNormalizedRotationInput();
    }

    public static Vector3f getNormalizedPositionInput(IController iController) {
        return iController.getNormalizedPositionInput();
    }

    public void updateController(IWindow window) {
        if (this.getMouseKeyboardController() != null) {
            this.getMouseKeyboardController().updateControllerState(window);
        }
    }

    public MouseKeyboardController getMouseKeyboardController() {
        return this.mouseKeyboardController;
    }
}
