package ru.alfabouh.engine.system.controller.binding;

import org.lwjgl.glfw.GLFW;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.gui.panels.GamePlayPanel;
import ru.alfabouh.engine.render.scene.gui.panels.PausePanel;
import ru.alfabouh.engine.system.controller.ControllerDispatcher;
import ru.alfabouh.engine.system.controller.components.FunctionalKey;
import ru.alfabouh.engine.system.controller.components.IKeyAction;
import ru.alfabouh.engine.system.controller.components.Key;
import ru.alfabouh.engine.system.controller.input.MouseKeyboardController;
import ru.alfabouh.engine.system.resources.ResourceManager;

public class BindingList {
    public final Key keyA;
    public final Key keyD;
    public final Key keyW;
    public final Key keyS;
    public final Key keyX;
    public final Key keyUp;
    public final Key keyDown;
    public final Key keyBlock1;
    public final Key keyBlock2;
    public final Key keyBlock3;
    public final Key keyClear;
    public final Key keyEsc;
    public final Key keyV;
    public final Key keyT;
    public final Key keyF11;
    public final Key keySelection;

    public BindingList() {
        this.keyA = new Key(GLFW.GLFW_KEY_A);
        this.keyD = new Key(GLFW.GLFW_KEY_D);
        this.keyW = new Key(GLFW.GLFW_KEY_W);
        this.keyS = new Key(GLFW.GLFW_KEY_S);
        this.keyUp = new Key(GLFW.GLFW_KEY_SPACE);
        this.keyDown = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
        this.keyBlock1 = new Key(GLFW.GLFW_KEY_F);
        this.keyBlock2 = new Key(GLFW.GLFW_KEY_C);
        this.keyBlock3 = new Key(GLFW.GLFW_KEY_G);
        this.keyClear = new FunctionalKey(e -> JGems.get().getPhysicsWorld().clearAllItems(), GLFW.GLFW_KEY_B);
        this.keyX = new Key(GLFW.GLFW_KEY_X);
        this.keySelection = new Key(GLFW.GLFW_MOUSE_BUTTON_LEFT);

        this.keyV = new FunctionalKey(e -> {
            ResourceManager.reloadShaders();
        }, GLFW.GLFW_KEY_V);

        this.keyEsc = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                if (JGems.get().isCurrentMapIsValid()) {
                    if (JGems.get().getEngineState().isPaused()) {
                        if (JGems.get().getScreen().getControllerDispatcher().getCurrentController() instanceof MouseKeyboardController) {
                            ControllerDispatcher.mouseKeyboardController.setCursorInCenter();
                            ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptLMB();
                            ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptRMB();
                            ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptMMB();
                        }
                        JGems.get().unPauseGame();
                        JGems.get().getScreen().getWindow().setInFocus(true);
                        JGems.get().getUI().setPanel(new GamePlayPanel(null));
                    } else {
                        JGems.get().pauseGame(true);
                        JGems.get().getScreen().getWindow().setInFocus(false);
                        JGems.get().getUI().setPanel(new PausePanel(null));
                    }
                }
            }
        }, GLFW.GLFW_KEY_ESCAPE);

        this.keyF11 = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                JGems.get().getScreen().switchScreenMode();
            }
        }, GLFW.GLFW_KEY_F11);

        this.keyT = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                JGems.get().getScreen().getWindow().switchFocus();
            }
        }, GLFW.GLFW_KEY_T);

        if (JGems.DEBUG_MODE) {
            Binding.createBinding(this.keyV, "Reload shaders");
            Binding.createBinding(this.keyT, "Фокус");
            Binding.createBinding(this.keyClear, "Очистка");
            Binding.createBinding(this.keyBlock1, "Куб статичный");
            Binding.createBinding(this.keyBlock2, "Куб-фонарь");
            Binding.createBinding(this.keyBlock3, "Куб реалистичный");
        }

        Binding.createBinding(this.keyA, "Шаг влево");
        Binding.createBinding(this.keyD, "Шаг вправо");
        Binding.createBinding(this.keyW, "Шаг вперед");
        Binding.createBinding(this.keyS, "Шаг назад");
        Binding.createBinding(this.keyEsc, "Закрыть экран");
        Binding.createBinding(this.keyUp, "Лететь вверх");
        Binding.createBinding(this.keyDown, "Лететь вниз");
        Binding.createBinding(this.keyX, "X");
        Binding.createBinding(this.keyF11, "FullScreen");
        Binding.createBinding(this.keySelection, "Выбрать объект");
    }
}
