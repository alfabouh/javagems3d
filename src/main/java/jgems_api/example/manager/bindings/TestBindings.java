package jgems_api.example.manager.bindings;

import org.lwjgl.glfw.GLFW;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultGamePanel;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultPausePanel;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.controller.components.FunctionalKey;
import ru.jgems3d.engine.system.controller.components.IKeyAction;
import ru.jgems3d.engine.system.controller.components.Key;
import ru.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class TestBindings extends BindingManager {
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

    public TestBindings() {
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
            this.addBinding(this.keyV);
            this.addBinding(this.keyT);
            this.addBinding(this.keyClear);
            this.addBinding(this.keyBlock1);
            this.addBinding(this.keyBlock2);
            this.addBinding(this.keyBlock3);
            this.addBinding(this.keyF2);
        }

        this.addBinding(this.keyA);
        this.addBinding(this.keyD);
        this.addBinding(this.keyW);
        this.addBinding(this.keyS);
        this.addBinding(this.keyEsc);
        this.addBinding(this.keyUp);
        this.addBinding(this.keyDown);
        this.addBinding(this.keyX);
        this.addBinding(this.keyF11);
        this.addBinding(this.keySelection);
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
        return this.keyDown;
    }
}
