package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.controller.base.MouseKeyboardController;
import org.joml.Vector2i;
import workbench.WBench;

public class EditorInterface implements DearUIInterface {
    @Override
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        ImGui.begin(WBench.get().toString(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(windowSize.x * 0.5f, windowSize.y * 0.5f);
        ImGui.setWindowPos(windowSize.x * 0.25f, windowSize.y * 0.25f);
        ImGui.text("Loading...");
        ImGui.end();
    }
}
