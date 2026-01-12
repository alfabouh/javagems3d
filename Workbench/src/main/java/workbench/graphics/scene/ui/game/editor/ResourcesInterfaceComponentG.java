package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import javagems3d.JGems3D;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.instances.ModelPreviewAsset;
import workbench.graphics.scene.ui.game.editor.instances.TexturePreviewAsset;
import workbench.project.managing.WBenchGameResourcesManager;
import workbench.project.map.WBenchMapProject;

import java.io.File;
import java.util.ArrayList;

public class ResourcesInterfaceComponentG {
    private ModelPreviewAsset modelPreviewAsset;
    private TexturePreviewAsset texturePreviewAsset;

    private CreateMapContext createMapContext;
    private WBenchMapProject mapProjectPreview;

    public ResourcesInterfaceComponentG() {
        this.createMapContext = new CreateMapContext();
    }

    public void clear() {
    }

    public void removeMapPreview() {
        this.mapProjectPreview = null;
    }

    public WBenchMapProject getMapProjectPreview() {
        return this.mapProjectPreview;
    }

    public ModelPreviewAsset getModelPreviewAsset() {
        return this.modelPreviewAsset;
    }

    public void setModelPreviewAsset(ModelPreviewAsset modelPreviewAsset) {
        this.modelPreviewAsset = modelPreviewAsset;
        ActionsInterfaceComponentG.reset();
    }

    public TexturePreviewAsset getTexturePreviewAsset() {
        return this.texturePreviewAsset;
    }

    public ResourcesInterfaceComponentG setTexturePreviewAsset(TexturePreviewAsset texturePreviewAsset) {
        this.texturePreviewAsset = texturePreviewAsset;
        return this;
    }

    private void drawModelsTree(WBenchGameResourcesManager.AssetsFolder<WBenchGameResourcesManager.ModelAsset> folder, boolean root) {
        String folderName = new File(folder.getPath()).getName();
        if (root) {
            folderName = "View";
        }
        if (ImGui.treeNodeEx(folderName + "##" + folder.getPath(), ImGuiTreeNodeFlags.OpenOnArrow)) {
            for (WBenchGameResourcesManager.ModelAsset asset : folder.getAssetsThere()) {
                String label = asset.getName() + "##" + asset.getName() + folder.getPath();
                boolean flag = this.getModelPreviewAsset() != null && asset.equals(this.getModelPreviewAsset().getModelAsset());
                ImGui.bullet();
                if (ImGui.selectable(label, flag)) {
                    if (!flag) {
                        this.setModelPreviewAsset(new ModelPreviewAsset(asset));
                    } else {
                        this.setModelPreviewAsset(null);
                    }
                }
            }
            for (WBenchGameResourcesManager.AssetsFolder<WBenchGameResourcesManager.ModelAsset> child : folder.getAssetsFoldersInside()) {
                this.drawModelsTree(child, false);
            }
            ImGui.treePop();
        }
    }

    private void drawTexturesTree(WBenchGameResourcesManager.AssetsFolder<WBenchGameResourcesManager.TextureAsset> folder, boolean root) {
        String folderName = new File(folder.getPath()).getName();
        if (root) {
            folderName = "View";
        }
        if (ImGui.treeNodeEx(folderName + "##" + folder.getPath(), ImGuiTreeNodeFlags.OpenOnArrow)) {
            for (WBenchGameResourcesManager.TextureAsset asset : folder.getAssetsThere()) {
                String label = asset.getName() + "##" + asset.getName() + folder.getPath();
                boolean flag = this.getTexturePreviewAsset() != null && asset.equals(this.getTexturePreviewAsset().getTextureAsset());
                ImGui.bullet();
                if (ImGui.selectable(label, flag)) {
                    if (!flag) {
                        this.setTexturePreviewAsset(new TexturePreviewAsset(asset));
                    } else {
                        this.setTexturePreviewAsset(null);
                    }
                }
            }
            for (WBenchGameResourcesManager.AssetsFolder<WBenchGameResourcesManager.TextureAsset> child : folder.getAssetsFoldersInside()) {
                this.drawTexturesTree(child, false);
            }
            ImGui.treePop();
        }
    }

