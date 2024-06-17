package ru.alfabouh.jgems3d.toolbox.controller.binding;

import org.lwjgl.glfw.GLFW;
import ru.alfabouh.jgems3d.engine.system.controller.binding.BindingManager;
import ru.alfabouh.jgems3d.engine.system.controller.components.Key;

public class TBoxBindingManager extends BindingManager {
    public final Key keyA;
    public final Key keyD;
    public final Key keyW;
    public final Key keyS;
    public final Key keyUp;
    public final Key keyDown;
    public final Key keyDelete;

    public TBoxBindingManager() {
        this.keyA = new Key(GLFW.GLFW_KEY_A);
        this.keyD = new Key(GLFW.GLFW_KEY_D);
        this.keyW = new Key(GLFW.GLFW_KEY_W);
        this.keyS = new Key(GLFW.GLFW_KEY_S);
        this.keyUp = new Key(GLFW.GLFW_KEY_SPACE);
        this.keyDown = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
        this.keyDelete = new Key(GLFW.GLFW_KEY_DELETE);

        this.createBinding(this.keyA, "Шаг влево");
        this.createBinding(this.keyD, "Шаг вправо");
        this.createBinding(this.keyW, "Шаг вперед");
        this.createBinding(this.keyS, "Шаг назад");
        this.createBinding(this.keyUp, "Лететь вверх");
        this.createBinding(this.keyDown, "Лететь вниз");
        this.createBinding(this.keyDelete, "Delete");
    }
}
