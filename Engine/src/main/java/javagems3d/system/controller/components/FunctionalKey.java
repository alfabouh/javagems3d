/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.controller.components;

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
