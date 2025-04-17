package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.mapping.JGemsMapping;
import javagems3d.system.service.path.JGemsPath;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.settings.WBenchSettings;

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
        GL46.glClearColor(0.0f, 0.0f, 0.2f, 1.0f);
        ImGui.begin(this + " | " + WBench.get().toString(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(windowSize.x * 0.5f, windowSize.y * 0.5f);
        ImGui.setWindowPos(windowSize.x * 0.25f, windowSize.y * 0.25f);

        boolean f1 = this.pressed && this.projectName.get().isEmpty();
        boolean f2 = this.pressed && this.projectPath.get().isEmpty();

        if (f1) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        ImGui.text("WBenchProject name:");
        ImGui.inputText("##project_name", this.projectName, ImGuiInputTextFlags.CallbackCharFilter, new ImGuiInputTextCallback() {
            @Override
            public void accept(ImGuiInputTextCallbackData data) {
                char c = (char) data.getEventChar();
                if (!Character.isLetterOrDigit(c)) {
                    data.setEventChar((char) 0);
                }
            }
        });
        if (f1) {
            ImGui.popStyleColor();
        }

        if (f2) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        ImGui.text("WBenchProject path:");
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
            String projectPath = this.projectPath.get();
            String projectName = this.projectName.get();
            if (!projectPath.isEmpty() && !projectName.isEmpty()) {
                WBench.get().getProjectManager().createProject(new JGemsPath(projectPath), new JGemsPath(projectPath, projectName + JGemsMapping.MAP_PROJECT_FILE), projectName);
                WBench.get().getSettings().addPath(projectPath);
            }
        }
        ImGui.sameLine();
        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.2f, 0.9f, 1.0f);
        if (ImGui.button("Open project", 120, 30)) {
            String projectPath = JGemsFilesHelper.openFolderViewer("");
            if (!projectPath.isEmpty() && WBench.get().getProjectManager().getCurrentProject() == null) {
                WBench.get().getProjectManager().openProject(new JGemsPath(projectPath));
                WBench.get().getSettings().addPath(projectPath);
            }
        }
        ImGui.popStyleColor();

        WBenchSettings wBenchSettings = WBench.get().getSettings();
        if (wBenchSettings != null && wBenchSettings.getRecentProjects() != null) {
            ImGui.separator();
            ImGui.text("Recent: ");
            for (String projectPath : wBenchSettings.getRecentProjects()) {
                ImGui.text(projectPath);
                ImGui.sameLine();
                if (ImGui.button("Open##" + projectPath) && WBench.get().getProjectManager().getCurrentProject() == null) {
                    WBench.get().getProjectManager().openProject(new JGemsPath(projectPath));
                }
            }
        }

        ImGui.end();
    }

    @Override
    public String toString() {
        return "HUB";
    }
}
