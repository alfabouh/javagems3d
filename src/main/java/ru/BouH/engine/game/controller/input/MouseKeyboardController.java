package ru.BouH.engine.game.controller.input;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.controller.binding.BindingList;
import ru.BouH.engine.render.screen.window.Window;

public class MouseKeyboardController implements IController {
    private final Vector3d xyzInput;
    private final Keyboard keyboard;
    private final Mouse mouse;
    private final Window window;
    private final Vector2d normalizedRotationInput;
    private final Vector3d normalizedPositionInput;

    public MouseKeyboardController(Window window) {
        this.window = window;
        this.keyboard = new Keyboard(window);
        this.mouse = new Mouse(window);
        this.xyzInput = new Vector3d(0.0d);
        this.normalizedRotationInput = new Vector2d();
        this.normalizedPositionInput = new Vector3d();
    }

    public Window getWindow() {
        return this.window;
    }

    public Keyboard getKeyboard() {
        return this.keyboard;
    }

    public Mouse getMouse() {
        return this.mouse;
    }

    public Vector2d getRotationInput() {
        return this.normalizedRotationInput;
    }

    public Vector3d getPositionInput() {
        return this.xyzInput;
    }

    @Override
    public Vector2d getNormalizedRotationInput() {
        return new Vector2d(this.normalizedRotationInput);
    }

    @Override
    public Vector3d getNormalizedPositionInput() {
        return new Vector3d(this.normalizedPositionInput);
    }

    @Override
    public void updateControllerState(Window window) {
        this.getMouse().update();
        if (this.getMouse().scrollUpdate) {
            this.getMouse().scrollUpdate = false;
        } else {
            this.getMouse().scrollVector = 0;
        }
        this.keyboard.updateKeys();
        this.getPositionInput().set(0.0d);
        this.getRotationInput().set(0.0d);
        this.normalizedPositionInput.set(0.0d);
        this.normalizedRotationInput.set(0.0d);
        if (!window.isInFocus()) {
            return;
        }
        Vector2i posM = new Vector2i((int) (window.getWidth() / 2.0f), (int) (window.getHeight() / 2.0f));
        double[] xy = this.getMouse().getCursorCoordinates();
        double d1 = xy[0] - posM.x;
        double d2 = xy[1] - posM.y;
        this.getRotationInput().set(new Vector2d(d2, d1));
        this.setCursorInCenter();
        if (BindingList.instance.keyA.isPressed()) {
            this.getPositionInput().add(-1.0f, 0.0f, 0.0f);
        }
        if (BindingList.instance.keyD.isPressed()) {
            this.getPositionInput().add(1.0f, 0.0f, 0.0f);
        }
        if (BindingList.instance.keyW.isPressed()) {
            this.getPositionInput().add(0.0f, 0.0f, -1.0f);
        }
        if (BindingList.instance.keyS.isPressed()) {
            this.getPositionInput().add(0.0f, 0.0f, 1.0f);
        }
        if (BindingList.instance.keyUp.isPressed()) {
            this.getPositionInput().add(0.0f, 1.0f, 0.0f);
        }
        if (BindingList.instance.keyDown.isPressed()) {
            this.getPositionInput().add(0.0f, -1.0f, 0.0f);
        }
        this.normalizedPositionInput.set(new Vector3d(this.getPositionInput().x == 0 ? 0 : this.getPositionInput().x > 0 ? 1 : -1, this.getPositionInput().y == 0 ? 0 : this.getPositionInput().y > 0 ? 1 : -1, this.getPositionInput().z == 0 ? 0 : this.getPositionInput().z > 0 ? 1 : -1));
        this.normalizedRotationInput.set(new Vector2d(this.getRotationInput()).mul(ControllerDispatcher.CAM_SENS));
    }

    public void setCursorInCenter() {
        Vector2i posM = new Vector2i((int) (window.getWidth() / 2.0f), (int) (window.getHeight() / 2.0f));
        this.getMouse().setCursorCoordinates(new double[]{posM.x, posM.y});
    }
}
