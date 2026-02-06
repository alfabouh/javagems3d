package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import javagems3d.JGems3D;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.AxisConstraints;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.instances.mapping.MapProjectPreview;
import workbench.graphics.scene.ui.game.editor.instances.mapping.SkyBoxAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.ObjectTagPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.TextureAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.world.ObjectEntityPreview;
import workbench.graphics.scene.ui.game.editor.instances.world.ObjectPropPreview;
import workbench.graphics.scene.ui.game.editor.utils.CreatableResourcesTreeDrawerG;
import workbench.graphics.scene.ui.game.editor.utils.FolderResourcesTreeDrawerG;
import workbench.project.managing.WBenchGameResourcesManager;
import workbench.project.managing.instances.group.GameResourceAssetsFolder;
import workbench.project.managing.instances.mapping.GameResourceMapAsset;
import workbench.project.managing.instances.mapping.GameResourceSkyboxAsset;
import workbench.project.managing.instances.misc.GameResourceModelAsset;
import workbench.project.managing.instances.misc.GameResourceObjectTagData;
import workbench.project.managing.instances.misc.GameResourceTextureAsset;
import workbench.project.managing.instances.world.GameResourceEntityObjectAsset;
import workbench.project.managing.instances.world.GameResourcePropObjectAsset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ResourcesInterfaceComponentG {
    private FolderResourcesTreeDrawerG<GameResourceModelAsset, ModelAssetPreview> modelAssetsTreeDrawer;
    private FolderResourcesTreeDrawerG<GameResourceTextureAsset, TextureAssetPreview> textureAssetsTreeDrawer;
    private CreatableResourcesTreeDrawerG<GameResourcePropObjectAsset, ObjectPropPreview> propResourceTreeDrawer;
    private CreatableResourcesTreeDrawerG<GameResourceEntityObjectAsset, ObjectEntityPreview> entityResourceTreeDrawer;
    private CreatableResourcesTreeDrawerG<GameResourceObjectTagData, ObjectTagPreview> tagResourceTreeDrawer;
    private CreatableResourcesTreeDrawerG<GameResourceMapAsset, MapProjectPreview> mapResourceTreeDrawer;
    private CreatableResourcesTreeDrawerG<GameResourceSkyboxAsset, SkyBoxAssetPreview> skyBoxResourceTreeDrawer;

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
                (e) -> e.getFirst().getFoldersThereMap().containsKey(e.getSecond().getInputStrings().get(0).get()),
                (e) -> {
                    final GameResourcePropObjectAsset gameResourcePropObjectAsset = new GameResourcePropObjectAsset(e.getSecond().getInputStrings().get(0).get(), null, new TagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
                    e.getFirst().putObjectThere(gameResourcePropObjectAsset);
                    return gameResourcePropObjectAsset;
                },
                ObjectPropPreview::new
        ).setAfterFolderCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.PROPS);
        }).setAfterFolderDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.PROPS);
        }).setAfterAssetCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.PROPS);
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.PROPS);
        }).setOnContextOnItem((e) -> {
            final GameResourceModelAsset extractModelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(e.getModelAssetRelativePath());
            ImGui.beginDisabled(extractModelAsset == null);
            if (ImGui.menuItem("View Model")) {
                if (extractModelAsset != null) {
                    this.getModelAssetsTreeDrawer().setPreviewWrapperObject(new ModelAssetPreview(extractModelAsset));
                }
            }
            ImGui.endDisabled();
        });

        this.entityResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Entities",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getEntityAssetsFolder(),
                new ArrayList<CreatableResourcesTreeDrawerG.PopupConstructorData>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Entity's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.getFirst().getFoldersThereMap().containsKey(e.getSecond().getInputStrings().get(0).get()),
                (e) -> {
                    final GameResourceEntityObjectAsset gameResourceEntityObjectAsset = new GameResourceEntityObjectAsset(e.getSecond().getInputStrings().get(0).get(), null, new TagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
                    e.getFirst().putObjectThere(gameResourceEntityObjectAsset);
                    return gameResourceEntityObjectAsset;
                },
                ObjectEntityPreview::new
        ).setAfterFolderCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.ENTITIES);
        }).setAfterFolderDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.ENTITIES);
        }).setAfterAssetCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.ENTITIES);
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.ENTITIES);
        }).setOnContextOnItem((e) -> {
            final GameResourceModelAsset extractModelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(e.getModelAssetRelativePath());
            ImGui.beginDisabled(extractModelAsset == null);
            if (ImGui.menuItem("View Model")) {
                if (extractModelAsset != null) {
                    this.getModelAssetsTreeDrawer().setPreviewWrapperObject(new ModelAssetPreview(extractModelAsset));
                }
            }
            ImGui.endDisabled();
        });

        this.skyBoxResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Skyboxes",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getSkyBoxesAssetsFolder(),
                new ArrayList<CreatableResourcesTreeDrawerG.PopupConstructorData>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Skybox's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.getFirst().getFoldersThereMap().containsKey(e.getSecond().getInputStrings().get(0).get()),
                (e) -> {
                    final GameResourceSkyboxAsset gameResourceSkyboxAsset = new GameResourceSkyboxAsset(e.getSecond().getInputStrings().get(0).get());
                    e.getFirst().putObjectThere(gameResourceSkyboxAsset);
                    return gameResourceSkyboxAsset;
                },
                SkyBoxAssetPreview::new
        ).setAfterFolderCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.SKYBOXES);
        }).setAfterFolderDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.SKYBOXES);
        }).setAfterAssetCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.SKYBOXES);
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.SKYBOXES);
        });

        this.tagResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Tag Containers",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getTagAssetsFolder(),
                new ArrayList<CreatableResourcesTreeDrawerG.PopupConstructorData>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Tag's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.getFirst().getFoldersThereMap().containsKey(e.getSecond().getInputStrings().get(0).get()),
                (e) -> {
                    final GameResourceObjectTagData gameResourceObjectTagData = new GameResourceObjectTagData(e.getSecond().getInputStrings().get(0).get(), new TagsContainer());
                    e.getFirst().putObjectThere(gameResourceObjectTagData);
                    return gameResourceObjectTagData;
                },
                ObjectTagPreview::new
        ).setAfterFolderCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.TAGS);
        }).setAfterFolderDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.TAGS);
        }).setAfterAssetCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.TAGS);
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.TAGS);
        });

        this.mapResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Maps",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getMapAssetsFolder(),
                new ArrayList<CreatableResourcesTreeDrawerG.PopupConstructorData>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Map's Name", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.getFirst().getFoldersThereMap().containsKey(e.getSecond().getInputStrings().get(0).get()),
                (e) -> {
                    final String name = e.getSecond().getInputStrings().get(0).get();
                    final String mapNameFile = name + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE;
                    final JGemsPath absPath = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), name, e.getFirst().getHierarchy());
                    final GameResourceMapAsset gameResourceMapAsset = new GameResourceMapAsset(WBench.get().getMapProjectManager().createMapProject(absPath, new JGemsPath(absPath, mapNameFile), name));
                    final GameResourceAssetsFolder<GameResourceMapAsset> newFolder = new GameResourceAssetsFolder<>(name);
                    newFolder.putObjectThere(gameResourceMapAsset);
                    e.getFirst().putFolderThere(newFolder);
                    return gameResourceMapAsset;
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
        }).setOnRefreshButton((e) -> {
            WBench.get().getGameProjectManager().refreshMaps(true);
        });
    }

    public void clear() {
    }

    public CreatableResourcesTreeDrawerG<GameResourceSkyboxAsset, SkyBoxAssetPreview> getSkyBoxResourceTreeDrawer() {
        return this.skyBoxResourceTreeDrawer;
    }

    public CreatableResourcesTreeDrawerG<GameResourceObjectTagData, ObjectTagPreview> getTagResourceTreeDrawer() {
        return this.tagResourceTreeDrawer;
    }

    public CreatableResourcesTreeDrawerG<GameResourceEntityObjectAsset, ObjectEntityPreview> getEntityResourceTreeDrawer() {
        return this.entityResourceTreeDrawer;
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
        this.Resources();
        ImGui.dummy(0.0f, 20.0f);
    }

    private void Resources() {
        if (ImGui.collapsingHeader("Assets", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent();
            this.getModelAssetsTreeDrawer().render();
            this.getTextureAssetsTreeDrawer().render();
            ImGui.unindent();
        }
        if (ImGui.collapsingHeader("Mapping", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent();
            this.getMapResourceTreeDrawer().render();
            ImGui.unindent();
        }
        if (ImGui.collapsingHeader("Environment", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent();
            this.getSkyBoxResourceTreeDrawer().render();
            ImGui.unindent();
        }
        if (ImGui.collapsingHeader("Game Objects", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent();
            this.getTagResourceTreeDrawer().render();
            this.getPropResourceTreeDrawer().render();
            this.getEntityResourceTreeDrawer().render();
            ImGui.unindent();
        }
    }
}
