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

package javagems3d.toolbox.controller;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.engine.graphics.opengl.screen.window.IWindow;
import javagems3d.engine.system.controller.dispatcher.IControllerDispatcher;
import javagems3d.engine.system.controller.objects.IController;
import javagems3d.engine.system.controller.objects.MouseKeyboardController;
import javagems3d.logger.SystemLogging;
import javagems3d.toolbox.ToolBox;
import javagems3d.toolbox.controller.binding.TBoxBindingManager;
import javagems3d.toolbox.controller.objects.AlternateMouseKeyboardController;

public class TBoxControllerDispatcher implements IControllerDispatcher {
    public static final float CAM_SENS = 0.001f;
    private final MouseKeyboardController mouseKeyboardController;

    public TBoxControllerDispatcher(IWindow window) {
        this.mouseKeyboardController = new AlternateMouseKeyboardController(window, new TBoxBindingManager());
        SystemLogging.get().getLogManager().log("Created controller dispatcher!");
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
