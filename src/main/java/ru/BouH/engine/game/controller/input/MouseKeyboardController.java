package ru.BouH.engine.game.controller.input;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.render.screen.window.Window;

public class MouseKeyboardController implements IController {
    private final Vector2d displayInput;
    private final Vector3d xyzInput;
    private final Keyboard keyboard;
    private final Mouse mouse;
    private Window window;

    public MouseKeyboardController(Window window) {
        this.keyboard = new Keyboard(window);
        this.mouse = new Mouse(window);
        this.displayInput = new Vector2d(0.0d);
        this.xyzInput = new Vector3d(0.0d);
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

    @Override
    public Vector2d getDisplayInput() {
        return this.displayInput;
    }

    @Override
    public Vector3d getXYZInput() {
        return this.xyzInput;
    }

    @Override
    public void updateControllerState(Window window) {
        this.keyboard.updateKeys();
    }
}
