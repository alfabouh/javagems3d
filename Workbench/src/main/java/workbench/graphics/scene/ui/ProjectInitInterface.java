/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.help.JGemsHelper;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.service.files.JGemsPath;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.settings.WBenchSettings;

import java.util.Iterator;

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
        ImGui.text("Game project name:");
        ImGui.inputText("##game_project_name", this.projectName, ImGuiInputTextFlags.CallbackCharFilter, new ImGuiInputTextCallback() {
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
        ImGui.text("Game project files:");
        ImGui.inputText("##game_project_path", this.projectPath);
        if (f2) {
            ImGui.popStyleColor();
        }

        ImGui.sameLine();
        if (ImGui.button("View")) {
            projectPath.set(JGemsHelper.files().openFolderViewChooser(""));
        }

        if (ImGui.button("Create project", 120, 30)) {
            this.pressed = true;
            String projectPath = this.projectPath.get();
            String projectName = this.projectName.get();
            if (!projectPath.isEmpty() && !projectName.isEmpty()) {
                //WBench.get().getMapProjectManager().createMapProject(new JGemsPath(projectPath), new JGemsPath(projectPath, projectName + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE), projectName);
                projectPath += "\\" + projectName;
                WBench.get().getGameProjectManager().createGameProject(new JGemsPath(projectPath), new JGemsPath(projectPath, projectName + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_PROJECT_FILE), projectName);
                WBench.get().getSettings().addPath(projectPath);
            }
        }
        ImGui.sameLine();
        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.2f, 0.9f, 1.0f);
        if (ImGui.button("Open project", 120, 30)) {
            String projectPath = JGemsHelper.files().openFolderViewChooser("");
            if (!projectPath.isEmpty() && WBench.get().getMapProjectManager().getCurrentMapProject() == null) {
                //WBench.get().getMapProjectManager().openMapProject(new JGemsPath(projectPath));
                WBench.get().getGameProjectManager().openGameProject(new JGemsPath(projectPath));
                WBench.get().getSettings().addPath(projectPath);
            }
        }
        ImGui.popStyleColor();

        WBenchSettings wBenchSettings = WBench.get().getSettings();
        if (wBenchSettings != null && wBenchSettings.getRecentProjects() != null) {
            ImGui.separator();
            ImGui.text("Recent: ");
            Iterator<String> projectPaths = wBenchSettings.getRecentProjects().iterator();
            while (projectPaths.hasNext()) {
                String projectPath = projectPaths.next();
                ImGui.text(projectPath);
                ImGui.sameLine();
                if (ImGui.button("Open##" + projectPath) && WBench.get().getMapProjectManager().getCurrentMapProject() == null) {
                    if (!WBench.get().getGameProjectManager().openGameProject(new JGemsPath(projectPath))) {
                        projectPaths.remove();
                    }
                    //WBench.get().getMapProjectManager().openMapProject(new JGemsPath(projectPath));
                }
            }
        }

        ImGui.end();
    }

    @Override
    public String toString() {
        return "Editor's HUB";
    }
}
