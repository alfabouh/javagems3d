package ru.BouH.engine.game.controller.components;

import java.awt.event.KeyEvent;

public class Key {
    private final int keyCode;
    protected boolean isPressed;
    protected boolean isClicked;
    protected boolean isUnpressed;

    public Key(int keyCode) {
        this.keyCode = keyCode;
        this.isPressed = false;
        this.isClicked = false;
        this.isUnpressed = false;
    }

    public void refreshState(boolean press) {
        if (this.isClicked) {
            this.isClicked = false;
        }
        if (this.isUnpressed) {
            this.isUnpressed = false;
        }
        if (press) {
            this.isClicked = !this.isPressed;
        } else {
            this.isUnpressed = this.isPressed;
        }
        this.isPressed = press;
    }

    public boolean isClicked() {
        return this.isClicked;
    }

    public boolean isPressed() {
        return this.isPressed;
    }

    public boolean isUnpressed() {
        return this.isUnpressed;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public String getKeyName() {
        String s = KeyEvent.getKeyText(this.getKeyCode());
        return s.toUpperCase();
    }
}
