package ru.jgems3d.toolbox.controller.binding;

import org.lwjgl.glfw.GLFW;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.controller.components.Key;

public class TBoxBindingManager extends BindingManager {
    public final Key keyA;
    public final Key keyD;
    public final Key keyW;
    public final Key keyS;
    public final Key keyUp;
    public final Key keyShift;
    public final Key keyCtrl;
    public final Key keyDelete;

    public TBoxBindingManager() {
        this.keyA = new Key(GLFW.GLFW_KEY_A);
        this.keyD = new Key(GLFW.GLFW_KEY_D);
        this.keyW = new Key(GLFW.GLFW_KEY_W);
        this.keyS = new Key(GLFW.GLFW_KEY_S);
        this.keyUp = new Key(GLFW.GLFW_KEY_SPACE);
        this.keyShift = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
        this.keyCtrl = new Key(GLFW.GLFW_KEY_LEFT_CONTROL);
        this.keyDelete = new Key(GLFW.GLFW_KEY_DELETE);

        this.createBinding(this.keyA);
        this.createBinding(this.keyD);
        this.createBinding(this.keyW);
        this.createBinding(this.keyS);
        this.createBinding(this.keyUp);
        this.createBinding(this.keyShift);
        this.createBinding(this.keyCtrl);
        this.createBinding(this.keyDelete);
    }
}
