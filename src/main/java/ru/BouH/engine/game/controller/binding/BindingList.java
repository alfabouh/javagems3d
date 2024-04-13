package ru.BouH.engine.game.controller.binding;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.controller.components.FunctionalKey;
import ru.BouH.engine.game.controller.components.IKeyAction;
import ru.BouH.engine.game.controller.components.Key;
import ru.BouH.engine.game.controller.input.IController;
import ru.BouH.engine.game.controller.input.MouseKeyboardController;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.gui.InGameGUI;
import ru.BouH.engine.render.scene.gui.PauseMenuGUI;
import ru.BouH.engine.render.scene.world.camera.AttachedCamera;
import ru.BouH.engine.render.scene.world.camera.FreeCamera;
import ru.BouH.engine.render.scene.world.camera.ICamera;
import ru.BouH.engine.render.screen.Screen;

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
    public final Key keyZ;
    public final Key keyR;
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
        this.keyClear = new Key(GLFW.GLFW_KEY_B);
        this.keyX = new Key(GLFW.GLFW_KEY_X);
        this.keySelection = new Key(GLFW.GLFW_MOUSE_BUTTON_LEFT);

        this.keyV = new FunctionalKey(e -> {
            ResourceManager.shaderAssets.destroyShaders();
            ResourceManager.reloadShaders();
            ResourceManager.shaderAssets.startShaders();
        }, GLFW.GLFW_KEY_V);

        this.keyEsc = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                if (Game.getGame().isCurrentMapIsValid()) {
                    if (Game.getGame().getEngineState().isPaused()) {
                        if (Game.getGame().getScreen().getControllerDispatcher().getCurrentController() instanceof MouseKeyboardController) {
                            ControllerDispatcher.mouseKeyboardController.setCursorInCenter();
                            ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptLMB();
                            ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptRMB();
                            ControllerDispatcher.mouseKeyboardController.getMouse().forceInterruptMMB();
                        }
                        Game.getGame().unPauseGame();
                        Game.getGame().getScreen().getWindow().setInFocus(true);
                        Game.getGame().showGui(new InGameGUI());
                    } else {
                        Game.getGame().pauseGame(true);
                        Game.getGame().getScreen().getWindow().setInFocus(false);
                        Game.getGame().showGui(new PauseMenuGUI());
                    }
                }
                //Game.getGame().destroyGame();
            }
        }, GLFW.GLFW_KEY_ESCAPE);

        this.keyF11 = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                Game.getGame().getScreen().switchScreenMode();
            }
        }, GLFW.GLFW_KEY_F11);

        this.keyZ = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                Scene.setSceneDebugMode(Scene.getDebugMode() + 1);
            }
        }, GLFW.GLFW_KEY_Z);

        this.keyR = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                IController controller = Game.getGame().getScreen().getControllerDispatcher().getCurrentController();
                ICamera camera = Game.getGame().getScreen().getCamera();
                if (controller != null) {
                    if (camera != null) {
                        if (camera instanceof AttachedCamera) {
                            AttachedCamera attachedCamera = (AttachedCamera) camera;
                            Game.getGame().getScreen().getScene().enableFreeCamera(controller, attachedCamera.getCamPosition(), attachedCamera.getCamRotation());
                            Game.getGame().getScreen().getControllerDispatcher().detachController();
                        } else if (camera instanceof FreeCamera) {
                            Game.getGame().getScreen().getScene().enableAttachedCamera((WorldItem) Game.getGame().getPlayerSP());
                            Game.getGame().getScreen().getControllerDispatcher().attachControllerTo(controller, Game.getGame().getPlayerSP());
                        }
                    } else {
                        Game.getGame().getScreen().getScene().enableAttachedCamera((WorldItem) Game.getGame().getPlayerSP());
                    }
                }
            }
        }, GLFW.GLFW_KEY_R);

        this.keyT = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                Game.getGame().getScreen().getWindow().setInFocus(!Screen.isInFocus());
            }
        }, GLFW.GLFW_KEY_T);

        Binding.createBinding(this.keyV, "Reload shaders");
        Binding.createBinding(this.keyA, "Шаг влево");
        Binding.createBinding(this.keyD, "Шаг вправо");
        Binding.createBinding(this.keyW, "Шаг вперед");
        Binding.createBinding(this.keyS, "Шаг назад");
        Binding.createBinding(this.keyEsc, "Закрыть экран");
        Binding.createBinding(this.keyT, "Фокус");
        Binding.createBinding(this.keyUp, "Лететь вверх");
        Binding.createBinding(this.keyDown, "Лететь вниз");
        Binding.createBinding(this.keyR, "Режим камеры");
        Binding.createBinding(this.keyZ, "Режим отладки");
        Binding.createBinding(this.keyX, "X");
        Binding.createBinding(this.keyClear, "Очистка");
        Binding.createBinding(this.keyF11, "FullScreen");
        Binding.createBinding(this.keyBlock1, "Куб статичный");
        Binding.createBinding(this.keyBlock2, "Куб-фонарь");
        Binding.createBinding(this.keyBlock3, "Куб реалистичный");
        Binding.createBinding(this.keySelection, "Выбрать объект");
    }
}
