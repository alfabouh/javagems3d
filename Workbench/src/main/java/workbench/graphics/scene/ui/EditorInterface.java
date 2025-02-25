package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.controller.base.MouseKeyboardController;
import org.joml.Vector2i;
import toolbox.ToolBox;
import workbench.WBench;

public class EditorInterface implements DearUIInterface {
    @Override
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        ImGui.begin(WBench.get().toString(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(400, 200);
        ImGui.setWindowPos(windowSize.x * 0.5f - 200, windowSize.y * 0.5f - 100);
        ImGui.text("Loading...");
        ImGui.end();
    }
}
