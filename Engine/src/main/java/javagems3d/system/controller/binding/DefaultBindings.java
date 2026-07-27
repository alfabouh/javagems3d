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

package javagems3d.system.controller.binding;

import api.events.EventBus;
import api.events.EventLauncher;
import api.scripting.JavaToJsAPI;
import api.scripting.coding.env.internal.game.init.events.rendering.world.JSSceneWorldObjectsUpdateEvent;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.misc.JSFrameTicking;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.DefaultGamePanel;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.DefaultPausePanel;
import javagems3d.help.JGemsHelper;
import javagems3d.system.controller.components.FunctionalKey;
import javagems3d.system.controller.components.IKeyAction;
import javagems3d.system.controller.components.Key;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import org.lwjgl.glfw.GLFW;

public final class DefaultBindings extends BindingManager {
    public final Key keyA;
    public final Key keyD;
    public final Key keyW;
    public final Key keyS;
    public final Key keyX;
    public final Key keyUp;
    public final Key keyDown;
    public final Key keyClear;
    public final Key keyEsc;
    public final Key keyV;
    public final Key keyT;
    public final Key keyF11;
    public final Key keyF2;
    public final Key keySelection;

    public DefaultBindings() {
        this.keyA = new Key(GLFW.GLFW_KEY_A);
        this.keyD = new Key(GLFW.GLFW_KEY_D);
        this.keyW = new Key(GLFW.GLFW_KEY_W);
        this.keyS = new Key(GLFW.GLFW_KEY_S);
        this.keyUp = new Key(GLFW.GLFW_KEY_SPACE);
        this.keyDown = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
        this.keyClear = new FunctionalKey(e -> JGemsHelper.get().getPhysicsWorld().killItems(), GLFW.GLFW_KEY_B);
        this.keyX = new Key(GLFW.GLFW_KEY_X);
        this.keySelection = new Key(GLFW.GLFW_MOUSE_BUTTON_LEFT);

        this.keyV = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                JGemsResourceManager.reloadShaders();
            }
        }, GLFW.GLFW_KEY_F1);

        this.keyEsc = new FunctionalKey(e -> {
            if (e == IKeyAction.KeyAction.CLICK) {
                if (JGems3D.get().isCurrentGameMapValid()) {
                    if (JGems3D.get().isPaused()) {
                        if (!EventLauncher.pushEvent(new EventBus.OnUnPauseFromButtonPressEvent(JGemsHelper.get().getSceneWorld(), JGemsHelper.get().getPhysicsWorld()), null).isCancelled()) {
                            if (JGems3D.get().getScreen().getControllerDispatcher().getCurrentController() instanceof MouseKeyboardController) {
                                ((MouseKeyboardController) JGemsHelper.controller().getControllerDispatcher().getCurrentController()).setCursorInCenter();
                                ((MouseKeyboardController) JGemsHelper.controller().getControllerDispatcher().getCurrentController()).getMouseAndKeyboard().forceInterruptLMB();
                                ((MouseKeyboardController) JGemsHelper.controller().getControllerDispatcher().getCurrentController()).getMouseAndKeyboard().forceInterruptRMB();
                                ((MouseKeyboardController) JGemsHelper.controller().getControllerDispatcher().getCurrentController()).getMouseAndKeyboard().forceInterruptMMB();
                            }
                            JGemsHelper.state().resumeGame();
                            JGems3D.get().getScreen().getWindow().setFocus(true);
                        }
                    } else {
                        if (!EventLauncher.pushEvent(new EventBus.OnPauseFromButtonPressEvent(JGemsHelper.get().getSceneWorld(), JGemsHelper.get().getPhysicsWorld()), null).isCancelled()) {
                            JGemsHelper.state().pauseGame(true);
                            JGems3D.get().getScreen().getWindow().setFocus(false);
                        }
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
               // JGems3D.get().getScreen().getScene().getSceneRenderer().takeScreenShot();
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