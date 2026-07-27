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

    @Override
    public int hashCode() {
        return this.getKeyCode();
    }

    public String getKeyName() {
        String s = KeyEvent.getKeyText(this.getKeyCode());
        return s.toUpperCase();
    }
}