package workbench.graphics.scene.ui.game.editor;

import api.system.JGemsAPI;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import javagems3d.JGems3D;
import javagems3d.system.external.gaming.JGemsGaming;
import javagems3d.system.external.gaming.def.misc.*;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import workbench.WBench;
import workbench.graphics.scene.ui.game.GameEditorInterface;
import workbench.graphics.scene.ui.game.editor.instances.mapping.MapProjectPreview;
import workbench.graphics.scene.ui.game.editor.instances.mapping.SkyBoxAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.ObjectTagPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.TextureAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.scripting.ScriptAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.world.ObjectEntityPreview;
import workbench.graphics.scene.ui.game.editor.instances.world.ObjectPropPreview;
import workbench.graphics.scene.ui.game.editor.utils.CreatableResourcesTreeDrawerG;
import workbench.graphics.scene.ui.game.editor.utils.FolderResourcesTreeDrawerG;
import workbench.project.managing.WBenchGameResourcesManager;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import workbench.project.managing.instances.WBenchResourceMapAsset;
import javagems3d.system.external.gaming.def.world.GameResourceEntityObjectAsset;
import javagems3d.system.external.gaming.def.world.GameResourcePropObjectAsset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ResourcesInterfaceComponentG {
    private final GameEditorInterface gameEditorInterface;
    private final FolderResourcesTreeDrawerG<GameResourceModelAsset, ModelAssetPreview> modelAssetsTreeDrawer;
    private final FolderResourcesTreeDrawerG<GameResourceTextureAsset, TextureAssetPreview> textureAssetsTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourcePropObjectAsset, ObjectPropPreview> propResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourceEntityObjectAsset, ObjectEntityPreview> entityResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourceObjectTagData, ObjectTagPreview> tagResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<WBenchResourceMapAsset, MapProjectPreview> mapResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourceScriptAsset, ScriptAssetPreview> scriptResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourceSkyboxAsset, SkyBoxAssetPreview> skyBoxResourceTreeDrawer;

    public ResourcesInterfaceComponentG(GameEditorInterface gameEditorInterface) {
        this.gameEditorInterface = gameEditorInterface;

        this.modelAssetsTreeDrawer = new FolderResourcesTreeDrawerG<>(
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getModelAssetsFolder(),
                "Models",
                (e) -> WBench.get().getGameProjectManager().refreshModelFiles(true),
                (e) -> WBenchGameResourcesManager.openModelsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()),
                ModelAssetPreview::new);

        this.textureAssetsTreeDrawer = new FolderResourcesTreeDrawerG<>(
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder(),
                "Textures",
                (e) -> WBench.get().getGameProjectManager().refreshTextureFiles(true),
                (e) -> WBenchGameResourcesManager.openTexturesFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()),
                TextureAssetPreview::new);

        this.propResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Props",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getPropAssetsFolder(),
                new ArrayList<CreatableResourcesTreeDrawerG.PopupConstructorData>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Prop's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final GameResourcePropObjectAsset gameResourcePropObjectAsset = new GameResourcePropObjectAsset(e.second().getInputStrings().getFirst().get(), null, new TagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
                    e.first().putObjectThere(gameResourcePropObjectAsset);
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
                new ArrayList<>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Entity's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final GameResourceEntityObjectAsset gameResourceEntityObjectAsset = new GameResourceEntityObjectAsset(e.second().getInputStrings().getFirst().get(), null, new TagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
                    e.first().putObjectThere(gameResourceEntityObjectAsset);
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
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final GameResourceSkyboxAsset gameResourceSkyboxAsset = new GameResourceSkyboxAsset(e.second().getInputStrings().getFirst().get());
                    e.first().putObjectThere(gameResourceSkyboxAsset);
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
                new ArrayList<>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Tag's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final GameResourceObjectTagData gameResourceObjectTagData = new GameResourceObjectTagData(e.second().getInputStrings().getFirst().get(), new TagsContainer());
                    e.first().putObjectThere(gameResourceObjectTagData);
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
                new ArrayList<>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Map's Name", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final String name = e.second().getInputStrings().getFirst().get();
                    final JGemsPath absPath = new JGemsPath(WBench.get().getGameProjectManager().getMapsPath(), name, e.first().getHierarchy());
                    final WBenchResourceMapAsset gameResourceMapAsset = new WBenchResourceMapAsset(WBench.get().getMapProjectManager().createMapProject(absPath, JGemsGaming.getPathToMainMapFile(absPath, name), name));
                    final GameResourceAssetsFolder<WBenchResourceMapAsset> newFolder = new GameResourceAssetsFolder<>(name);
                    newFolder.putObjectThere(gameResourceMapAsset);
                    e.first().putFolderThere(newFolder);
                    return gameResourceMapAsset;
                },
                MapProjectPreview::new
        ).setAfterAssetDeleted((e) -> {
            try {
                WBench.get().getMapProjectManager().deleteMapProjectFolder(e.second().getMapProject().getAbsolutePath());
            } catch (IOException ex) {
                LoggingManager.showWindowWarn("Couldn't delete map: " + e.second().name());
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

        this.scriptResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Scripts",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getScriptAssetsFolder(),
                new ArrayList<>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("File's Name", "^(?!\\.)[a-zA-Z\\d_-]+$", "Only letters, digits, underscores (_) and dashes (-) allowed. Spaces and special symbols are not allowed, name cannot start with a dot."));
                }},
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final String sampleText = JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController().getEntryPointClass().sampleCode().toString();
                    final String name = e.second().getInputStrings().getFirst().get() + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.JS_SCRIPT_FILE;
                    final GameResourceScriptAsset gameResourceScriptAsset = new GameResourceScriptAsset(name, e.first().getHierarchy() + "/" + name, sampleText);
                    gameResourceScriptAsset.save(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath(), sampleText);
                    e.first().putObjectThere(gameResourceScriptAsset);
                    return gameResourceScriptAsset;
                },
                ScriptAssetPreview::new
        ).setAfterAssetDeleted((e) -> {
            final JGemsPath absPath = new JGemsPath(JGemsGaming.getScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), e.first().getHierarchy());
            if (absPath.toFile().exists()) {
                absPath.toFile().delete();
            }
        }).setAfterFolderCreated((e) -> {
            final JGemsPath absPath = new JGemsPath(JGemsGaming.getScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), e.getHierarchy());
            if (absPath.toFile().exists()) {
                absPath.toFile().mkdirs();
            }
        }).setAfterFolderDeleted((e) -> {
            new JGemsPath(JGemsGaming.getScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), e.getHierarchy()).recursiveDelete();
        }).setOnRefreshButton((e) -> {
            WBench.get().getGameProjectManager().refreshScripts(true);
        }).setOnItemSelection((e) -> {
            if (this.getScriptResourceTreeDrawer().getPreviewWrapperObject() != null) {
                this.gameEditorInterface.getWindowInterfaceComponentG().getScenePreviewScriptG().save();
            }
            if (e != null) {
                this.gameEditorInterface.getWindowInterfaceComponentG().getScenePreviewScriptG().setScriptPreviewObject(e);
            }
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

    public CreatableResourcesTreeDrawerG<WBenchResourceMapAsset, MapProjectPreview> getMapResourceTreeDrawer() {
        return this.mapResourceTreeDrawer;
    }

    public FolderResourcesTreeDrawerG<GameResourceModelAsset, ModelAssetPreview> getModelAssetsTreeDrawer() {
        return this.modelAssetsTreeDrawer;
    }

    public FolderResourcesTreeDrawerG<GameResourceTextureAsset, TextureAssetPreview> getTextureAssetsTreeDrawer() {
        return this.textureAssetsTreeDrawer;
    }

    public CreatableResourcesTreeDrawerG<GameResourceScriptAsset, ScriptAssetPreview> getScriptResourceTreeDrawer() {
        return this.scriptResourceTreeDrawer;
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
        if (ImGui.collapsingHeader("Game", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent();
            this.getMapResourceTreeDrawer().render();
            this.getScriptResourceTreeDrawer().render();
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
