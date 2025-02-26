package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.help.JGemsFilesHelper;
import javagems3d.system.controller.base.MouseKeyboardController;
import org.joml.Vector2i;
import workbench.WBench;

public class ProjectInitInterface implements DearUIInterface {
    private final ImString projectName = new ImString(256);
    private final ImString projectPath = new ImString(512);

    private boolean pressed;

    public ProjectInitInterface() {
        this.clear();
    }

    public void clear() {
        this.projectName.set("");
        this.projectPath.set("");
        this.pressed = false;
    }

    @Override
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        ImGui.begin(this + " | " + WBench.get().toString(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(windowSize.x * 0.5f, windowSize.y * 0.5f);
        ImGui.setWindowPos(windowSize.x * 0.25f, windowSize.y * 0.25f);

        boolean f1 = this.pressed && this.projectName.get().isEmpty();
        boolean f2 = this.pressed && this.projectPath.get().isEmpty();

        if (f1) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        ImGui.text("Project name:");
        ImGui.inputText("##project_name", this.projectName);
        if (f1) {
            ImGui.popStyleColor();
        }

        if (f2) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        ImGui.text("Project path:");
        ImGui.inputText("##project_path", this.projectPath);
        if (f2) {
            ImGui.popStyleColor();
        }

        ImGui.sameLine();
        if (ImGui.button("View")) {
            projectPath.set(JGemsFilesHelper.openFolderViewer(""));
        }

        if (ImGui.button("Create project", 120, 30)) {
            this.pressed = true;
        }

        ImGui.separator();

        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.2f, 0.9f, 1.0f);
        if (ImGui.button("Open project", 120, 30)) {
            //projectPath.set(JGemsFilesHelper.openFolderViewer(""));
        }
        ImGui.popStyleColor();

        ImGui.end();
    }

    @Override
    public String toString() {
        return "HUB";
    }
}
