package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.managers.LoggingManager;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.instances.ModelPreviewAsset;
import workbench.project.map.WBenchMapProject;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActionsInterfaceComponentG {
    private static ImInt currentAnimation = new ImInt(0);
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final ImString imStringDesc;
    public float modelPreviewScaling;
    private boolean showAABB;
    private boolean showChessTerrain;
    private boolean flipModel;

    public ActionsInterfaceComponentG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.imStringDesc = new ImString(256);
        this.showChessTerrain = true;
    }

    public void actionsContent() {
        this.map();
        this.modelPreview();
    }

    public static void reset() {
        ActionsInterfaceComponentG.currentAnimation.set(0);
    }

    public boolean isFlipModel() {
        return this.flipModel;
    }

    public float getModelPreviewScaling() {
        return this.modelPreviewScaling;
    }

    public boolean isShowChessTerrain() {
        return this.showChessTerrain;
    }

    public boolean isShowAABB() {
        return this.showAABB;
    }

    private void modelPreview() {
        ModelPreviewAsset modelPreviewAsset = this.resourcesInterfaceComponentG.getModelPreviewAsset();
        if (modelPreviewAsset != null) {
            modelPreviewAsset.updateAnimation();
            if (ImGui.collapsingHeader(modelPreviewAsset.getModelAsset().getName(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.indent();
                ImGui.bullet();
                ImGui.text("Nodes:");
                int totalVertexes = 0;
                int totalTriangles = 0;
                for (MeshNode<?> node : modelPreviewAsset.getModelAsset().getMeshGroup().getAllNodes()) {
                    totalVertexes += node.getMeshData().totalVertexes();
                    totalTriangles += node.getMeshData().numVertexIndexes() / 3;
                }
                ImGui.textWrapped("Total Nodes: " + modelPreviewAsset.getModelAsset().getMeshGroup().getAllNodes().size());
                ImGui.textWrapped("Solid Nodes: " + modelPreviewAsset.getModelAsset().getMeshGroup().getSolidNodes().size());
                ImGui.textWrapped("Transparent Nodes: " + modelPreviewAsset.getModelAsset().getMeshGroup().getBlendedTransparencyNodes().size());
                ImGui.textWrapped("Total Vertexes: " + totalVertexes);
                ImGui.textWrapped("Total Triangles: " + totalTriangles);
                if (!modelPreviewAsset.getModelAsset().getMeshGroup().getAnimationsList().isEmpty()) {
                    ImGui.separator();
                    ImGui.bullet();
                    ImGui.text("Animation:");
                    ImGui.textWrapped("Total Animations: " + modelPreviewAsset.getModelAsset().getMeshGroup().getAnimationsNum());
                    List<String> animations = new ArrayList<>();
                    animations.add("None");
                    for (Animation animation : modelPreviewAsset.getModelAsset().getMeshGroup().getAnimationsList()) {
                        animations.add(animation.getName());
                    }
                    if (ImGui.combo("List", ActionsInterfaceComponentG.currentAnimation, animations.toArray(new String[]{}))) {
                        modelPreviewAsset.setAnimationByID(ActionsInterfaceComponentG.currentAnimation.get() - 1);
                    }
                    if (ActionsInterfaceComponentG.currentAnimation.get() > 0) {
                        ImGui.indent();
                        ImGui.text("FPS: " + modelPreviewAsset.getAnimationData().getFps());
                        ImGui.text("Duration: " + modelPreviewAsset.getAnimationData().getCurrentAnimation().getDuration());
                        ImGui.text("Frame Count: " + modelPreviewAsset.getAnimationData().getCurrentAnimation().getFrameCount());
                        ImGui.unindent();
                    }
                }
                ImGui.separator();
                ImGui.text("Preview Tools:");
                float[] f1 = new float[] {this.modelPreviewScaling};
                if (ImGui.dragFloat("Distance", f1, 0.1f)) {
                    this.modelPreviewScaling = f1[0];
                }
                if (ImGui.checkbox("Show AABB", this.showAABB)) {
                    this.showAABB = !this.showAABB;
                }
                if (ImGui.checkbox("Show Chess Terrain", this.showChessTerrain)) {
                    this.showChessTerrain = !this.showChessTerrain;
                }
                if (ImGui.checkbox("Flip Model", this.flipModel)) {
                    this.flipModel = !this.flipModel;
                }
                ImGui.unindent();
            }
        }
    }

    private void map() {
        final WBenchMapProject projectData = this.resourcesInterfaceComponentG.getMapProjectPreview();
        if (projectData != null) {
            final String mapProject = projectData.getProjectName();
            if (ImGui.collapsingHeader(projectData.getProjectName(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.treePush();
                if (ImGui.beginPopup("NewMapInfoPopup")) {
                    ImGui.text("New Description:");
                    ImGui.inputText("##mapDesc", this.imStringDesc);

                    if (ImGui.button("Enter")) {
                        WBench.get().getMapProjectManager().editMapProjectDescription(projectData, this.imStringDesc.toString());
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

                ImGui.textWrapped("Map: " + projectData.getProjectName());
                ImGui.textWrapped("Description: " + projectData.getInformation());
                ImGui.textWrapped("Version: " + projectData.getVersion());
                if (!projectData.getMapDataFile().isEmpty()) {
                    final JGemsPath pathToMapDataFile = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), mapProject, projectData.getMapDataFile());
                    final String modifiedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(Instant.ofEpochMilli(pathToMapDataFile.toFile().lastModified()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    ImGui.textWrapped("Map Data: " + projectData.getMapDataFile());
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
                    WBench.get().getGameProjectManager().refreshMaps();
                }
                ImGui.sameLine();
                if (ImGui.button("Delete")) {
                    if (LoggingManager.showConfirmationWindowDialog("Are you sure?")) {
                        final JGemsPath pathToMap = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), mapProject);
                        try {
                            if (WBench.get().getGameProjectManager().getMapProjectManager().deleteMapProjectFolder(pathToMap)) {
                                WBench.get().getGameProjectManager().refreshMaps();
                                ImGui.endChild();
                                return;
                            } else {
                                LoggingManager.showWindowWarn("Couldn't delete path: " + pathToMap);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (ImGui.button("Edit Description")) {
                    ImGui.openPopup("NewMapInfoPopup");
                }
                ImGui.treePop();
            }
        }
    }
}
