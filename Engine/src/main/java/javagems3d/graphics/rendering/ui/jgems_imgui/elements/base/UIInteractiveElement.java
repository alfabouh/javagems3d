package javagems3d.graphics.rendering.ui.jgems_imgui.elements.base;

import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.joml.Vector2i;

public abstract class UIInteractiveElement extends UIElement {
    private boolean selected;
    private boolean isMLKPressedOutsideButton;
    private boolean wasClickedButton;

    public UIInteractiveElement(@NotNull IWindow window, JGemsShaderManager currentShader, float zValue) {
        super(window, currentShader, zValue);
    }

    public void render(float frameDeltaTicks) {
        this.handleInput();
    }

    protected void handleInput() {
        IController controller = this.getController();
        if (controller instanceof MouseKeyboardController mouseKeyboardController) {
            boolean flag = mouseKeyboardController.getMouseAndKeyboard().isLeftKeyPressed();
            if (!flag) {
                this.isMLKPressedOutsideButton = false;
            }

            final Vector2f pos = this.getPosition();
            final Vector2f size = this.getScaledSize();
            Vector2f mouseCoordinates = mouseKeyboardController.getMouseAndKeyboard().getCursorCoordinatesV2F();
            if (mouseCoordinates.x >= pos.x &&
                    mouseCoordinates.x <= pos.x + size.x &&
                    mouseCoordinates.y >= pos.y &&
                    mouseCoordinates.y <= pos.y + size.y) {
                this.selected = true;
                this.onMouseEntered();
                this.onMouseInside(new Vector2f(mouseCoordinates));
            } else {
                if (flag) {
                    this.isMLKPressedOutsideButton = true;
                }
                if (this.selected) {
                    this.selected = false;
                    this.onMouseLeft();
                }
            }

            if (flag) {
                if (!this.isMLKPressedOutsideButton || (this.wasClickedButton && this.handleClickOutsideBorder())) {
                    this.selected = true;
                    this.onClicked(new Vector2f(mouseCoordinates));
                    this.wasClickedButton = true;
                    if (this.interruptMouseAfterClick()) {
                        ((MouseKeyboardController) this.getController()).getMouseAndKeyboard().forceInterruptLMB();
                        ((MouseKeyboardController) this.getController()).getMouseAndKeyboard().forceInterruptRMB();
                        ((MouseKeyboardController) this.getController()).getMouseAndKeyboard().forceInterruptMMB();
                    }
                }
            } else if (this.wasClickedButton) {
                this.onReleased(new Vector2f(mouseCoordinates));
                this.wasClickedButton = false;
            }
        }
    }

    protected abstract void onMouseInside(Vector2f mouseCoordinates);

    protected abstract void onMouseEntered();

    protected abstract void onMouseLeft();

    protected abstract void onClicked(Vector2f mouseCoordinates);

    protected abstract void onReleased(Vector2f mouseCoordinates);

    public boolean isSelected() {
        return this.selected;
    }

    protected boolean handleClickOutsideBorder() {
        return false;
    }

    protected boolean interruptMouseAfterClick() {
        return true;
    }
}
