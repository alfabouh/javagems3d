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

package ru.jgems3d.engine.system.controller.components;

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
