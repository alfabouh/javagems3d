package ru.BouH.engine.game.controller;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.binding.BindingList;
import ru.BouH.engine.game.controller.input.MouseKeyboardController;
import ru.BouH.engine.physics.entities.IRemoteController;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.screen.window.Window;

public class ControllerDispatcher {
    public static final float CAM_SENS = 0.1f;
    public static MouseKeyboardController mouseKeyboardController = null;
    private IController currentController;
    private IRemoteController currentControlledItem;

    public ControllerDispatcher(Window window) {
        ControllerDispatcher.mouseKeyboardController = new MouseKeyboardController(window);
        this.setController(ControllerDispatcher.defaultController());
    }

    public static IController defaultController() {
        return ControllerDispatcher.mouseKeyboardController;
    }

    public static Vector2d getOptionedDisplayVec(IController iController) {
        return new Vector2d(iController.getDisplayInput()).mul(ControllerDispatcher.CAM_SENS);
    }

    public static Vector3d getOptionedXYZVec(IController iController) {
        Vector3d v = iController.getXYZInput();
        return new Vector3d(v.x == 0 ? 0 : v.x > 0 ? 1 : -1, v.y == 0 ? 0 : v.y > 0 ? 1 : -1, v.z == 0 ? 0 : v.z > 0 ? 1 : -1);
    }

    public void setController(IController iController) {
        this.currentController = iController;
    }

    public IController getCurrentController() {
        return this.currentController;
    }

    public void attachControllerTo(IController controller, IRemoteController remoteController) {
        Game.getGame().getLogManager().log("Attached controller to: " + ((WorldItem) remoteController).getItemName());
        remoteController.setController(controller);
        this.currentControlledItem = remoteController;
    }

    public void detachController(IController controller) {
        if (this.getCurrentControlledItem() != null) {
            Game.getGame().getLogManager().log("Detached Controller From: " + ((WorldItem) this.getCurrentControlledItem()).getItemName());
            this.getCurrentControlledItem().setController(null);
            this.currentControlledItem = null;
        }
    }

    public IRemoteController getCurrentControlledItem() {
        return this.currentControlledItem;
    }

    public void updateController(boolean isInFocus, Window window) {
        this.getCurrentController().updateControllerState(window);
        if (isInFocus) {
            if (this.getCurrentController() != null) {
                if (this.getCurrentController() == ControllerDispatcher.mouseKeyboardController) {
                    this.getCurrentController().getXYZInput().set(0.0d);
                    this.getCurrentController().getDisplayInput().set(0.0d);
                    MouseKeyboardController mouseKeyboardController1 = ControllerDispatcher.mouseKeyboardController;
                    Vector2i posM = new Vector2i((int) (window.getWidth() / 2.0f), (int) (window.getHeight() / 2.0f));
                    double[] xy = mouseKeyboardController1.getMouse().getCursorCoordinates();
                    double d1 = xy[0] - posM.x;
                    double d2 = xy[1] - posM.y;
                    this.getCurrentController().getDisplayInput().set(new Vector2d(d2, d1));
                    mouseKeyboardController1.getMouse().setCursorCoordinates(new double[]{posM.x, posM.y});
                    if (BindingList.instance.keyA.isPressed()) {
                        this.getCurrentController().getXYZInput().add(-1.0f, 0.0f, 0.0f);
                    }
                    if (BindingList.instance.keyD.isPressed()) {
                        this.getCurrentController().getXYZInput().add(1.0f, 0.0f, 0.0f);
                    }
                    if (BindingList.instance.keyW.isPressed()) {
                        this.getCurrentController().getXYZInput().add(0.0f, 0.0f, -1.0f);
                    }
                    if (BindingList.instance.keyS.isPressed()) {
                        this.getCurrentController().getXYZInput().add(0.0f, 0.0f, 1.0f);
                    }
                    if (BindingList.instance.keyUp.isPressed()) {
                        this.getCurrentController().getXYZInput().add(0.0f, 1.0f, 0.0f);
                    }
                    if (BindingList.instance.keyDown.isPressed()) {
                        this.getCurrentController().getXYZInput().add(0.0f, -1.0f, 0.0f);
                    }
                }
            }
        } else {
            this.getCurrentController().getDisplayInput().set(0.0d);
            this.getCurrentController().getXYZInput().set(0.0d);
        }
        if (this.getCurrentControlledItem() != null) {
            this.performControllerToItem(this.getCurrentController(), this.getCurrentControlledItem());
        }
    }

    private void performControllerToItem(IController iController, IRemoteController iRemoteController) {
        Vector2d d1 = iController == null ? new Vector2d(0.0d) : ControllerDispatcher.getOptionedDisplayVec(iController);
        Vector3d d2 = iController == null ? new Vector3d(0.0d) : ControllerDispatcher.getOptionedXYZVec(iController);
        iRemoteController.performController(d1, d2);
    }
}
