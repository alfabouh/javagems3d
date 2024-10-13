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

package javagems3d.system.controller.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.camera.FreeControlledCamera;
import javagems3d.graphics.opengl.screen.window.IWindow;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.controller.objects.devices.MouseKeyboard;
import javagems3d.system.inventory.IInventoryOwner;
import javagems3d.system.inventory.Inventory;

public class MouseKeyboardController implements IController {
    protected final Vector2f normalizedRotationInput;
    protected final Vector3f normalizedPositionInput;
    private final BindingManager bindingManager;
    private final Vector3f xyzInput;
    private final MouseKeyboard mouseAndKeyboard;
    private final IWindow window;

    public MouseKeyboardController(IWindow window, @NotNull BindingManager bindingManager) {
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

    public void clear() {
        this.normalizedPositionInput.set(0.0f);
        this.normalizedRotationInput.set(0.0f);
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
        if (JGemsHelper.CAMERA.getCurrentCamera() instanceof FreeControlledCamera) {
            return;
        }
        Inventory inventory = hasInventory.getInventory();
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