    public void resourcesContent() {
        if (ImGui.collapsingHeader("Maps")) {
            this.Maps();
        }
        if (ImGui.collapsingHeader("Resources")) {
            ImGui.indent();
            if (ImGui.collapsingHeader("Assets")) {
                ImGui.indent();
                if (ImGui.treeNode("Models")) {
                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        ImGui.setTooltip("Only GLTF2");
                        ImGui.endTooltip();
                    }
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c8cff);
                    ImGui.bullet();
                    if (ImGui.selectable("Open Folder")) {
                        WBenchGameResourcesManager.openModelsFolder(WBench.get().getGameProjectManager().getCurrentGameProject().getCurrentProjectAbsolutePath());
                    }
                    ImGui.popStyleColor();
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                    ImGui.bullet();
                    if (ImGui.selectable("Refresh")) {
                        WBench.get().getGameProjectManager().refreshModelFiles(true);
                        this.setModelPreviewAsset(null);
                    }
                    ImGui.popStyleColor();
                    {

                    }
                    this.drawModelsTree(WBench.get().getGameProjectManager().getGameResourcesManager().getModelAssetsFolder(), true);
                    ImGui.treePop();
                }
                if (ImGui.treeNode("Textures")) {
                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        ImGui.setTooltip("PNG, JPG, JPEG, BMP");
                        ImGui.endTooltip();
                    }
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c8cff);
                    ImGui.bullet();
                    if (ImGui.selectable("Open Folder")) {
                        WBenchGameResourcesManager.openTexturesFolder(WBench.get().getGameProjectManager().getCurrentGameProject().getCurrentProjectAbsolutePath());
                    }
                    ImGui.popStyleColor();
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                    ImGui.bullet();
                    if (ImGui.selectable("Refresh")) {
                        WBench.get().getGameProjectManager().refreshTextureFiles(true);
                        //this.setModelPreviewAsset(null);
                    }
                    ImGui.popStyleColor();
                    {

                    }
                    ImGui.separator();
                    this.drawTexturesTree(WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder(), true);
                    ImGui.treePop();
                }
                ImGui.unindent();
            }
            if (ImGui.collapsingHeader("Game Objects")) {
                ImGui.indent();
                if (ImGui.treeNode("Props")) {

                    ImGui.treePop();
                }
                if (ImGui.treeNode("Entities")) {

                    ImGui.treePop();
                }
                if (ImGui.treeNode("Particles")) {

                    ImGui.treePop();
                }
            }
            ImGui.unindent();
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
                if (!name.matches("[a-zA-Z\\d]+")) {
                    this.createMapContext.errTest = "Use valid symbols!";
                }
                for (String mapProject : new ArrayList<>(WBench.get().getGameProjectManager().getCurrentGameProject().getMaps())) {
                    if (mapProject.equals(name)) {
                        this.createMapContext.errTest = "This map already exists!";
                        break;
                    }
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

        if (!WBench.get().getGameProjectManager().getCurrentGameProject().getMaps().isEmpty()) {
            try {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                if (ImGui.selectable(" + Create")) {
                    this.createMapContext.clear();
                    ImGui.openPopup("NewMapPopup");
                }
                ImGui.popStyleColor();
                for (String mapProject : new ArrayList<>(WBench.get().getGameProjectManager().getCurrentGameProject().getMaps())) {
                    final boolean selected = this.getMapProjectPreview() != null && mapProject.equals(this.getMapProjectPreview().getProjectName());
                    ImGui.bullet();
                    if (ImGui.selectable(mapProject, selected)) {
                        if (selected) {
                            this.removeMapPreview();
                        } else {
                            final JGemsPath pathToMap = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), mapProject);
                            this.mapProjectPreview = WBench.get().getMapProjectManager().readMainFile(new JGemsPath(pathToMap, mapProject + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE), true);
                        }
                    }
                }
            } catch (JGemsIOException e) {
                Log.get().exception(e);
                LoggingManager.showExceptionDialog("Error!", e);
            }
        }
        ImGui.separator();
        if (ImGui.button("Refresh")) {
            WBench.get().getGameProjectManager().refreshMaps();
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
    }

    private static class CreateMapContext {
        private String errTest;
        private ImString mapNamePopup;
        private ImString mapDescriptionPopup;

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
