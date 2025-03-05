package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.system.controller.base.MouseKeyboardController;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import workbench.WBench;

public class LoadingInterface implements DearUIInterface {
    public LoadingInterface() {
        LoadingInterface.setResource("...");
    }

    public static String resourceName = "";

    @Override
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        LoadingInterface.drawWindow(windowSize);
    }

    public static void drawWindow(Vector2i windowSize) {
        ImGui.begin(WBench.get().toString(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(400, 200);
        ImGui.setWindowPos(windowSize.x * 0.5f - 200, windowSize.y * 0.5f - 100);
        ImGui.text("Loading...");
        ImGui.text(LoadingInterface.resourceName);
        ImGui.end();
    }

    public static void setResource(String name) {
        GL46.glClearColor(0.0f, 0.0f, 0.2f, 1.0f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        LoadingInterface.resourceName = name;
        GLFW.glfwSwapBuffers(WBench.get().getScreen().getWindow().getDescriptor());
        GLFW.glfwPollEvents();
    }

    @Override
    public String toString() {
        return "HUB";
    }
}
