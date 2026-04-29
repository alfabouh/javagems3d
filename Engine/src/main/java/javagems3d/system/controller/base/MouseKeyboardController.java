package javagems3d.system.controller.base;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.devices.MouseKeyboard;

import java.util.function.Predicate;

public abstract class MouseKeyboardController implements IController {
    private final Vector2i prevMouseCoordinates;

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
        this.prevMouseCoordinates = new Vector2i(0);
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

        double[] xy = this.getMouseAndKeyboard().getCursorCoordinates();
        if (!window.isWindowInFocus()) {
            this.prevMouseCoordinates.set((int) xy[0], (int) xy[1]);
            return;
        }

        boolean isCenterScanning = this.getScanningMode().equals(ScanningMode.CENTER);
        Vector2i posM = isCenterScanning ? new Vector2i((int) (window.getWindowSize().x / 2.0f), (int) (window.getWindowSize().y / 2.0f)) : this.prevMouseCoordinates;
        float d1 = (float) (xy[0] - posM.x);
        float d2 = (float) (xy[1] - posM.y);
        this.prevMouseCoordinates.set((int) xy[0], (int) xy[1]);

        if (!this.disableMouseScanning()) {
            this.scanMouse(isCenterScanning, d1, d2);
        }
        if (!this.disableKeyboardScanning()) {
            this.scanKeyBoard();
        }

        this.normalizedPositionInput.set(new Vector3f(this.getPositionInput().x == 0 ? 0 : this.getPositionInput().x > 0 ? 1 : -1, this.getPositionInput().y == 0 ? 0 : this.getPositionInput().y > 0 ? 1 : -1, this.getPositionInput().z == 0 ? 0 : this.getPositionInput().z > 0 ? 1 : -1));
        this.normalizedRotationInput.set(new Vector2f(this.getRotationInput()).mul(this.getCamSensitivity()));
    }

    protected void scanMouse(boolean isCenterScanning, float d1, float d2) {
        if (isCenterScanning) {
            this.getRotationInput().set(new Vector2f(d2, d1));
            this.setCursorInCenter();
        } else {
            Predicate<Void> predicate = null;
            switch (this.getScanningMode()) {
                case POS_RELATIVE_LMK: {
                    predicate = (e) -> this.getMouseAndKeyboard().isLeftKeyPressed();
                    break;
                }
                case POS_RELATIVE_RMK: {
                    predicate = (e) -> this.getMouseAndKeyboard().isRightKeyPressed();
                    break;
                }
                case POS_RELATIVE_MMK: {
                    predicate = (e) -> this.getMouseAndKeyboard().isMiddleKeyPressed();
                    break;
                }
            }
            if (predicate != null && predicate.test(null)) {
                this.getRotationInput().set(new Vector2f(d2, d1));
            }
        }
    }

    protected void scanKeyBoard() {
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
    }

    public abstract ScanningMode getScanningMode();

    public boolean disableMouseScanning() {
        return false;
    }

    public boolean disableKeyboardScanning() {
        return false;
    }

    public abstract float getCamSensitivity();

    public void setCursorInCenter() {
        Vector2i posM = new Vector2i((int) (this.getWindow().getWindowSize().x / 2.0f), (int) (this.getWindow().getWindowSize().y / 2.0f));
        this.getMouseAndKeyboard().setCursorCoordinates(new double[]{posM.x, posM.y});
    }
}
