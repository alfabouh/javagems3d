package ru.alfabouh.engine.render.scene.gui.ui;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.controller.ControllerDispatcher;
import ru.alfabouh.engine.game.controller.input.IController;
import ru.alfabouh.engine.game.controller.input.MouseKeyboardController;

public abstract class InteractiveUI implements BasicUI {
    private final boolean isVisible;
    private boolean isMLKPressedOutsideButton;
    private boolean wasClickedButton;
    private Vector3f position;
    private Vector2f size;
    private boolean selected;

    public InteractiveUI(Vector3f position, Vector2f size) {
        this.position = position;
        this.size = size;
        this.isMLKPressedOutsideButton = false;
        this.wasClickedButton = false;
        this.isVisible = true;
        this.selected = false;
    }

    public void render(double partialTicks) {
        if (!this.isVisible()) {
            return;
        }
        this.handleInput();
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
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

    protected boolean handleClickOutsideBorder() {
        return false;
    }

    protected boolean interruptMouseAfterClick() {
        return true;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public Vector2f getSize() {
        return new Vector2f(this.size);
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public abstract void onMouseInside(Vector2d mouseCoordinates);

    public abstract void onMouseEntered();

    public abstract void onMouseLeft();

    public abstract void onClicked(Vector2d mouseCoordinates);

    public abstract void onUnClicked(Vector2d mouseCoordinates);
}
