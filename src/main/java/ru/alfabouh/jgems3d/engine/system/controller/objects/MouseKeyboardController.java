package ru.alfabouh.jgems3d.engine.system.controller.objects;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.system.controller.binding.BindingManager;
import ru.alfabouh.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.alfabouh.jgems3d.engine.system.controller.binding.JGemsBindingManager;
import ru.alfabouh.jgems3d.engine.system.controller.objects.components.MouseKeyboard;

public class MouseKeyboardController implements IController {
    private final BindingManager bindingManager;
    private final Vector3d xyzInput;
    private final MouseKeyboard mouseAndKeyboard;
    private final IWindow window;
    protected final Vector2d normalizedRotationInput;
    protected final Vector3d normalizedPositionInput;

    public MouseKeyboardController(IWindow window, BindingManager bindingManager) {
        this.window = window;
        this.mouseAndKeyboard = new MouseKeyboard(window);
        this.xyzInput = new Vector3d(0.0d);
        this.normalizedRotationInput = new Vector2d();
        this.normalizedPositionInput = new Vector3d();
        this.bindingManager = bindingManager;
    }

    public IWindow getWindow() {
        return this.window;
    }

    public MouseKeyboard getMouseAndKeyboard() {
        return this.mouseAndKeyboard;
    }

    public Vector2d getRotationInput() {
        return this.normalizedRotationInput;
    }

    public Vector3d getPositionInput() {
        return this.xyzInput;
    }

    public BindingManager getBindingManager() {
        return this.bindingManager;
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
    public void updateControllerState(IWindow window) {
        JGemsBindingManager gemsBindingManager = (JGemsBindingManager) this.getBindingManager();
        this.getMouseAndKeyboard().update(this.getBindingManager());
        this.getPositionInput().set(0.0d);
        this.getRotationInput().set(0.0d);
        this.normalizedPositionInput.set(0.0d);
        this.normalizedRotationInput.set(0.0d);
        if (!window.isInFocus()) {
            return;
        }
        Vector2i posM = new Vector2i((int) (window.getWindowDimensions().x / 2.0f), (int) (window.getWindowDimensions().y / 2.0f));
        double[] xy = this.getMouseAndKeyboard().getCursorCoordinates();
        double d1 = xy[0] - posM.x;
        double d2 = xy[1] - posM.y;
        this.getRotationInput().set(new Vector2d(d2, d1));
        this.setCursorInCenter();
        if (gemsBindingManager.keyA.isPressed()) {
            this.getPositionInput().add(-1.0f, 0.0f, 0.0f);
        }
        if (gemsBindingManager.keyD.isPressed()) {
            this.getPositionInput().add(1.0f, 0.0f, 0.0f);
        }
        if (gemsBindingManager.keyW.isPressed()) {
            this.getPositionInput().add(0.0f, 0.0f, -1.0f);
        }
        if (gemsBindingManager.keyS.isPressed()) {
            this.getPositionInput().add(0.0f, 0.0f, 1.0f);
        }
        if (gemsBindingManager.keyUp.isPressed()) {
            this.getPositionInput().add(0.0f, 1.0f, 0.0f);
        }
        if (gemsBindingManager.keyDown.isPressed()) {
            this.getPositionInput().add(0.0f, -1.0f, 0.0f);
        }
        this.normalizedPositionInput.set(new Vector3d(this.getPositionInput().x == 0 ? 0 : this.getPositionInput().x > 0 ? 1 : -1, this.getPositionInput().y == 0 ? 0 : this.getPositionInput().y > 0 ? 1 : -1, this.getPositionInput().z == 0 ? 0 : this.getPositionInput().z > 0 ? 1 : -1));
        this.normalizedRotationInput.set(new Vector2d(this.getRotationInput()).mul(JGemsControllerDispatcher.CAM_SENS));
    }

    public void setCursorInCenter() {
        Vector2i posM = new Vector2i((int) (this.getWindow().getWindowDimensions().x / 2.0f), (int) (this.getWindow().getWindowDimensions().y / 2.0f));
        this.getMouseAndKeyboard().setCursorCoordinates(new double[]{posM.x, posM.y});
    }
}
