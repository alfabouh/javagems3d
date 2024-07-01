package ru.alfabouh.jgems3d.engine.system.controller.dispatcher;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.objects.base.IControllable;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.system.controller.binding.JGemsBindingManager;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class JGemsControllerDispatcher implements IControllerDispatcher {
    public static final float CAM_SENS = 0.1f;
    public static MouseKeyboardController mouseKeyboardController = null;
    private IController currentController;
    private IControllable currentControlledItem;

    public JGemsControllerDispatcher(Window window) {
        JGemsControllerDispatcher.mouseKeyboardController = new MouseKeyboardController(window, new JGemsBindingManager());
        this.setController(JGemsControllerDispatcher.defaultController());
        SystemLogging.get().getLogManager().log("Created controller dispatcher!");
    }

    public static JGemsBindingManager bindingManager() {
        return (JGemsBindingManager) JGems.get().getScreen().getControllerDispatcher().getCurrentController().getBindingManager();
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
        SystemLogging.get().getLogManager().log("Attached controller to: " + ((WorldItem) remoteController).getItemName());
        remoteController.setController(controller);
        this.currentControlledItem = remoteController;
    }

    public void detachController() {
        if (this.getCurrentControlledItem() != null) {
            SystemLogging.get().getLogManager().log("Detached Controller From: " + ((WorldItem) this.getCurrentControlledItem()).getItemName());
            this.getCurrentControlledItem().setController(null);
            this.currentControlledItem = null;
        }
    }

    public IControllable getCurrentControlledItem() {
        return this.currentControlledItem;
    }

    public void updateController(IWindow window) {
        if (this.getCurrentController() != null) {
            this.getCurrentController().updateControllerState(window);
            if (!JGems.get().getEngineState().isPaused()) {
                if (this.getCurrentControlledItem() != null) {
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
