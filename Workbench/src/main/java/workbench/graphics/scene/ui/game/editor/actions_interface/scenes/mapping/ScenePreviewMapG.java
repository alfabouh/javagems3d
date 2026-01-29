package workbench.graphics.scene.ui.game.editor.actions_interface.scenes.mapping;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.resources_interface.ResourcesInterfaceComponentG;
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
                ImGui.treePush();
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
                if (!projectData.getAsset().getMapProject().getMapDataFile().isEmpty()) {
                    final JGemsPath pathToMapDataFile = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), mapProject, projectData.getAsset().getMapProject().getMapDataFile());
                    final String modifiedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(Instant.ofEpochMilli(pathToMapDataFile.toFile().lastModified()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    ImGui.textWrapped("Map Data: " + projectData.getAsset().getMapProject().getMapDataFile());
                    ImGui.textWrapped("Modified: " + modifiedDate);
                }
                ImGui.separator();
                if (ImGui.button("Open")) {
                    final JGemsPath pathToMap = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), mapProject);
                    File file = new File(pathToMap.getFullPath());
                    if (!file.exists()) {
                        throw new JGemsIOException("Couldn't open file: " + mapProject);
                    }
                    WBench.get().getMapProjectManager().openMapProject(pathToMap);
                    WBench.get().getGameProjectManager().refreshMaps(true);
                }
                ImGui.sameLine();
                if (ImGui.button("Edit Description")) {
                    ImGui.openPopup("NewMapInfoPopup");
                }
                ImGui.endChild();
                ImGui.treePop();
            }
        }
    }
}
