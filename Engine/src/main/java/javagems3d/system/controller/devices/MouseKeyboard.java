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

package javagems3d.system.controller.devices;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.components.Key;

public class MouseKeyboard {
    private final IWindow window;
    public boolean scrollUpdate;
    private int scrollVector;
    private boolean isInWindowBounds;
    private boolean flag1;
    private boolean flag2;
    private boolean flag3;
    private boolean forceInterruptLMB;
    private boolean forceInterruptRMB;
    private boolean forceInterruptMMB;

    public MouseKeyboard(IWindow window) {
        this.window = window;
        GLFW.glfwSetCursorEnterCallback(window.getDescriptor(), (getWindow, entered) -> this.isInWindowBounds = entered);
        GLFW.glfwSetScrollCallback(window.getDescriptor(), (getWindow, x, y) -> {
            if (y > 0) {
                this.scrollVector = 1;
            }
            if (y < 0) {
                this.scrollVector = -1;
            }
            this.scrollUpdate = true;
        });
    }

    public void update(BindingManager bindingManager) {
        bindingManager.getBindingSet().forEach(e -> e.getKey().refreshState(this.isKeyPressed(e.getKey())));

        this.flag1 = GLFW.glfwGetMouseButton(this.window.getDescriptor(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
        this.flag2 = GLFW.glfwGetMouseButton(this.window.getDescriptor(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS;
        this.flag3 = GLFW.glfwGetMouseButton(this.window.getDescriptor(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS;

        if (this.forceInterruptLMB) {
            if (!this.flag1) {
                this.forceInterruptLMB = false;
            } else {
                this.flag1 = false;
            }
        }

        if (this.forceInterruptRMB) {
            if (!this.flag2) {
                this.forceInterruptRMB = false;
            } else {
                this.flag2 = false;
            }
        }

        if (this.forceInterruptMMB) {
            if (!this.flag3) {
                this.forceInterruptMMB = false;
            } else {
                this.flag3 = false;
            }
        }

        if (this.scrollUpdate) {
            this.scrollUpdate = false;
        } else {
            this.scrollVector = 0;
        }
    }

    public boolean isKeyPressed(int keyCode) {
        if (keyCode >= GLFW.GLFW_MOUSE_BUTTON_1 && keyCode <= GLFW.GLFW_MOUSE_BUTTON_8) {
            return GLFW.glfwGetMouseButton(this.getWindow().getDescriptor(), keyCode) == GLFW.GLFW_PRESS;
        }
        return GLFW.glfwGetKey(this.getWindow().getDescriptor(), keyCode) == GLFW.GLFW_PRESS;
    }

    public boolean isKeyPressed(Key key) {
        return this.isKeyPressed(key.getKeyCode());
    }

    public IWindow getWindow() {
        return this.window;
    }

    public Vector2f getCursorCoordinatesV2F() {
        double[] d1 = this.getCursorCoordinates();
        return new Vector2f((float) d1[0], (float) d1[1]);
    }

    public double[] getCursorCoordinates() {
        double[] dx = new double[1];
        double[] dy = new double[1];
        GLFW.glfwGetCursorPos(this.getWindow().getDescriptor(), dx, dy);
        return new double[]{dx[0], dy[0]};
    }

    public void setCursorCoordinates(double[] xy) {
        GLFW.glfwSetCursorPos(this.window.getDescriptor(), xy[0], xy[1]);
    }

    public boolean isCursorInWindowBounds() {
        return this.isInWindowBounds;
    }

    public void forceInterruptLMB() {
        this.forceInterruptLMB = true;
        this.flag1 = false;
    }

    public void forceInterruptRMB() {
        this.forceInterruptRMB = true;
        this.flag2 = false;
    }

    public void forceInterruptMMB() {
        this.forceInterruptMMB = true;
        this.flag3 = false;
    }

    public boolean isLeftKeyPressed() {
        return this.flag1;
    }

    public boolean isRightKeyPressed() {
        return this.flag2;
    }

    public boolean isMiddleKeyPressed() {
        return this.flag3;
    }

    public int getScrollVector() {
        return this.scrollVector;
    }
}
