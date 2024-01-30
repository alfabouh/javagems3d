package ru.BouH.engine.game.controller.input;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.binding.Binding;
import ru.BouH.engine.game.controller.components.Key;
import ru.BouH.engine.render.screen.window.Window;

public class Keyboard {
    private final Window window;

    public Keyboard(Window window) {
        this.window = window;
    }

    public static boolean isPressedKey(int keyCode) {
        return GLFW.glfwGetKey(Game.getGame().getScreen().getWindow().getDescriptor(), keyCode) == GLFW.GLFW_PRESS;
    }

    public boolean isKeyPressed(int keyCode) {
        return GLFW.glfwGetKey(this.getWindow().getDescriptor(), keyCode) == GLFW.GLFW_PRESS;
    }

    public void updateKeys() {
        Binding.getBindingList().forEach(e -> e.getKey().refreshState(this.isKeyPressed(e.getKey())));
    }

    public boolean isKeyPressed(Key key) {
        return this.isKeyPressed(key.getKeyCode());
    }

    public Window getWindow() {
        return this.window;
    }
}
