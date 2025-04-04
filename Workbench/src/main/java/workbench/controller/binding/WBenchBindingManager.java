package workbench.controller.binding;

import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.components.Key;
import org.lwjgl.glfw.GLFW;

public class WBenchBindingManager extends BindingManager {
    public final Key keyA;
    public final Key keyD;
    public final Key keyW;
    public final Key keyS;
    public final Key keyUp;
    public final Key keyShift;
    public final Key keyCtrl;
    public final Key keyDelete;
    public final Key keyEsc;

    public WBenchBindingManager() {
        this.keyA = new Key(GLFW.GLFW_KEY_A);
        this.keyD = new Key(GLFW.GLFW_KEY_D);
        this.keyW = new Key(GLFW.GLFW_KEY_W);
        this.keyS = new Key(GLFW.GLFW_KEY_S);
        this.keyUp = new Key(GLFW.GLFW_KEY_SPACE);
        this.keyShift = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
        this.keyCtrl = new Key(GLFW.GLFW_KEY_LEFT_CONTROL);
        this.keyDelete = new Key(GLFW.GLFW_KEY_DELETE);
        this.keyEsc = new Key(GLFW.GLFW_KEY_ESCAPE);

        this.addBinding(this.keyA, "Walk Left");
        this.addBinding(this.keyD, "Walk Right");
        this.addBinding(this.keyW, "Walk Forward");
        this.addBinding(this.keyS, "Walk Backward");
        this.addBinding(this.keyUp, "Fly Up");
        this.addBinding(this.keyShift, "Fly Down");
        this.addBinding(this.keyCtrl, "Special Interface Controlling");
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
