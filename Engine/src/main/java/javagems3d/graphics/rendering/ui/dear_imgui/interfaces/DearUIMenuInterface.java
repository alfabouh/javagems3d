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

package javagems3d.graphics.rendering.ui.dear_imgui.interfaces;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.system.controller.base.MouseKeyboardController;
import logger.managers.LoggingManager;
import org.joml.Vector2i;

public class DearUIMenuInterface implements DearUIInterface {
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        float logX = (float) windowSize.x / 3;
        float logY = (float) windowSize.y / 1.5f;

        ImGui.setNextWindowPos(windowSize.x - logX, 0, ImGuiCond.Always);
        ImGui.setNextWindowSize(logX, logY);
        ImGui.setNextWindowCollapsed(true, ImGuiCond.Once);
        ImGui.begin("Output", ImGuiWindowFlags.AlwaysVerticalScrollbar | ImGuiWindowFlags.NoResize);
        DearUIGameInterface.consoleContent();
        ImGui.end();

        ImGui.setNextWindowSize(JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 3.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 3.0f, ImGuiCond.Once);
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.begin("Debug");
        ImGui.text("FPS: " + JGemsScreen.RENDER_FPS + " | TPS: " + JGemsScreen.PHYS_TPS);
        ImGui.end();
    }
}
