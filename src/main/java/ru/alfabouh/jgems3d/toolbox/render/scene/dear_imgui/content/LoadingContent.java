package ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2i;
import ru.alfabouh.jgems3d.toolbox.ToolBox;

public class LoadingContent implements ImGuiContent {
    public LoadingContent() {
    }

    public void drawContent(Vector2i dim, float partialTicks) {
        ImGui.begin(ToolBox.get().toString(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(400, 200);
        ImGui.setWindowPos(dim.x * 0.5f - 200, dim.y * 0.5f - 100);
        ImGui.text("Loading...");
        ImGui.end();
    }
}
