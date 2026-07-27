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

package workbench.graphics.scene.ui;

import imgui.ImGui;
import logger.managers.LoggingManager;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

public abstract class ProjectUIUtils {
    public static boolean ctrlSPress() {
        return ImGui.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) && ImGui.isKeyPressed(GLFW.GLFW_KEY_S);
    }

    public static boolean ctrlGPress() {
        return ImGui.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) && ImGui.isKeyPressed(GLFW.GLFW_KEY_G);
    }

    public static boolean ctrlCPress() {
        return ImGui.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) && ImGui.isKeyPressed(GLFW.GLFW_KEY_C);
    }

    public static boolean ctrlS() {
        return ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed(GLFW.GLFW_KEY_S, false);
    }

    public static boolean ctrlC() {
        return ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed(GLFW.GLFW_KEY_C, false);
    }

    public static boolean ctrlG() {
        return ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed(GLFW.GLFW_KEY_G, false);
    }

    public static boolean ctrlZ() {
        return ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed(GLFW.GLFW_KEY_Z, false);
    }

    public static boolean ctrlY() {
        return ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed(GLFW.GLFW_KEY_Y, false);
    }

    public static boolean ctrl() {
        return ImGui.getIO().getKeyCtrl();
    }
}
