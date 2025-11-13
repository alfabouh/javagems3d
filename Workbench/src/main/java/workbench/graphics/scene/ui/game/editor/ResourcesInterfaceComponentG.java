package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import javagems3d.JGems3D;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import workbench.WBench;
import workbench.project.game.WBenchGameProjectManager;

import java.io.File;
import java.util.ArrayList;

public class ResourcesInterfaceComponentG {
    private CreateMapContext createMapContext;

    public ResourcesInterfaceComponentG() {
        this.createMapContext = new CreateMapContext();
    }

    public void clear() {
    }

    public void resourcesContent() {
        if (ImGui.collapsingHeader("Maps")) {
            this.Maps();
        }
    }

    private void Maps() {
        if (ImGui.beginPopup("NewMapPopup")) {
            ImGui.text("Enter map name:");
            ImGui.inputText("##mapName", this.createMapContext.getMapNamePopup());

            if (this.createMapContext.errTest != null) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
                ImGui.text(this.createMapContext.errTest);
                ImGui.popStyleColor();
            }

            if (ImGui.button("Create")) {
                this.createMapContext.setErrTest(null);
                String name = this.createMapContext.getMapNamePopup().get();
                if (name.isEmpty()) {
                    this.createMapContext.errTest = "Enter map name!";
                }
                if (!name.matches("[a-zA-Z\\d\\s]+")) {
                    this.createMapContext.errTest = "Use valid symbols!";
                }

                if (this.createMapContext.errTest == null) {
                    final String mapNameFile = name + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE;
                    final JGemsPath absPath = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), name);
                    if (WBench.get().getMapProjectManager().createMapProject(absPath, new JGemsPath(absPath, mapNameFile), name)) {
                        WBench.get().getGameProjectManager().getCurrentGameProject().getMaps().add(name);
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
        }
        if (ImGui.button("Refresh")) {
            WBench.get().getGameProjectManager().refreshMapsFolderData();
        }
        ImGui.sameLine();
        if (ImGui.button("Create")) {
            this.createMapContext.clear();
            ImGui.openPopup("NewMapPopup");
        }
        ImGui.sameLine();
        if (ImGui.button("Open Folder")) {
            try {
                File folder = new File(WBench.get().getGameProjectManager().getMapsPath().getFullPath());
                if (folder.exists()) {
                    java.awt.Desktop.getDesktop().open(folder);
                } else {
                    Log.get().error("Folder not found: " + folder.getAbsolutePath());
                }
            } catch (Exception e) {
                Log.get().error("Failed to open folder: " + e.getMessage());
            }
        }
        ImGui.separator();
        ImGui.treePush();
        if (ImGui.treeNodeEx("List:", ImGuiTreeNodeFlags.DefaultOpen)) {
            if (WBench.get().getGameProjectManager().getCurrentGameProject().getMaps().isEmpty()) {
                ImGui.text("<Empty>");
            } else {
                try {
                    for (String mapProject : new ArrayList<>(WBench.get().getGameProjectManager().getCurrentGameProject().getMaps())) {
                        if (ImGui.selectable(mapProject)) {
                            final JGemsPath pathToMap = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), mapProject);
                            File file = new File(pathToMap.getFullPath());
                            if (!file.exists()) {
                                throw new JGemsIOException("Couldn't open file: " + mapProject);
                            }
                            WBench.get().getMapProjectManager().openMapProject(pathToMap);
                            WBench.get().getGameProjectManager().refreshMapsFolderData();
                        }
                    }
                } catch (JGemsIOException e) {
                    Log.get().exception(e);
                    LoggingManager.showExceptionDialog("Error!", e);
                }
            }
            ImGui.treePop();
        }
        ImGui.treePop();
    }

    private static class CreateMapContext {
        private String errTest;
        private final ImString mapNamePopup;
        private final ImString mapDescriptionPopup;

        public CreateMapContext() {
            this.mapNamePopup = new ImString();
            this.mapDescriptionPopup = new ImString();
            this.errTest = null;
        }

        public void clear() {
            this.getMapNamePopup().clear();
            this.getMapDescriptionPopup().clear();
            this.errTest = null;
        }

        public String getErrTest() {
            return this.errTest;
        }

        public CreateMapContext setErrTest(String errTest) {
            this.errTest = errTest;
            return this;
        }

        public ImString getMapNamePopup() {
            return this.mapNamePopup;
        }

        public ImString getMapDescriptionPopup() {
            return this.mapDescriptionPopup;
        }
    }
}
