package ru.alfabouh.engine.render.scene.immediate_gui.elements.base;

import org.joml.Vector2d;
import ru.alfabouh.engine.system.controller.ControllerDispatcher;
import ru.alfabouh.engine.system.controller.input.IController;
import ru.alfabouh.engine.system.controller.input.MouseKeyboardController;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;

public abstract class UIInteractiveElement extends UIElement {
    private boolean selected;
    private boolean isMLKPressedOutsideButton;
    private boolean wasClickedButton;

    public UIInteractiveElement(ShaderManager currentShader, float zValue) {
        super(currentShader, zValue);
    }

    public void render(double partialTicks) {
        this.handleInput();
    }

    protected void handleInput() {
        IController controller = this.getController();
        if (controller instanceof MouseKeyboardController) {
            MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
            boolean flag = mouseKeyboardController.getMouse().isLeftKeyPressed();
            if (!flag) {
                this.isMLKPressedOutsideButton = false;
            }

            Vector2d mouseCoordinates = new Vector2d(mouseKeyboardController.getMouse().getCursorCoordinates()[0], mouseKeyboardController.getMouse().getCursorCoordinates()[1]);
            if (mouseCoordinates.x >= this.getPosition().x && mouseCoordinates.x <= this.getPosition().x + this.getSize().x && mouseCoordinates.y >= this.getPosition().y && mouseCoordinates.y <= this.getPosition().y + this.getSize().y) {
                this.selected = true;
                this.onMouseEntered();
                this.onMouseInside(new Vector2d(mouseCoordinates));
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
                    this.onClicked(new Vector2d(mouseCoordinates));
                    this.wasClickedButton = true;
                    if (this.interruptMouseAfterClick()) {
                        ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptLMB();
                        ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptRMB();
                        ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptMMB();
                    }
                }
            } else if (this.wasClickedButton) {
                this.onUnClicked(new Vector2d(mouseCoordinates));
                this.wasClickedButton = false;
            }
        }
    }

    protected abstract void onMouseInside(Vector2d mouseCoordinates);

    protected abstract void onMouseEntered();

    protected abstract void onMouseLeft();

    protected abstract void onClicked(Vector2d mouseCoordinates);

    protected abstract void onUnClicked(Vector2d mouseCoordinates);

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
