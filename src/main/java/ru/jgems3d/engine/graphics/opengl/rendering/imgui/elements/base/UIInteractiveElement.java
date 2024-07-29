package ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base;

import org.joml.Vector2f;
import ru.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.jgems3d.engine.system.controller.objects.IController;
import ru.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public abstract class UIInteractiveElement extends UIElement {
    private boolean selected;
    private boolean isMLKPressedOutsideButton;
    private boolean wasClickedButton;

    public UIInteractiveElement(JGemsShaderManager currentShader, float zValue) {
        super(currentShader, zValue);
    }

    public void render(float partialTicks) {
        this.handleInput();
    }

    protected void handleInput() {
        IController controller = this.getController();
        if (controller instanceof MouseKeyboardController) {
            MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
            boolean flag = mouseKeyboardController.getMouseAndKeyboard().isLeftKeyPressed();
            if (!flag) {
                this.isMLKPressedOutsideButton = false;
            }

            Vector2f mouseCoordinates = mouseKeyboardController.getMouseAndKeyboard().getCursorCoordinatesV2F();
            if (mouseCoordinates.x >= this.getPosition().x && mouseCoordinates.x <= this.getPosition().x + this.getSize().x && mouseCoordinates.y >= this.getPosition().y && mouseCoordinates.y <= this.getPosition().y + this.getSize().y) {
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
                        JGemsControllerDispatcher.mouseKeyboardController.getMouseAndKeyboard().forceInterruptLMB();
                        JGemsControllerDispatcher.mouseKeyboardController.getMouseAndKeyboard().forceInterruptRMB();
                        JGemsControllerDispatcher.mouseKeyboardController.getMouseAndKeyboard().forceInterruptMMB();
                    }
                }
            } else if (this.wasClickedButton) {
                this.onUnClicked(new Vector2f(mouseCoordinates));
                this.wasClickedButton = false;
            }
        }
    }

    protected abstract void onMouseInside(Vector2f mouseCoordinates);

    protected abstract void onMouseEntered();

    protected abstract void onMouseLeft();

    protected abstract void onClicked(Vector2f mouseCoordinates);

    protected abstract void onUnClicked(Vector2f mouseCoordinates);

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
