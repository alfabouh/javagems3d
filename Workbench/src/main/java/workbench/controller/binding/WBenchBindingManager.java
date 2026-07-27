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

package workbench.controller.binding;

import javagems3d.JGems3D;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.components.FunctionalKey;
import javagems3d.system.controller.components.IKeyAction;
import javagems3d.system.controller.components.Key;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.lwjgl.glfw.GLFW;
import workbench.resources.WBenchResourceManager;

public class WBenchBindingManager extends BindingManager {
    public final Key keyA;
    public final Key keyD;
    public final Key keyW;
    public final Key keyS;
    public final Key keyUp;
    public final Key keyShift;
    public final Key keyCtrl;
    public final Key keyAlt;
    public final Key keyDelete;
    public final Key keyEsc;
    public final Key keyV;

    public WBenchBindingManager() {
        this.keyA = new Key(GLFW.GLFW_KEY_A);
        this.keyD = new Key(GLFW.GLFW_KEY_D);
        this.keyW = new Key(GLFW.GLFW_KEY_W);
        this.keyS = new Key(GLFW.GLFW_KEY_S);
        this.keyUp = new Key(GLFW.GLFW_KEY_SPACE);
        this.keyShift = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
        this.keyCtrl = new Key(GLFW.GLFW_KEY_LEFT_CONTROL);
        this.keyAlt = new Key(GLFW.GLFW_KEY_LEFT_ALT);
        this.keyDelete = new Key(GLFW.GLFW_KEY_DELETE);
        this.keyEsc = new Key(GLFW.GLFW_KEY_ESCAPE);

        this.keyV = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                WBenchResourceManager.reloadShaders();
            }
        }, GLFW.GLFW_KEY_F1);

        if (JGems3D.DEBUG_MODE) {
            this.addBinding(this.keyV);
        }

        this.addBinding(this.keyA, "Walk Left");
        this.addBinding(this.keyD, "Walk Right");
        this.addBinding(this.keyW, "Walk Forward");
        this.addBinding(this.keyS, "Walk Backward");
        this.addBinding(this.keyUp, "Fly Up");
        this.addBinding(this.keyShift, "Fly Down");
        this.addBinding(this.keyAlt, "Camera Speed Up | Additional Func");
        this.addBinding(this.keyCtrl, "Special Interface Controlling | Camera Slow Down");
        this.addBinding(this.keyDelete, "Delete");
        this.addBinding(this.keyEsc, "Cancel");
    }

    @Override
    public Key keyMoveLeft() {
        return this.keyA;
    }

    @Override
    public Key keyMoveRight() {
        return this.keyD;
    }

    @Override
    public Key keyMoveForward() {
        return this.keyW;
    }

    @Override
    public Key keyMoveBackward() {
        return this.keyS;
    }

    @Override
    public Key keyMoveUp() {
        return this.keyUp;
    }

    @Override
    public Key keyMoveDown() {
        return this.keyShift;
    }
}
