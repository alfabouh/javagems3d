package ru.alfabouh.jgems3d.engine.system.controller.components;

import org.jetbrains.annotations.NotNull;

public class FunctionalKey extends Key {
    private final IKeyAction keyAction;

    public FunctionalKey(@NotNull IKeyAction keyAction, int keyCode) {
        super(keyCode);
        this.keyAction = keyAction;
    }

    public void refreshState(boolean press) {
        super.refreshState(press);
        if (this.isClicked) {
            this.getKeyAction().onTrigger(IKeyAction.KeyAction.CLICK);
        }
        if (this.isPressed) {
            this.getKeyAction().onTrigger(IKeyAction.KeyAction.PRESS);
        }
        if (this.isUnpressed) {
            this.getKeyAction().onTrigger(IKeyAction.KeyAction.UN_PRESS);
        }
    }

    public IKeyAction getKeyAction() {
        return this.keyAction;
    }
}
