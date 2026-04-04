package workbench.project.map;

import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javagems3d.JGems3D;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.environment.fog.FogScene;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.external.gaming.JGemsGaming;
import javagems3d.system.external.mapping.data.MapObjectsDataPack;
import javagems3d.system.external.mapping.data.items.*;
import javagems3d.system.external.mapping.data.templates.RowMapObjectData;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.files.json.JSONFileManaging;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.environment.components.WBenchSkyBackground;
import workbench.graphics.objects.*;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.objects.templates.WBenchTemplate;
import workbench.graphics.scene.renderer.IProjectActionsCallback;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.asnapshots.WBenchSnapshotsTrace;
import workbench.graphics.scene.ui.asnapshots.instances.WBenchSnapshotsContainer;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;
import workbench.resources.frame.LoadingInterfaceSwing;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class WBenchMapProjectManager {
    private final MapObjectTemplatesManager mapObjectTemplates;
    private WBenchMapProject currentMapProject;
    private WBenchWorld world;
    private WBenchSnapshotsTrace snapshotsTrace;

    public WBenchMapProjectManager() {
        this.mapObjectTemplates = new MapObjectTemplatesManager();
        this.currentMapProject = null;
        this.world = null;
    }

    public void setWorld(@NotNull WBenchWorld world) {
        this.world = world;
        this.snapshotsTrace = new WBenchSnapshotsTrace((MapEditorInterface) WBenchOpenGLRenderer.getMapEditorInterface(), this.world);
    }

    public void pushSnapshot(WBenchSnapshotsContainer snapshotsContainer) {
        this.getSnapshotsTrace().pushSnapshot(snapshotsContainer);
    }

    public void takeSnapshot() {
        this.getSnapshotsTrace().pushSnapshot(this.snapshotsTrace.takeSnapshot());
    }

    public void redo() {
        this.getSnapshotsTrace().redo();
    }

    public void undo() {
        this.getSnapshotsTrace().undo();
    }

    public WBenchSnapshotsTrace getSnapshotsTrace() {
        return this.snapshotsTrace;
    }

    public WBenchMapProject createMapProject(JGemsPath absPath, JGemsPath path, String name) {
        try {
            WBenchMapProject wBenchMapProject = new WBenchMapProject(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_DATA_VERSION, name, absPath);
            //this.setCurrentProject(files, wBenchMapProject);
            this.refresScriptFolder(absPath, name);
            this.saveMapProjectFile(wBenchMapProject);
            Log.get().debug("Created WBenchMapProject: " + wBenchMapProject + ". Path: " + path + " (" + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE + ")");

            return wBenchMapProject;
        } catch (JGemsIOException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't create object!", e);
            Log.get().exception(e);
            this.currentMapProject = null;
            return null;
        }
    }

    public void editMapProjectDescription(@NotNull WBenchMapProject mapProjectData, @NotNull String newText) {
        mapProjectData.setMapDescription(newText);
        this.saveMapProjectFile(mapProjectData);
    }

    public void editMapProjectDescription(@NotNull String newText) {
        this.editMapProjectDescription(this.getCurrentMapProject(), newText);
    }

    private void saveMapProjectFile(@NotNull WBenchMapProject wBenchMapProject) {
        JSONFileManaging jsonFileManaging = JSONFileManaging.createSerializationRules();
        jsonFileManaging.writeToFile(wBenchMapProject, wBenchMapProject.getPathToMainMapFile().toFile(), null);
    }

    private <T extends WBenchTemplate> void handleObjects(boolean background, @NotNull Set<RowMapObjectData> templates, @NotNull BiFunction<String, String, T> templateFinder, @NotNull BiFunction<T, RowMapObjectData, WBenchObject<?>> objectCreator, @NotNull List<WBenchObject<?>> defaultObjectsToCreate, Function<RowMapObjectData, T> functionCreateDefault) {
        for (RowMapObjectData rowMapObjectData : templates) {
            final String name = rowMapObjectData.getObjectNameId();
            final String path = rowMapObjectData.getObjectPath();

            T tpl = null;
            try {
                tpl = templateFinder.apply(path, name);
                if (tpl == null) {
                    throw new JGemsNullException();
                }
            } catch (Exception e) {
                {
                    WBenchObject<?> DEFAULT_object = objectCreator.apply(functionCreateDefault.apply(rowMapObjectData), rowMapObjectData);
                    DEFAULT_object.setPosition(rowMapObjectData.getPosition() == null ? new Vector3f(0.0f) : rowMapObjectData.getPosition());
                    DEFAULT_object.setRotation(rowMapObjectData.getRotation() == null ? new Vector3f(0.0f) : rowMapObjectData.getRotation());
                    DEFAULT_object.setScaling(rowMapObjectData.getScaling() == null ? new Vector3f(0.0f) : rowMapObjectData.getScaling());
                    DEFAULT_object.setId(-1);
                    defaultObjectsToCreate.add(DEFAULT_object);
                }
                Log.get().error("Couldn't find rowMapObjectData: " + path + "/" + name + ". Created default");
                Log.get().exception(e);
                continue;
            }

            WBenchObject<?> object = objectCreator.apply(tpl, rowMapObjectData);
            object.setPosition(rowMapObjectData.getPosition() == null ? new Vector3f(0.0f) : rowMapObjectData.getPosition());
            object.setRotation(rowMapObjectData.getRotation() == null ? new Vector3f(0.0f) : rowMapObjectData.getRotation());
            object.setScaling(rowMapObjectData.getScaling() == null ? new Vector3f(0.0f) : rowMapObjectData.getScaling());
            object.setId(rowMapObjectData.getId());
            if (background) {
                this.getWorld().getEnvironment().getSkyBox().getBackground().addObject(object);
            } else {
                this.getWorld().addObject(object);
            }
        }
    }

    public void readMapProject() {
        final WBenchWorld world = this.getWorld();
        final File file = this.getCurrentMapProject().getPathToDataMapFile().toFile();
        if (!file.exists()) {
            return;
        }

        JSONFileManaging jsonFileManaging = TagsContainer.createJSONFileManaging();
        MapObjectsDataPack mapObjectsDataPack;
        try {
            mapObjectsDataPack = jsonFileManaging.readFromFile(file, new TypeToken<>() {
            }, null);
            final SkyData skyData = mapObjectsDataPack.getSkyData();
            final SunData sunData = mapObjectsDataPack.getSunData();
            final FogData fogData = mapObjectsDataPack.getFogData();
            final ObjectsData objectsData = mapObjectsDataPack.getObjectsData();
            final ShadowsData shadowsData = mapObjectsDataPack.getShadowsData();

            if (skyData != null && this.getMapObjectTemplates().getSkyBoxes().get(skyData.getNameId()) != null) {
                ICubeMapProgram cubeMapProgram = this.getMapObjectTemplates().getSkyBoxes().get(skyData.getNameId()).getCubeMapProgram();
                world.getEnvironment().getSkyBox().setSky2DTexture(cubeMapProgram);
                world.getEnvironment().getSkyBox().getBackground().setViewScaling(skyData.backGroundScaling);
                Log.get().debug("Read SkyData");
            } else {
                Log.get().error("Couldn't get SkyData");
            }

            if (sunData != null) {
                world.getEnvironment().getSkyBox().getSun().setSunBrightness(sunData.brightness);
                world.getEnvironment().getSkyBox().getSun().setLightPosition(sunData.position);
                world.getEnvironment().getSkyBox().getSun().setLightColor(sunData.color);
                Log.get().debug("Read SunData");
            } else {
                Log.get().error("Couldn't get SunData");
            }

            if (fogData != null) {
                world.getEnvironment().getSkyBox().setSkyCoveredByFog(fogData.isSkyCoveredByFog);
                world.getEnvironment().getFogScene().setFogColor(fogData.color);
                world.getEnvironment().getFogScene().setFogDensity(fogData.density);
                Log.get().debug("Read FogData");
            } else {
                Log.get().error("Couldn't get FogData");
            }

            if (shadowsData != null) {
                world.getEnvironment().getShadowScene().getSunLightShadow().setCascadeSplits(shadowsData.splits);
                Log.get().debug("Read ShadowsData");
            } else {
                Log.get().error("Couldn't get ShadowsData");
            }

            if (objectsData != null) {
                final List<WBenchObject<?>> defaultBackgroundPropsToCreate = new ArrayList<>();
                final List<WBenchObject<?>> defaultPropsToCreate = new ArrayList<>();
                final List<WBenchObject<?>> defaultEntitiesToCreate = new ArrayList<>();
                final List<WBenchObject<?>> defaultMarkersToCreate = new ArrayList<>();

                if (objectsData.propObjects != null) {
                    this.handleObjects(
                            false,
                            objectsData.propObjects,
                            (path, name) -> this.getMapObjectTemplates().getProps().find(path, name),
                            (tpl, t) -> {
                                WBenchCommonObject wBenchCommonObject = new WBenchCommonObject(this.getWorld(), Objects.requireNonNull(tpl), t.getTagsContainer());
                                wBenchCommonObject.getRenderAttributes().setRenderProperties(t.getRenderProperties().copy());
                                return wBenchCommonObject;
                            },
                            defaultPropsToCreate,
                            (rowMapObjectData -> new WBenchObjectTemplate(new WBenchObject.ID(rowMapObjectData.getObjectNameId(), rowMapObjectData.getObjectPath()), null, RenderAttributes.getDefaultIndirect(), rowMapObjectData.getTagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ)))
                    );
                    Log.get().debug("Read Props: " + objectsData.propObjects.size());
                } else {
                    Log.get().debug("Map has no props");
                }

                if (objectsData.entityObjects != null) {
                    this.handleObjects(
                            false,
                            objectsData.entityObjects,
                            (path, name) -> this.getMapObjectTemplates().getEntities().find(path, name),
                            (tpl, t) -> {
                                WBenchCommonObject wBenchCommonObject = new WBenchCommonObject(this.getWorld(), Objects.requireNonNull(tpl), t.getTagsContainer());
                                wBenchCommonObject.getRenderAttributes().setRenderProperties(t.getRenderProperties().copy());
                                return wBenchCommonObject;
                            },
                            defaultEntitiesToCreate,
                            (rowMapObjectData -> new WBenchObjectTemplate(new WBenchObject.ID(rowMapObjectData.getObjectNameId(), rowMapObjectData.getObjectPath()), null, RenderAttributes.getDefaultIndirect(), rowMapObjectData.getTagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ)))
                    );
                    Log.get().debug("Read Entities: " + objectsData.entityObjects.size());
                } else {
                    Log.get().debug("Map has no entities");
                }

                if (objectsData.markerObjects != null) {
                    this.handleObjects(
                            false,
                            objectsData.markerObjects,
                            (path, name) -> this.getMapObjectTemplates().getMarkers().find(path, name),
                            (tpl, t) -> new WBenchMarkerObject(this.getWorld(), Objects.requireNonNull(tpl), t.getTagsContainer(), tpl.getColor(), tpl.isTransparent()),
                            defaultMarkersToCreate,
                            (rowMapObjectData -> new WBenchMarkerTemplate(new WBenchObject.ID(rowMapObjectData.getObjectNameId(), rowMapObjectData.getObjectPath()), WBenchResourceManager.gameEditorModelAssets.markerDefault, rowMapObjectData.getTagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ), new Vector3f(1.0f), false))
                    );
                    Log.get().debug("Read Markers: " + objectsData.markerObjects.size());
                } else {
                    Log.get().debug("Map has no markers");
                }

                if (objectsData.backgroundProps != null) {
                    this.handleObjects(
                            true,
                            objectsData.backgroundProps,
                            (path, name) -> this.getMapObjectTemplates().getProps().find(path, name),
                            (tpl, t) -> {
                                WBenchCommonObject wBenchCommonObject = new WBenchCommonObject(this.getWorld(), Objects.requireNonNull(tpl), t.getTagsContainer());
                                wBenchCommonObject.getRenderAttributes().setRenderProperties(t.getRenderProperties());
                                return wBenchCommonObject;
                            },
                            defaultBackgroundPropsToCreate,
                            (rowMapObjectData -> new WBenchObjectTemplate(new WBenchObject.ID(rowMapObjectData.getObjectNameId(), rowMapObjectData.getObjectPath()), null, RenderAttributes.getDefaultIndirect(), rowMapObjectData.getTagsContainer(), new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ)))
                    );
                    Log.get().debug("Read Background prop: " + objectsData.markerObjects.size());
                } else {
                    Log.get().debug("Map has no background props");
                }

                if (objectsData.pointLights != null) {
                    for (RowMapObjectData template : objectsData.pointLights) {
                        WBenchPointLightObject object = WBenchPointLightObject.create(template.getObjectNameId(), this.getWorld(), template.getTagsContainer());
                        object.setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
                        object.setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
                        object.setScaling(template.getScaling() == null ? new Vector3f(0.0f) : template.getScaling());
                        object.setId(template.getId());
                        this.getWorld().addObject(object);
                    }
                    Log.get().debug("Read Point Lights: " + objectsData.pointLights.size());
                } else {
                    Log.get().debug("Map has no point lights");
                }

                final WBenchSkyBackground wBenchSkyBackground = (WBenchSkyBackground) world.getEnvironment().getSkyBox().getBackground();
                WBenchWorld.calcFreeIds(world.getFreeIds(), world.getSceneObjects());
                WBenchWorld.calcFreeIds(wBenchSkyBackground.getFreeIds(), wBenchSkyBackground.getSkySceneObjects());

                defaultPropsToCreate.forEach(world::addObject);
                defaultEntitiesToCreate.forEach(world::addObject);
                defaultMarkersToCreate.forEach(world::addObject);
                defaultBackgroundPropsToCreate.forEach(wBenchSkyBackground::addObject);
            } else {
                Log.get().error("Couldn't get ObjectsData");
            }
            Log.get().info("Saved project");
        } catch (Exception e) {
            throw new JGemsIOException(e);
        }
    }

    @SuppressWarnings("all")
    public void saveMapProject(boolean wait) {
        final WBenchWorld world = this.getWorld();

        JSONFileManaging jsonFileManaging = TagsContainer.createJSONFileManaging();
        final SunLight sunLight = world.getEnvironment().getSkyBox().getSun();
        final FogScene fogScene = world.getEnvironment().getFogScene();
        final IShadowScene shadowScene = world.getEnvironment().getShadowScene();

        final Set<SceneObject> objectsCopy = new HashSet<>(world.getSceneObjects());
        final Set<SceneObject> backgroundCopy = new HashSet<>(world.getEnvironment().getSkyBox().getBackground().getSkySceneObjects());
        final Set<RowMapObjectData> props = new HashSet<>();
        final Set<RowMapObjectData> backgroundProps = new HashSet<>();
        final Set<RowMapObjectData> entities = new HashSet<>();
        final Set<RowMapObjectData> markers = new HashSet<>();
        final Set<RowMapObjectData> pointLights = new HashSet<>();
        final SkyBox skyBox = world.getEnvironment().getSkyBox();

        final MapObjectTemplatesManager.SkyBoxTemplate skyBoxTemplate = this.getMapObjectTemplates().getSkyBoxesCache().get(skyBox.getTexture());

        final SunData sunData = new SunData(world.getEnvironment().getSkyBox().isDrawSunOnSkyBox(), sunLight.getSunBrightness(), sunLight.getLightColor(), sunLight.getLightPosition());
        final FogData fogData = new FogData(skyBox.isSkyCoveredByFog(), fogScene.getFogDensity(), fogScene.getFogColor());
        final SkyData skyData = new SkyData(skyBoxTemplate == null ? "" : skyBoxTemplate.getNameId(), world.getEnvironment().getSkyBox().getBackground().getViewScaling());
        final ShadowsData shadowsData = new ShadowsData(shadowScene.getSunLightShadow().getCascadeSplits());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Map<String, Set<RowMapObjectData>> categoryMap = new HashMap<>();
                categoryMap.put(MapObjectsIdentifiers.PROP, props);
                categoryMap.put(MapObjectsIdentifiers.ENTITY, entities);
                categoryMap.put(MapObjectsIdentifiers.MARKER, markers);
                categoryMap.put(MapObjectsIdentifiers.POINT_LIGHT, pointLights);
                categoryMap.put("backgroundProps", backgroundProps);

                for (SceneObject sceneObject : objectsCopy) {
                    if (sceneObject instanceof WBenchObject) {
                        WBenchObject wBenchObject = (WBenchObject) sceneObject;
                        final WBenchObject.ID objectId = wBenchObject.getObjectNameId();
                        final String nameId = objectId.nameId();

                        for (Map.Entry<String, Set<RowMapObjectData>> entry : categoryMap.entrySet()) {
                            if (nameId.startsWith(entry.getKey())) {
                                entry.getValue().add(new RowMapObjectData(wBenchObject.getListID(), wBenchObject.getRenderAttributes().getProperties(), objectId.nameId(), objectId.objectPath(), wBenchObject.getTagsContainer(), wBenchObject.getPosition(), wBenchObject.getRotation(), wBenchObject.getScaling()));
                                break;
                            }
                        }
                    }
                }
                for (SceneObject sceneObject : backgroundCopy) {
                    if (sceneObject instanceof WBenchObject) {
                        WBenchObject wBenchObject = (WBenchObject) sceneObject;
                        final WBenchObject.ID objectId = wBenchObject.getObjectNameId();
                        final String nameId = objectId.nameId();
                        categoryMap.get("backgroundProps").add(new RowMapObjectData(wBenchObject.getListID(), wBenchObject.getRenderAttributes().getProperties(), objectId.nameId(), objectId.objectPath(), wBenchObject.getTagsContainer(), wBenchObject.getPosition(), wBenchObject.getRotation(), wBenchObject.getScaling()));
                    }
                }

                final ObjectsData objectsData = new ObjectsData(props, markers, entities, pointLights, backgroundProps);
                MapObjectsDataPack mapObjectsDataPack = new MapObjectsDataPack();
                jsonFileManaging.setMatch(MapObjectsDataPack.class, mapObjectsDataPack.getSerializationRules());
                mapObjectsDataPack.set(fogData, sunData, objectsData, skyData, shadowsData);

                jsonFileManaging.writeToFile(mapObjectsDataPack, this.getCurrentMapProject().getPathToDataMapFile().toFile(), null);
                this.saveMapProjectFile(this.currentMapProject);
            } catch (Exception e) {
                Log.get().exception(e);
                LoggingManager.showExceptionDialog("Where was an error, while saving map!", e);
            } finally {
                executorService.shutdown();
            }
        });
        if (wait) {
            try {
                executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new JGemsRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("all")
    private void refresScriptFolder(JGemsPath path, String name) {
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        File scripts = JGemsGaming.getScriptsFolder(path).toFile();
        if (!scripts.mkdirs()) {
            this.currentMapProject.refreshScriptFiles();
        }
    }

    public void closeMapProject(boolean save) {
        if (this.getCurrentMapProject() != null) {
            Log.get().info("Closing project " + this.getCurrentMapProject());
            if (save) {
                this.saveMapProject(true);
            }
            this.closeWorkingSpace(WBenchOpenGLRenderer.getGameEditorInterface());
            this.destroyLocalResources(this.getCurrentMapProject());
            this.currentMapProject = null;
            Log.get().info("Map successfully closed");
        }
    }

    public boolean deleteMapProjectFolder(JGemsPath path) throws IOException {
        return path.recursiveDelete();
    }

    public boolean openMapProject(JGemsPath path) {
        try {
            File projectFolder = new File(path.fullPath());
            if (!projectFolder.exists() || !projectFolder.isDirectory()) {
                throw new JGemsIOException("Invalid files: " + path);
            }

            File[] files = projectFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE));
            if (files == null || files.length != 1) {
                throw new JGemsIOException("Couldn't find WBenchMapProject file: " + path + " (" + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE + ")");
            }

            File projectFile = files[0];
            WBenchMapProject wBenchMapProject = this.readMainFile(new JGemsPath(projectFile.getPath()), false);
            if (wBenchMapProject == null) {
                return false;
            }
            wBenchMapProject.checkVersion();

            this.refresScriptFolder(path, wBenchMapProject.getMapName());
            Log.get().info("Opened WBenchMapProject: " + wBenchMapProject);
            Log.get().info(wBenchMapProject.getMapDescription());

            this.initLocalResources(wBenchMapProject);
            LoadingInterfaceSwing.setResource("JSON Processing...");
            this.readMapProject();
            this.initWorkingSpace(WBenchOpenGLRenderer.getMapEditorInterface());

            return true;
        } catch (JGemsIOException e) {
            if (this.getCurrentMapProject() != null) {
                this.destroyLocalResources(this.getCurrentMapProject());
                this.currentMapProject = null;
            }
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!", e);
            Log.get().exception(e);
            return false;
        } finally {
            LoadingInterfaceSwing.dispose();
        }
    }

    public WBenchMapProject readMainFile(JGemsPath path, boolean preview) {
        return this.readMainFile(path.toFile(), preview);
    }

    public WBenchMapProject readMainFile(File file, boolean preview) {
        try {
            JSONFileManaging jsonFileManaging = JSONFileManaging.createSerializationRules();
            WBenchMapProject wBenchMapProject = jsonFileManaging.readFromFile(file, new TypeToken<>() {}, null);
            wBenchMapProject.setAbsolutePath(new JGemsPath(file.toPath()).getAbsolutePathDirectory());
            if (!preview) {
                this.currentMapProject = wBenchMapProject;
            }
            return wBenchMapProject;
        } catch (JGemsIOException | JsonSyntaxException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!", e);
            Log.get().exception(e);
            return null;
        }
    }

    private void initLocalResources(WBenchMapProject wBenchMapProject) {
        LoadingInterfaceSwing.invoke();
        //WBenchResourceManager.createLocalGameEditorShaders();
        //WBenchResourceManager.setDefaultRenderTableValues();
        WBench.get().getResourceManager().initLocalMapEditorResources();
        WBench.get().getResourceManager().loadLocalMapEditorResources();
        this.getWorld().onWorldStart();
        ((IProjectActionsCallback) WBench.get().getScreen().getScene().getSceneRenderer()).onOpeningProject(WBench.get().getResourceManager(), wBenchMapProject);
    }

    private void destroyLocalResources(WBenchMapProject wBenchMapProject) {
        ((IProjectActionsCallback) WBench.get().getScreen().getScene().getSceneRenderer()).onClosingProject(WBench.get().getResourceManager(), wBenchMapProject);
        this.getWorld().onWorldEnd();
        WBench.get().getResourceManager().destroyLocalMapEditorResources();
        this.getMapObjectTemplates().clear();
        ((MapEditorInterface) WBenchOpenGLRenderer.getMapEditorInterface()).clear();
    }

    private void initWorkingSpace(DearUIInterface dearUIInterface) {
        this.getWorld().setCamera(new ControlledCamera(WBench.get().getControllerDispatcher().getCurrentController(), new Vector3f(0.0f, 5.0f, 0.0f), new Vector3f()));
        WBench.get().openInterface(dearUIInterface);
    }

    public MapObjectTemplatesManager getMapObjectTemplates() {
        return this.mapObjectTemplates;
    }

    private void closeWorkingSpace(DearUIInterface dearUIInterface) {
        this.saveMapProject(true);
        world.setCamera(null);
        WBench.get().openInterface(dearUIInterface);
    }

    public WBenchWorld getWorld() {
        return this.world;
    }

    public @Nullable WBenchMapProject getCurrentMapProject() {
        return this.currentMapProject;
    }
}