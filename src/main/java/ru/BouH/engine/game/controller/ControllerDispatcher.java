package ru.BouH.engine.game.controller;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.input.IController;
import ru.BouH.engine.game.controller.input.MouseKeyboardController;
import ru.BouH.engine.physics.entities.IControllable;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.screen.window.Window;

public class ControllerDispatcher {
    public static final float CAM_SENS = 0.1f;
    public static MouseKeyboardController mouseKeyboardController = null;
    private IController currentController;
    private IControllable currentControlledItem;

    public ControllerDispatcher(Window window) {
        ControllerDispatcher.mouseKeyboardController = new MouseKeyboardController(window);
        this.setController(ControllerDispatcher.defaultController());
    }

    public static IController defaultController() {
        return ControllerDispatcher.mouseKeyboardController;
    }

    public static Vector2d getNormalizedRotationInput(IController iController) {
        return iController.getNormalizedRotationInput();
    }

    public static Vector3d getNormalizedPositionInput(IController iController) {
        return iController.getNormalizedPositionInput();
    }

    public void setController(IController iController) {
        this.currentController = iController;
    }

    public IController getCurrentController() {
        return this.currentController;
    }

    public void attachControllerTo(IController controller, IControllable remoteController) {
        Game.getGame().getLogManager().log("Attached controller to: " + ((WorldItem) remoteController).getItemName());
        remoteController.setController(controller);
        this.currentControlledItem = remoteController;
    }

    public void detachController() {
        if (this.getCurrentControlledItem() != null) {
            Game.getGame().getLogManager().log("Detached Controller From: " + ((WorldItem) this.getCurrentControlledItem()).getItemName());
            this.getCurrentControlledItem().setController(null);
            this.currentControlledItem = null;
        }
    }

    public IControllable getCurrentControlledItem() {
        return this.currentControlledItem;
    }

    public void updateController(Window window) {
        if (this.getCurrentController() != null) {
            this.getCurrentController().updateControllerState(window);
            if (this.getCurrentControlledItem() != null) {
                this.performControllerToItem(this.getCurrentController(), this.getCurrentControlledItem());
            }
        }
    }

    private void performControllerToItem(IController iController, IControllable iControllable) {
        Vector2d d1 = ControllerDispatcher.getNormalizedRotationInput(iController);
        Vector3d d2 = ControllerDispatcher.getNormalizedPositionInput(iController);
        iControllable.performController(d1, d2);
    }
}
