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

package toolbox.controller.binding;

import org.lwjgl.glfw.GLFW;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.components.Key;

public class TBoxBindingManager extends BindingManager {
    public final Key keyA;
    public final Key keyD;
    public final Key keyW;
    public final Key keyS;
    public final Key keyUp;
    public final Key keyShift;
    public final Key keyCtrl;
    public final Key keyDelete;
    public final Key keyEsc;

    public TBoxBindingManager() {
        this.keyA = new Key(GLFW.GLFW_KEY_A);
        this.keyD = new Key(GLFW.GLFW_KEY_D);
        this.keyW = new Key(GLFW.GLFW_KEY_W);
        this.keyS = new Key(GLFW.GLFW_KEY_S);
        this.keyUp = new Key(GLFW.GLFW_KEY_SPACE);
        this.keyShift = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
        this.keyCtrl = new Key(GLFW.GLFW_KEY_LEFT_CONTROL);
        this.keyDelete = new Key(GLFW.GLFW_KEY_DELETE);
        this.keyEsc = new Key(GLFW.GLFW_KEY_ESCAPE);

        this.addBinding(this.keyA);
        this.addBinding(this.keyD);
        this.addBinding(this.keyW);
        this.addBinding(this.keyS);
        this.addBinding(this.keyUp);
        this.addBinding(this.keyShift);
        this.addBinding(this.keyCtrl);
        this.addBinding(this.keyDelete);
        this.addBinding(this.keyEsc);
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
