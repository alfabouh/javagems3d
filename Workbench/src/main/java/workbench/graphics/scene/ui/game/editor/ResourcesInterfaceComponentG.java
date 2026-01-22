package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import javagems3d.JGems3D;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.AxisConstraints;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.instances.*;
import workbench.graphics.scene.ui.game.editor.utils.CreatableResourcesTreeDrawerG;
import workbench.graphics.scene.ui.game.editor.utils.FolderResourcesTreeDrawerG;
import workbench.project.managing.WBenchGameResourcesManager;
import workbench.project.managing.instances.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ResourcesInterfaceComponentG {
    private FolderResourcesTreeDrawerG<GameResourceModelAsset, ModelAssetPreview> modelAssetsTreeDrawer;
    private FolderResourcesTreeDrawerG<GameResourceTextureAsset, TextureAssetPreview> textureAssetsTreeDrawer;
    private CreatableResourcesTreeDrawerG<GameResourcePropObjectAsset, ObjectPropPreview> propResourceTreeDrawer;
    private CreatableResourcesTreeDrawerG<GameResourceMapAsset, MapProjectPreview> mapResourceTreeDrawer;

    public ResourcesInterfaceComponentG() {
        this.modelAssetsTreeDrawer = new FolderResourcesTreeDrawerG<>(
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getModelAssetsFolder(),
                "Models",
                (e) -> WBenchGameResourcesManager.openModelsFolder(WBench.get().getGameProjectManager().getCurrentGameProject().getCurrentProjectAbsolutePath()),
                ModelAssetPreview::new);

        this.textureAssetsTreeDrawer = new FolderResourcesTreeDrawerG<>(
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder(),
                "Textures",
                (e) -> WBenchGameResourcesManager.openTexturesFolder(WBench.get().getGameProjectManager().getCurrentGameProject().getCurrentProjectAbsolutePath()),
                TextureAssetPreview::new);

        this.propResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Props",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getPropAssetsFolder(),
                new ArrayList<CreatableResourcesTreeDrawerG.PopupConstructorData>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Prop's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.getFirst().getAssetsFoldersInsideMap().containsKey(e.getSecond().getInputStrings().get(0).get()),
                (e) -> {
                    return new GameResourcePropObjectAsset(e.getSecond().getInputStrings().get(0).get(), null, new TagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
                },
                ObjectPropPreview::new
        ).setAfterFolderCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles();
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles();
        }).setAfterAssetCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles();
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles();
        });

        this.mapResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Maps",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getMapAssetsFolder(),
                new ArrayList<CreatableResourcesTreeDrawerG.PopupConstructorData>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Map's Name", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.getFirst().getAssetsFoldersInsideMap().containsKey(e.getSecond().getInputStrings().get(0).get()),
                (e) -> {
                    final String name = e.getSecond().getInputStrings().get(0).get();
                    final String mapNameFile = name + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE;
                    final JGemsPath absPath = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), e.getFirst().getHierarchy());
                    return new GameResourceMapAsset(WBench.get().getMapProjectManager().createMapProject(absPath, new JGemsPath(absPath, mapNameFile), name));
                },
                MapProjectPreview::new
        ).setAfterAssetDeleted((e) -> {
            try {
                WBench.get().getMapProjectManager().deleteMapProjectFolder(e.getSecond().getMapProject().getCurrentProjectPath());
            } catch (IOException ex) {
                LoggingManager.showWindowWarn("Couldn't delete map: " + e.getSecond().getName());
                Log.get().exception(ex);
            }
        }).setAfterFolderCreated((e) -> {
            File file = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), e.getHierarchy()).toFile();
            file.mkdirs();
        }).setAfterFolderDeleted((e) -> {
            new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), e.getHierarchy()).recursiveDelete();
        });
    }

    public void clear() {
    }

    public CreatableResourcesTreeDrawerG<GameResourcePropObjectAsset, ObjectPropPreview> getPropResourceTreeDrawer() {
        return this.propResourceTreeDrawer;
    }

    public CreatableResourcesTreeDrawerG<GameResourceMapAsset, MapProjectPreview> getMapResourceTreeDrawer() {
        return this.mapResourceTreeDrawer;
    }

    public FolderResourcesTreeDrawerG<GameResourceModelAsset, ModelAssetPreview> getModelAssetsTreeDrawer() {
        return this.modelAssetsTreeDrawer;
    }

    public FolderResourcesTreeDrawerG<GameResourceTextureAsset, TextureAssetPreview> getTextureAssetsTreeDrawer() {
        return this.textureAssetsTreeDrawer;
    }

    public void resourcesContent() {
        if (ImGui.collapsingHeader("Resources", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent();
            this.Resources();
            ImGui.unindent();
        }
    }

    private void Resources() {
        if (ImGui.collapsingHeader("Assets", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent();
            this.getModelAssetsTreeDrawer().render();
            this.getTextureAssetsTreeDrawer().render();
            ImGui.unindent();
        }
        if (ImGui.collapsingHeader("Game Objects", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent();
            this.getMapResourceTreeDrawer().render();
            this.getPropResourceTreeDrawer().render();
            ImGui.unindent();
        }
    }
}
