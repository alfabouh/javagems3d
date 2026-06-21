package workbench.graphics.scene.ui.game.editor;

import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import api.system.JGemsAPI;
import com.google.gson.*;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import javagems3d.JGems3D;
import javagems3d.system.external.gaming.JGemsGameInstance;
import javagems3d.system.external.gaming.def.IAsset;
import javagems3d.system.external.gaming.def.misc.*;
import javagems3d.system.external.gaming.def.world.GameResourceMarkerObjectAsset;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.external.mapping.tags.items.TagRadioBoolean;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.scene.ui.game.GameEditorInterface;
import workbench.graphics.scene.ui.game.editor.instances.mapping.MapProjectPreview;
import workbench.graphics.scene.ui.game.editor.instances.mapping.SkyBoxAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.ObjectTagPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.SoundAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.TextureAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.scripting.ScriptAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.world.ObjectEntityPreview;
import workbench.graphics.scene.ui.game.editor.instances.world.ObjectMarkerPreview;
import workbench.graphics.scene.ui.game.editor.instances.world.ObjectPropPreview;
import workbench.graphics.scene.ui.game.editor.utils.CreatableResourcesTreeDrawerG;
import workbench.graphics.scene.ui.game.editor.utils.FolderResourcesTreeDrawerG;
import workbench.project.managing.WBenchProjectResourcesManager;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import workbench.project.managing.instances.WBenchResourceMapAsset;
import javagems3d.system.external.gaming.def.world.GameResourceEntityObjectAsset;
import javagems3d.system.external.gaming.def.world.GameResourcePropObjectAsset;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResourcesInterfaceComponentG {
    private final GameEditorInterface gameEditorInterface;
    private final FolderResourcesTreeDrawerG<GameResourceModelAsset, ModelAssetPreview> modelAssetsTreeDrawer;
    private final FolderResourcesTreeDrawerG<GameResourceSoundAsset, SoundAssetPreview> soundAssetsTreeDrawer;
    private final FolderResourcesTreeDrawerG<GameResourceTextureAsset, TextureAssetPreview> textureAssetsTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourcePropObjectAsset, ObjectPropPreview> propResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourceEntityObjectAsset, ObjectEntityPreview> entityResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourceMarkerObjectAsset, ObjectMarkerPreview> markerResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourceObjectTagData, ObjectTagPreview> tagResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<WBenchResourceMapAsset, MapProjectPreview> mapResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourceScriptAsset, ScriptAssetPreview> scriptResourceTreeDrawer;
    private final CreatableResourcesTreeDrawerG<GameResourceSkyboxAsset, SkyBoxAssetPreview> skyBoxResourceTreeDrawer;

    public ResourcesInterfaceComponentG(GameEditorInterface gameEditorInterface) {
        this.gameEditorInterface = gameEditorInterface;

        this.modelAssetsTreeDrawer = new FolderResourcesTreeDrawerG<>(
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getModelAssetsFolder(),
                "Models (GLTF)",
                (e) -> WBench.get().getGameProjectManager().refreshModelFiles(true),
                (e) -> WBenchProjectResourcesManager.openModelsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()),
                ModelAssetPreview::new);

        this.textureAssetsTreeDrawer = new FolderResourcesTreeDrawerG<>(
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder(),
                "Textures",
                (e) -> WBench.get().getGameProjectManager().refreshTextureFiles(true),
                (e) -> WBenchProjectResourcesManager.openTexturesFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()),
                TextureAssetPreview::new);

        this.soundAssetsTreeDrawer = new FolderResourcesTreeDrawerG<>(
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getSoundAssetsFolder(),
                "Sounds (OGG)",
                (e) -> WBench.get().getGameProjectManager().refreshSoundFiles(true),
                (e) -> WBenchProjectResourcesManager.openSoundsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()),
                SoundAssetPreview::new).setOnSelectedObject((o) -> {
                    gameEditorInterface.getActionsInterfaceComponentG().getScenePreviewSoundG().reset();
        });

        this.propResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Props",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getPropAssetsFolder(),
                new ArrayList<CreatableResourcesTreeDrawerG.PopupConstructorData>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Prop's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final GameResourcePropObjectAsset gameResourcePropObjectAsset = new GameResourcePropObjectAsset(e.second().getInputStrings().getFirst().get(), null, new TagsContainer(), new WBenchRenderProperties(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
                    e.first().putObjectThere(gameResourcePropObjectAsset);
                    return gameResourcePropObjectAsset;
                },
                ObjectPropPreview::new
        ).setAfterFolderCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.PROPS);
        }).setAfterFolderDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.PROPS);
        }).setAfterAssetCreated((e) -> {
            ResourcesInterfaceComponentG.DEFAULT_TAGS_PROP(e.second());
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.PROPS);
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.PROPS);
        }).setOnContextOnItem((e) -> {
            final GameResourceModelAsset extractModelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(e.getModelAssetRelativePath());
            ImGui.beginDisabled(extractModelAsset == null);
            if (ImGui.menuItem("View Model")) {
                if (extractModelAsset != null) {
                    this.getModelAssetsTreeDrawer().setPreviewWrapperObject(new ModelAssetPreview(extractModelAsset));
                }
            }
            ImGui.endDisabled();
        }).setAfterAssetMoved((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.PROPS);
            ResourcesInterfaceComponentG.changeMapDatObjProperties(MapObjectsIdentifiers.PROP, new String[] {"propObjects", "backgroundProps"}, e);
        });

        this.entityResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Entities",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getEntityAssetsFolder(),
                new ArrayList<>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Entity's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final GameResourceEntityObjectAsset gameResourceEntityObjectAsset = new GameResourceEntityObjectAsset(e.second().getInputStrings().getFirst().get(), null, new TagsContainer(), new WBenchRenderProperties(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
                    e.first().putObjectThere(gameResourceEntityObjectAsset);
                    return gameResourceEntityObjectAsset;
                },
                ObjectEntityPreview::new
        ).setAfterFolderCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.ENTITIES);
        }).setAfterFolderDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.ENTITIES);
        }).setAfterAssetCreated((e) -> {
            ResourcesInterfaceComponentG.DEFAULT_TAGS_ENTITY(e.second());
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.ENTITIES);
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.ENTITIES);
        }).setOnContextOnItem((e) -> {
            final GameResourceModelAsset extractModelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(e.getModelAssetRelativePath());
            ImGui.beginDisabled(extractModelAsset == null);
            if (ImGui.menuItem("View Model")) {
                if (extractModelAsset != null) {
                    this.getModelAssetsTreeDrawer().setPreviewWrapperObject(new ModelAssetPreview(extractModelAsset));
                }
            }
            ImGui.endDisabled();
        }).setAfterAssetMoved((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.ENTITIES);
            ResourcesInterfaceComponentG.changeMapDatObjProperties(MapObjectsIdentifiers.ENTITY, new String[] {"entityObjects"}, e);
        });

        this.markerResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Markers",
                () -> WBench.get().getGameProjectManager().getGameResourcesManager().getMarkerAssetsFolder(),
                new ArrayList<>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("Marker's ID", "[a-zA-Z\\d]+", "Digits, spec. symbols and spaces are not allowed!"));
                }},
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final GameResourceMarkerObjectAsset gameResourceMarkerObjectAsset = new GameResourceMarkerObjectAsset(e.second().getInputStrings().getFirst().get(), null, new TagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ), new Vector3f(1.0f), false);
                    e.first().putObjectThere(gameResourceMarkerObjectAsset);
                    return gameResourceMarkerObjectAsset;
                },
                ObjectMarkerPreview::new
        ).setAfterFolderCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.MARKERS);
        }).setAfterFolderDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.MARKERS);
        }).setAfterAssetCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.MARKERS);
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.MARKERS);
        }).setOnContextOnItem((e) -> {
            final GameResourceModelAsset extractModelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(e.getModelAssetRelativePath());
            ImGui.beginDisabled(extractModelAsset == null);
            if (ImGui.menuItem("View Model")) {
                if (extractModelAsset != null) {
                    this.getModelAssetsTreeDrawer().setPreviewWrapperObject(new ModelAssetPreview(extractModelAsset));
                }
            }
            ImGui.endDisabled();
        }).setAfterAssetMoved((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.MARKERS);
            ResourcesInterfaceComponentG.changeMapDatObjProperties(MapObjectsIdentifiers.MARKER, new String[] {"markerObjects"}, e);
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
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.SKYBOXES);
        }).setAfterFolderDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.SKYBOXES);
        }).setAfterAssetCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.SKYBOXES);
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.SKYBOXES);
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
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.TAGS);
        }).setAfterFolderDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.TAGS);
        }).setAfterAssetCreated((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.TAGS);
        }).setAfterAssetDeleted((e) -> {
            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.TAGS);
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
                    final WBenchResourceMapAsset gameResourceMapAsset = new WBenchResourceMapAsset(WBench.get().getMapProjectManager().createMapProject(absPath, JGemsGameInstance.getPathToMainMapFile(absPath, name), name));
                    final GameResourceAssetsFolder<WBenchResourceMapAsset> newFolder = new GameResourceAssetsFolder<>(name);
                    newFolder.putObjectThere(gameResourceMapAsset);
                    e.first().putFolderThere(newFolder);
                    return gameResourceMapAsset;
                },
                MapProjectPreview::new
        ).setAfterAssetDeleted((e) -> {
            try {
                WBench.get().getMapProjectManager().deleteMapProjectFolder(e.second().getMapProject().getMapAbsolutePath());
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
        }).setOnOpenFolderButton((e) -> {
            WBenchProjectResourcesManager.openMapsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath());
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
                    gameResourceScriptAsset.save(JGemsGameInstance.getScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), sampleText);
                    e.first().putObjectThere(gameResourceScriptAsset);
                    return gameResourceScriptAsset;
                },
                ScriptAssetPreview::new
        ).setAfterAssetDeleted((e) -> {
            final JGemsPath absPath = new JGemsPath(JGemsGameInstance.getScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), e.first().getHierarchy());
            if (absPath.toFile().exists()) {
                absPath.toFile().delete();
            }
        }).setAfterFolderCreated((e) -> {
            final JGemsPath absPath = new JGemsPath(JGemsGameInstance.getScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), e.getHierarchy());
            absPath.toFile().mkdirs();
        }).setAfterFolderDeleted((e) -> {
            new JGemsPath(JGemsGameInstance.getScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), e.getHierarchy()).recursiveDelete();
        }).setOnRefreshButton((e) -> {
            WBench.get().getGameProjectManager().refreshScripts(true);
        }).setOnItemSelection((e) -> {
            if (this.getScriptResourceTreeDrawer().getPreviewWrapperObject() != null) {
                this.gameEditorInterface.getWindowInterfaceComponentG().getScenePreviewScriptG().save();
            }
            if (e != null) {
                this.gameEditorInterface.getWindowInterfaceComponentG().getScenePreviewScriptG().setScriptPreviewObject(e);
            }
        }).setOnOpenFolderButton((e) -> {
            WBenchProjectResourcesManager.openScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath());
        });
    }

    public void clear() {
       this.modelAssetsTreeDrawer.setPreviewWrapperObject(null);
       this.soundAssetsTreeDrawer.setPreviewWrapperObject(null);
       this.textureAssetsTreeDrawer.setPreviewWrapperObject(null);
       this.propResourceTreeDrawer.setPreviewWrapperObject(null);
       this.entityResourceTreeDrawer.setPreviewWrapperObject(null);
       this.markerResourceTreeDrawer.setPreviewWrapperObject(null);
       this.tagResourceTreeDrawer.setPreviewWrapperObject(null);
       this.mapResourceTreeDrawer.setPreviewWrapperObject(null);
       this.scriptResourceTreeDrawer.setPreviewWrapperObject(null);
       this.skyBoxResourceTreeDrawer.setPreviewWrapperObject(null);
    }

    public static List<GameResourceObjectTagData> DEFAULT_TAGS() {
        List<GameResourceObjectTagData> gameResourceObjectTagData = new ArrayList<>();
        {
            final Tag<TagRadioBoolean> physical = Tag.create(TagID.DEFAULT.PHYSICS_STATE, new TagRadioBoolean(new TagRadioBoolean.Info("Dynamic", false), new TagRadioBoolean.Info("Static", true)));
            final Tag<TagRadioBoolean> directIndirect = Tag.create(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, new TagRadioBoolean(new TagRadioBoolean.Info("Direct", true), new TagRadioBoolean.Info("Indirect", false)));
            gameResourceObjectTagData.add(new GameResourceObjectTagData("Physical", new TagsContainer().addTag(physical)));
            gameResourceObjectTagData.add(new GameResourceObjectTagData("Rendering", new TagsContainer().addTag(directIndirect)));
        }
        return gameResourceObjectTagData;
    }

    private static void DEFAULT_TAGS_PROP(GameResourcePropObjectAsset propObjectAsset) {
        final Tag<TagRadioBoolean> directIndirect = Tag.create(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, new TagRadioBoolean(new TagRadioBoolean.Info("Direct", true), new TagRadioBoolean.Info("Indirect", false)));
        propObjectAsset.getTagsContainer().addTag(directIndirect);
    }

    private static void DEFAULT_TAGS_ENTITY(GameResourceEntityObjectAsset entityObjectAsset) {
        final Tag<TagRadioBoolean> physical = Tag.create(TagID.DEFAULT.PHYSICS_STATE, new TagRadioBoolean(new TagRadioBoolean.Info("Dynamic", false), new TagRadioBoolean.Info("Static", true)));
        final Tag<TagRadioBoolean> directIndirect = Tag.create(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, new TagRadioBoolean(new TagRadioBoolean.Info("Direct", true), new TagRadioBoolean.Info("Indirect", false)));
        entityObjectAsset.getTagsContainer().addTag(physical);
        entityObjectAsset.getTagsContainer().addTag(directIndirect);
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

    public CreatableResourcesTreeDrawerG<GameResourceMarkerObjectAsset, ObjectMarkerPreview> getMarkerResourceTreeDrawer() {
        return this.markerResourceTreeDrawer;
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

    public FolderResourcesTreeDrawerG<GameResourceSoundAsset, SoundAssetPreview> getSoundAssetsTreeDrawer() {
        return this.soundAssetsTreeDrawer;
    }

    private static void changeMapDatObjProperties(String prefix, String[] rootNames, CreatableResourcesTreeDrawerG.AssetMovedData<? extends IAsset> e) {
        final List<JGemsPath> getAllMapDataFiles = new ArrayList<>();
        WBench.get().getGameProjectManager().getGameResourcesManager().getAllMapsDataPaths(getAllMapDataFiles, WBench.get().getGameProjectManager().getGameResourcesManager().getMapAssetsFolder());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        for (JGemsPath path : getAllMapDataFiles) {
            try (FileReader reader = new FileReader(path.toFile())) {
                JsonElement root = JsonParser.parseReader(reader);
                AtomicBoolean changed = new AtomicBoolean(false);
                JsonObject inner = root.getAsJsonObject().get("objectsData").getAsJsonObject();
                Arrays.stream(rootNames).forEach(name -> {
                    if (inner != null && inner.has(name)) {
                        JsonArray array = inner.getAsJsonObject().getAsJsonArray(name);
                        for (JsonElement el : array) {
                            if (!el.isJsonObject()) {
                                continue;
                            }
                            JsonObject obj = el.getAsJsonObject();
                            if (!obj.has("objectId")) {
                                continue;
                            }
                            String objectId = obj.get("objectId").getAsString();
                            String objectPath = obj.get("objectPath").getAsString();
                            if (objectId.equals(prefix + e.asset().name()) && objectPath.equals(e.from().getHierarchy())) {
                                Log.get().debug("Changed map-obj-path: " + e.asset().name());
                                obj.addProperty("objectPath", e.to().getHierarchy());
                                changed.set(true);
                            }
                        }
                    }
                });
                if (changed.get()) {
                    try (FileWriter writer = new FileWriter(path.toFile())) {
                        gson.toJson(root, writer);
                    }
                }

            } catch (Exception ex) {
                Log.get().exception(ex);
            }
        }
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
            this.getSoundAssetsTreeDrawer().render();
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
            this.getMarkerResourceTreeDrawer().render();
            ImGui.unindent();
        }
    }
}
