package javagems3d.graphics.rendering.ui.dear_imgui.interfaces;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.global.JGemsGlobalConfiguration;
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
        String[] textLines = LoggingManager.consoleText().split("\n");
        for (String s : textLines) {
            if (s.isEmpty()) {
                continue;
            }
            ImGui.textWrapped(s);
        }
        if (LoggingManager.markConsoleDirty) {
            ImGui.setScrollHereY(1.0f);
            LoggingManager.markConsoleDirty = false;
        }
        ImGui.end();

        ImGui.setNextWindowSize(JGemsGlobalConfiguration.DEFAULT_SCREEN_WIDTH / 3.0f, JGemsGlobalConfiguration.DEFAULT_SCREEN_HEIGHT / 3.0f, ImGuiCond.Once);
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.begin("Debug");
        ImGui.text("FPS: " + JGemsScreen.RENDER_FPS + " | TPS: " + JGemsScreen.PHYS_TPS);
        ImGui.end();
    }
}
