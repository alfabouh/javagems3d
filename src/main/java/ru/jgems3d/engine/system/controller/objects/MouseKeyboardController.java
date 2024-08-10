package ru.jgems3d.engine.system.controller.objects;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.camera.FreeCamera;
import ru.jgems3d.engine.graphics.opengl.screen.window.IWindow;
import ru.jgems3d.engine.system.inventory.IInventoryOwner;
import ru.jgems3d.engine.system.inventory.Inventory;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.jgems3d.engine.system.controller.objects.devices.MouseKeyboard;

public class MouseKeyboardController implements IController {
    protected final Vector2f normalizedRotationInput;
    protected final Vector3f normalizedPositionInput;
    private final BindingManager bindingManager;
    private final Vector3f xyzInput;
    private final MouseKeyboard mouseAndKeyboard;
    private final IWindow window;

    public MouseKeyboardController(IWindow window, BindingManager bindingManager) {
        this.window = window;
        this.mouseAndKeyboard = new MouseKeyboard(window);
        this.xyzInput = new Vector3f(0.0f);
        this.normalizedRotationInput = new Vector2f();
        this.normalizedPositionInput = new Vector3f();
        this.bindingManager = bindingManager;
    }

    public IWindow getWindow() {
        return this.window;
    }

    public MouseKeyboard getMouseAndKeyboard() {
        return this.mouseAndKeyboard;
    }

    public Vector2f getRotationInput() {
        return this.normalizedRotationInput;
    }

    public Vector3f getPositionInput() {
        return this.xyzInput;
    }

    public BindingManager getBindingManager() {
        return this.bindingManager;
    }

    @Override
    public Vector2f getNormalizedRotationInput() {
        return new Vector2f(this.normalizedRotationInput);
    }

    @Override
    public Vector3f getNormalizedPositionInput() {
        return new Vector3f(this.normalizedPositionInput);
    }

    @Override
    public void updateControllerState(IWindow window) {
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
        float d1 = (float) (xy[0] - posM.x);
        float d2 = (float) (xy[1] - posM.y);
        this.getRotationInput().set(new Vector2f(d2, d1));
        this.setCursorInCenter();
        if (this.getBindingManager().keyMoveLeft().isPressed()) {
            this.getPositionInput().add(-1.0f, 0.0f, 0.0f);
        }
        if (this.getBindingManager().keyMoveRight().isPressed()) {
            this.getPositionInput().add(1.0f, 0.0f, 0.0f);
        }
        if (this.getBindingManager().keyMoveForward().isPressed()) {
            this.getPositionInput().add(0.0f, 0.0f, -1.0f);
        }
        if (this.getBindingManager().keyMoveBackward().isPressed()) {
            this.getPositionInput().add(0.0f, 0.0f, 1.0f);
        }
        if (this.getBindingManager().keyMoveUp().isPressed()) {
            this.getPositionInput().add(0.0f, 1.0f, 0.0f);
        }
        if (this.getBindingManager().keyMoveDown().isPressed()) {
            this.getPositionInput().add(0.0f, -1.0f, 0.0f);
        }
        this.normalizedPositionInput.set(new Vector3f(this.getPositionInput().x == 0 ? 0 : this.getPositionInput().x > 0 ? 1 : -1, this.getPositionInput().y == 0 ? 0 : this.getPositionInput().y > 0 ? 1 : -1, this.getPositionInput().z == 0 ? 0 : this.getPositionInput().z > 0 ? 1 : -1));
        this.normalizedRotationInput.set(new Vector2f(this.getRotationInput()).mul(JGemsControllerDispatcher.CAM_SENS));
    }

    @Override
    public void updateItemWithInventory(IInventoryOwner hasInventory) {
        if (JGemsHelper.CAMERA.getCurrentCamera() instanceof FreeCamera) {
            return;
        }
        Inventory inventory = hasInventory.inventory();
        if (this.getMouseAndKeyboard().isLeftKeyPressed()) {
            inventory.onMouseLeftClick(hasInventory.getWorld());
        }
        if (this.getMouseAndKeyboard().isRightKeyPressed()) {
            inventory.onMouseRightClick(hasInventory.getWorld());
        }
        inventory.scrollInventoryToNotNullItem(this.getMouseAndKeyboard().getScrollVector());
    }

    public void setCursorInCenter() {
        Vector2i posM = new Vector2i((int) (this.getWindow().getWindowDimensions().x / 2.0f), (int) (this.getWindow().getWindowDimensions().y / 2.0f));
        this.getMouseAndKeyboard().setCursorCoordinates(new double[]{posM.x, posM.y});
    }
}
