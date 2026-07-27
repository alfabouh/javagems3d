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

package workbench.graphics.scene.ui.game.editor.scenes.mapping;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import javagems3d.system.service.files.JGemsPath;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.mapping.MapProjectPreview;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ScenePreviewMapG {
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final ImString imStringDesc;

    public ScenePreviewMapG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.imStringDesc = new ImString(256);
    }

    public void render() {
        final MapProjectPreview projectData = this.resourcesInterfaceComponentG.getMapResourceTreeDrawer().getPreviewWrapperObject();
        if (projectData != null && projectData.getAsset() != null) {
            final String mapProject = projectData.getAsset().getMapProject().getMapName();
            if (ImGui.collapsingHeader("Map: " + projectData.getAsset().getMapProject().getMapName(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##map_preview", ImGui.getColumnWidth(), 200, true);
                ImGui.indent();
                if (ImGui.beginPopup("NewMapInfoPopup")) {
                    ImGui.text("New Description:");
                    ImGui.inputText("##mapDesc", this.imStringDesc);

                    if (ImGui.button("Enter")) {
                        WBench.get().getMapProjectManager().editMapProjectDescription(projectData.getAsset().getMapProject(), this.imStringDesc.toString());
                        ImGui.closeCurrentPopup();
                        this.imStringDesc.clear();
                    }
                    ImGui.sameLine();
                    if (ImGui.button("Cancel")) {
                        this.imStringDesc.clear();
                        ImGui.closeCurrentPopup();
                    }
                    ImGui.endPopup();
                }

                ImGui.textWrapped("Map: " + projectData.getAsset().getMapProject().getMapName());
                ImGui.textWrapped("Description: " + projectData.getAsset().getMapProject().getMapDescription());
                ImGui.textWrapped("Version: " + projectData.getAsset().getMapProject().getVersion());
                final File dataFile = projectData.getAsset().getMapProject().getPathToDataMapFile().toFile();
                if (dataFile.exists()) {
                    final String modifiedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(Instant.ofEpochMilli(dataFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    ImGui.textWrapped("Map Data: " + dataFile);
                    ImGui.textWrapped("Modified: " + modifiedDate);
                }
                ImGui.spacing();
                if (ImGui.button("Open")) {
                    final JGemsPath pathToMap = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), mapProject);
                    WBench.get().getGameProjectManager().saveGameProject(true);
                    WBench.get().getMapProjectManager().openMapProject(pathToMap);
                    WBench.get().getGameProjectManager().refreshMaps(true);
                }
                ImGui.sameLine();
                if (ImGui.button("Edit Description")) {
                    ImGui.openPopup("NewMapInfoPopup");
                }
                ImGui.unindent();
                ImGui.endChild();
            }
        }
    }
}
