package ru.BouH.engine.render.scene.gui.ui;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.controller.input.IController;
import ru.BouH.engine.game.controller.input.MouseKeyboardController;

public abstract class InteractiveUI implements BasicUI {
    private int mouseState;
    private Vector3f position;
    private Vector2f size;
    private final boolean isVisible;
    private boolean selected;

    public InteractiveUI(Vector3f position, Vector2f size) {
        this.position = position;
        this.size = size;
        this.mouseState = -1;
        this.isVisible = true;
        this.selected = false;
    }

    public void render(double partialTicks) {
        if (!this.isVisible()) {
            return;
        }
        IController controller = Game.getGame().getScreen().getControllerDispatcher().getCurrentController();
        if (controller instanceof MouseKeyboardController) {
            MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
            Vector2d mouseCoordinates = new Vector2d(mouseKeyboardController.getMouse().getCursorCoordinates()[0], mouseKeyboardController.getMouse().getCursorCoordinates()[1]);
            if (mouseCoordinates.x >= this.getPosition().x && mouseCoordinates.x <= this.getPosition().x + this.getSize().x && mouseCoordinates.y >= this.getPosition().y && mouseCoordinates.y <= this.getPosition().y + this.getSize().y) {
                if (this.mouseState == -1) {
                    this.selected = true;
                    this.onMouseEntered();
                }
                this.mouseState = 0;
            } else {
                if (this.mouseState == 0) {
                    this.selected = false;
                    this.onMouseLeft();
                }
                this.mouseState = -1;
            }
            if (this.mouseState == 0) {
                if (mouseKeyboardController.getMouse().isLeftKeyPressed()) {
                    this.onClicked();
                    ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptLMB();
                    ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptRMB();
                    ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptMMB();
                }
                this.onMouseInside();
            }
        }
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public Vector2f getSize() {
        return this.size;
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

    public abstract void onMouseInside();

    public abstract void onMouseEntered();

    public abstract void onMouseLeft();

    public abstract void onClicked();
}
