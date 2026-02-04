package workbench.graphics.scene.ui;

import imgui.ImGui;
import logger.managers.LoggingManager;
import org.lwjgl.glfw.GLFW;

public abstract class ProjectUIUtils {
    public static boolean ctrlS() {
        return ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed(GLFW.GLFW_KEY_S, false);
    }

    public static boolean ctrlC() {
        return ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed(GLFW.GLFW_KEY_C, false);
    }

    public static void consoleContent() {
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
    }
}
