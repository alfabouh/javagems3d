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
