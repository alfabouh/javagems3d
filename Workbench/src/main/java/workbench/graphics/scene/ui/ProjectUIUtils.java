package workbench.graphics.scene.ui;

import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public abstract class ProjectUIUtils {
    public static boolean ctrlS() {
        return ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed(GLFW.GLFW_KEY_S, false);
    }

    public static boolean ctrlC() {
        return ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed(GLFW.GLFW_KEY_C, false);
    }
}
