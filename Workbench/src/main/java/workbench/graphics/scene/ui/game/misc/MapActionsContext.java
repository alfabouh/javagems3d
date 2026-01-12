package workbench.graphics.scene.ui.game.misc;

import imgui.ImGui;
import imgui.type.ImString;
import javagems3d.JGems3D;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.WBench;
import workbench.graphics.scene.ui.game.GameEditorInterface;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.project.map.WBenchMapProject;

public class MapActionsContext implements IActionsBlockContext<ResourcesInterfaceComponentG> {
    private final WBenchMapProject mapProject;
    private final ImString editPopupText;
    private String editTextErrString;

    public MapActionsContext(WBenchMapProject mapProject) {
        this.mapProject = mapProject;
        this.editPopupText = new ImString();
    }

    @Override
    public void onRender(@Nullable ResourcesInterfaceComponentG resourcesInterfaceComponentG, @NotNull GameEditorInterface gameEditorInterface) {
        ImGui.beginPopup("_editname");
        ImGui.text("Edit: ");
        ImGui.inputText("#edit_to", this.editPopupText);
        if (ImGui.button("Enter")) {
            this.editTextErrString = null;
            String text = this.editPopupText.toString();
            if (text.isEmpty()) {
                this.editTextErrString = "Enter map name!";
            }
            if (!text.matches("[a-zA-Z\\d\\s]+")) {
                this.editTextErrString = "Use valid symbols!";
            }

            if (this.editTextErrString == null) {
                final String mapNameFile = text + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE;
                final JGemsPath absPath = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), text);
                this.mapProject.setMapName(text);

                if (WBench.get().getMapProjectManager().createMapProject(absPath, new JGemsPath(absPath, mapNameFile), text)) {
                    WBench.get().getGameProjectManager().getCurrentGameProject().getMaps().add(text);
                    WBench.get().getGameProjectManager().saveGameProject(true);
                }
                ImGui.closeCurrentPopup();
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            ImGui.closeCurrentPopup();
        }
        ImGui.endPopup();

        ImGui.text("Project Title: ");
        ImGui.text(this.mapProject.getMapName());
    }

    public static class MapContext {
        public String mapName;
        public String mapDescription;

        public final String mapVersion;
        public final int mapFilesSize;
        public final JGemsPath pathToMapFile;

        public MapContext(@NotNull JGemsPath pathToMapFile) {
            this.pathToMapFile = pathToMapFile;
        }

        private void loadMapData(@NotNull JGemsPath path) {
            WBenchMapProject mapProject1 = JSONFileManaging.create()
        }
    }
}
