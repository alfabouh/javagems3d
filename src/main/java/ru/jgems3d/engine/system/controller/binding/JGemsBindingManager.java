package ru.jgems3d.engine.system.controller.binding;

import org.lwjgl.glfw.GLFW;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultGamePanel;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultPausePanel;
import ru.jgems3d.engine.system.controller.components.FunctionalKey;
import ru.jgems3d.engine.system.controller.components.IKeyAction;
import ru.jgems3d.engine.system.controller.components.Key;
import ru.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class JGemsBindingManager extends BindingManager {
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
    public final Key keyF2;
    public final Key keySelection;

    public JGemsBindingManager() {
        this.keyA = new Key(GLFW.GLFW_KEY_A);
        this.keyD = new Key(GLFW.GLFW_KEY_D);
        this.keyW = new Key(GLFW.GLFW_KEY_W);
        this.keyS = new Key(GLFW.GLFW_KEY_S);
        this.keyUp = new Key(GLFW.GLFW_KEY_SPACE);
        this.keyDown = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
        this.keyBlock1 = new Key(GLFW.GLFW_KEY_F);
        this.keyBlock2 = new Key(GLFW.GLFW_KEY_C);
        this.keyBlock3 = new Key(GLFW.GLFW_KEY_G);
        this.keyClear = new FunctionalKey(e -> JGems3D.get().getPhysicsWorld().killItems(), GLFW.GLFW_KEY_B);
        this.keyX = new Key(GLFW.GLFW_KEY_X);
        this.keySelection = new Key(GLFW.GLFW_MOUSE_BUTTON_LEFT);

        this.keyV = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                JGemsResourceManager.reloadShaders();
            }
        }, GLFW.GLFW_KEY_V);

        this.keyEsc = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                if (JGems3D.get().isCurrentMapIsValid()) {
                    if (JGems3D.get().getEngineState().isPaused()) {
                        if (JGems3D.get().getScreen().getControllerDispatcher().getCurrentController() instanceof MouseKeyboardController) {
                            JGemsControllerDispatcher.mouseKeyboardController.setCursorInCenter();
                            JGemsControllerDispatcher.mouseKeyboardController.getMouseAndKeyboard().forceInterruptLMB();
                            JGemsControllerDispatcher.mouseKeyboardController.getMouseAndKeyboard().forceInterruptRMB();
                            JGemsControllerDispatcher.mouseKeyboardController.getMouseAndKeyboard().forceInterruptMMB();
                        }
                        JGems3D.get().unPauseGame();
                        JGems3D.get().getScreen().getWindow().setInFocus(true);
                        JGems3D.get().getUI().setPanel(new DefaultGamePanel(null));
                    } else {
                        JGems3D.get().pauseGame(true);
                        JGems3D.get().getScreen().getWindow().setInFocus(false);
                        JGems3D.get().getUI().setPanel(new DefaultPausePanel(null));
                    }
                }
            }
        }, GLFW.GLFW_KEY_ESCAPE);

        this.keyF11 = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                JGems3D.get().getScreen().switchScreenMode();
            }
        }, GLFW.GLFW_KEY_F11);

        this.keyF2 = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                JGems3D.get().getScreen().getScene().getSceneRenderer().takeScreenShot();
            }
        }, GLFW.GLFW_KEY_F2);

        this.keyT = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                JGems3D.get().getScreen().getWindow().switchFocus();
            }
        }, GLFW.GLFW_KEY_T);

        if (JGems3D.DEBUG_MODE) {
            this.createBinding(this.keyV, "Reload shaders");
            this.createBinding(this.keyT, "Фокус");
            this.createBinding(this.keyClear, "Очистка");
            this.createBinding(this.keyBlock1, "Куб статичный");
            this.createBinding(this.keyBlock2, "Куб-фонарь");
            this.createBinding(this.keyBlock3, "Куб реалистичный");
            this.createBinding(this.keyF2, "Screenshot");
        }

        this.createBinding(this.keyA, "Шаг влево");
        this.createBinding(this.keyD, "Шаг вправо");
        this.createBinding(this.keyW, "Шаг вперед");
        this.createBinding(this.keyS, "Шаг назад");
        this.createBinding(this.keyEsc, "Закрыть экран");
        this.createBinding(this.keyUp, "Лететь вверх");
        this.createBinding(this.keyDown, "Лететь вниз");
        this.createBinding(this.keyX, "X");
        this.createBinding(this.keyF11, "FullScreen");
        this.createBinding(this.keySelection, "Выбрать объект");
    }
}
