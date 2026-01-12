package workbench.project.map;

import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import com.google.gson.JsonSyntaxException;
import javagems3d.JGems3D;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.environment.fog.FogScene;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.mapping.data.*;
import javagems3d.mapping.data.items.*;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.environment.components.WBenchSkyBackground;
import workbench.graphics.objects.*;
import workbench.graphics.scene.renderer.IProjectActionsCallback;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.world.WBenchWorld;
import javagems3d.mapping.data.templates.MapObjectTemplate;
import workbench.resources.WBenchResourceManager;
import workbench.resources.frame.LoadingInterfaceSwing;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public final class WBenchMapProjectManager {
    public static final String SCRIPTS_PATH = "scripts";

    private final ProjectMapObjectTemplates projectMapObjectTemplates;
    private WBenchMapProject currentMapProject;
    private WBenchWorld world;

    public WBenchMapProjectManager() {
        this.projectMapObjectTemplates = new ProjectMapObjectTemplates();
        this.currentMapProject = null;
        this.world = null;
    }

    public void setWorld(@NotNull WBenchWorld world) {
        this.world = world;
    }

    public boolean createMapProject(JGemsPath absPath, JGemsPath path, String name) {
        try {
            WBenchMapProject wBenchMapProject = new WBenchMapProject(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_DATA_VERSION, name);
            wBenchMapProject.setCurrentProjectPath(path);
            //this.setCurrentProject(path, wBenchMapProject);
            this.createMapSystemFiles(absPath, name);
            this.saveMapProjectFile(wBenchMapProject);
            Log.get().debug("Created WBenchMapProject: " + wBenchMapProject + ". Path: " + path + " (" + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE + ")");

            return true;
        } catch (JGemsIOException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't create object!", e);
            Log.get().exception(e);
            this.currentMapProject = null;
            return false;
        }
    }

    public void editMapProjectDescription(@NotNull WBenchMapProject mapProjectData, @NotNull String newText) {
        mapProjectData.setInformation(newText);
        this.saveMapProjectFile(mapProjectData);
    }

    public void editMapProjectDescription(@NotNull String newText) {
        this.editMapProjectDescription(this.getCurrentMapProject(), newText);
    }

    private void saveMapProjectFile(@NotNull WBenchMapProject wBenchMapProject) {
        JSONFileManaging jsonFileManaging = JSONFileManaging.create();
        jsonFileManaging.writeToFile(wBenchMapProject, wBenchMapProject.getCurrentProjectPath().toFile(), null);
    }

    private <T> void handleObjects(boolean background, Set<MapObjectTemplate> templates, BiFunction<String, String, T> templateFinder, BiFunction<T, MapObjectTemplate, WBenchObject> objectCreator) {
        for (MapObjectTemplate template : templates) {
            final String name = template.getObjectNameId();
            final String group = template.getObjectGroup();

            T tpl = null;
            try {
                tpl = templateFinder.apply(group, name);
                if (tpl == null) {
                    throw new JGemsNullException();
                }
            } catch (Exception e) {
                Log.get().error("Couldn't find template: " + group + "/" + name);
                continue;
            }

            WBenchObject object = objectCreator.apply(tpl, template);
            object.setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
            object.setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
            object.setScaling(template.getScaling() == null ? new Vector3f(0.0f) : template.getScaling());
            object.setId(template.getId());
            if (background) {
                this.getWorld().getEnvironment().getSkyBox().getBackground().addObject(object);
            } else {
                this.getWorld().addObject(object);
            }
        }
    }

    public void readMapProject() {
        final WBenchWorld world = this.getWorld();
        final String mapDataFile = this.getCurrentMapProject().getMapDataFile();
        if (mapDataFile == null || mapDataFile.isEmpty()) {
            return;
        }

        final File file = new File(this.getCurrentMapProject().getCurrentProjectPath().getAbsolutePathDirectory().getFullPath(), mapDataFile);
        if (!file.exists()) {
            return;
        }

        JSONFileManaging jsonFileManaging = TagsContainer.createJSONFileManaging();
        MapObjectsDataPack mapObjectsDataPack;
        try {
            mapObjectsDataPack = jsonFileManaging.readFromFile(file, MapObjectsDataPack.class, null);
            final SkyData skyData = mapObjectsDataPack.getSkyData();
            final SunData sunData = mapObjectsDataPack.getSunData();
            final FogData fogData = mapObjectsDataPack.getFogData();
            final ObjectsData objectsData = mapObjectsDataPack.getObjectsData();
            final ShadowsData shadowsData = mapObjectsDataPack.getShadowsData();

            if (skyData != null) {
                ICubeMapProgram cubeMapProgram = this.getMapObjectTemplates().getSkyBoxes().get(skyData.skyboxPath);
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
                if (objectsData.propObjects != null) {
                    this.handleObjects(false, objectsData.propObjects, (group, name) -> this.getMapObjectTemplates().getPropGroups().get(group).find(name), (tpl, t) -> new WBenchCommonObject(this.getWorld(), tpl, t.getTagsContainer()));
                    Log.get().debug("Read Props: " + objectsData.propObjects.size());
                } else {
                    Log.get().debug("Map has no props");
                }

                if (objectsData.entityObjects != null) {
                    this.handleObjects(false, objectsData.entityObjects, (group, name) -> this.getMapObjectTemplates().getEntityGroups().get(group).find(name), (tpl, t) -> new WBenchCommonObject(this.getWorld(), tpl, t.getTagsContainer()));
                    Log.get().debug("Read Entities: " + objectsData.entityObjects.size());
                } else {
                    Log.get().debug("Map has no entities");
                }

                if (objectsData.markerObjects != null) {
                    this.handleObjects(false, objectsData.markerObjects, (group, name) -> this.getMapObjectTemplates().getMarkerGroups().get(group).find(name), (tpl, t) -> new WBenchMarkerObject(this.getWorld(), tpl, t.getTagsContainer(), tpl.getColor(), tpl.isTransparent()));
                    Log.get().debug("Read Markers: " + objectsData.markerObjects.size());
                } else {
                    Log.get().debug("Map has no markers");
                }

                if (objectsData.backgroundProps != null) {
                    this.handleObjects(true, objectsData.backgroundProps, (group, name) -> this.getMapObjectTemplates().getPropGroups().get(group).find(name), (tpl, t) -> new WBenchCommonObject(this.getWorld(), tpl, t.getTagsContainer()));
                    Log.get().debug("Read Background prop: " + objectsData.markerObjects.size());
                } else {
                    Log.get().debug("Map has no background props");
                }

                if (objectsData.pointLights != null) {
                    for (MapObjectTemplate template : objectsData.pointLights) {
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
        final Set<MapObjectTemplate> props = new HashSet<>();
        final Set<MapObjectTemplate> backgroundProps = new HashSet<>();
        final Set<MapObjectTemplate> entities = new HashSet<>();
        final Set<MapObjectTemplate> markers = new HashSet<>();
        final Set<MapObjectTemplate> pointLights = new HashSet<>();
        final SkyBox skyBox = world.getEnvironment().getSkyBox();

        final SunData sunData = new SunData(sunLight.getSunBrightness(), sunLight.getLightColor(), sunLight.getLightPosition());
        final FogData fogData = new FogData(skyBox.isSkyCoveredByFog(), fogScene.getFogDensity(), fogScene.getFogColor());
        final SkyData skyData = new SkyData(this.getMapObjectTemplates().getSkyBoxes().inverse().get(skyBox.getTexture()), world.getEnvironment().getSkyBox().getBackground().getViewScaling());
        final ShadowsData shadowsData = new ShadowsData(shadowScene.getSunLightShadow().getCascadeSplits());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Map<String, Set<MapObjectTemplate>> categoryMap = new HashMap<>();
                categoryMap.put(MapObjectsIdentifiers.PROP, props);
                categoryMap.put(MapObjectsIdentifiers.ENTITY, entities);
                categoryMap.put(MapObjectsIdentifiers.MARKER, markers);
                categoryMap.put(MapObjectsIdentifiers.POINT_LIGHT, pointLights);
                categoryMap.put("backgroundProps", backgroundProps);

                for (SceneObject sceneObject : objectsCopy) {
                    if (sceneObject instanceof WBenchObject) {
                        WBenchObject wBenchObject = (WBenchObject) sceneObject;
                        final WBenchObject.ID objectId = wBenchObject.getObjectId();
                        final String nameId = objectId.getNameId();

                        for (Map.Entry<String, Set<MapObjectTemplate>> entry : categoryMap.entrySet()) {
                            if (nameId.startsWith(entry.getKey())) {
                                entry.getValue().add(new MapObjectTemplate(wBenchObject.getId(), objectId.getNameId(), objectId.getGroupId(), wBenchObject.getTagsContainer(), wBenchObject.getPosition(), wBenchObject.getRotation(), wBenchObject.getScaling()));
                                break;
                            }
                        }
                    }
                }
                for (SceneObject sceneObject : backgroundCopy) {
                    if (sceneObject instanceof WBenchObject) {
                        WBenchObject wBenchObject = (WBenchObject) sceneObject;
                        final WBenchObject.ID objectId = wBenchObject.getObjectId();
                        final String nameId = objectId.getNameId();
                        categoryMap.get("backgroundProps").add(new MapObjectTemplate(wBenchObject.getId(), objectId.getNameId(), objectId.getGroupId(), wBenchObject.getTagsContainer(), wBenchObject.getPosition(), wBenchObject.getRotation(), wBenchObject.getScaling()));
                    }
                }

                final ObjectsData objectsData = new ObjectsData(props, markers, entities, pointLights, backgroundProps);
                MapObjectsDataPack mapObjectsDataPack = new MapObjectsDataPack();
                jsonFileManaging.setMatch(MapObjectsDataPack.class, mapObjectsDataPack.getSerializationRules());
                mapObjectsDataPack.set(fogData, sunData, objectsData, skyData, shadowsData);

                final String mapDataFile = this.getCurrentMapProject().getMapName() + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_DATA_FILE;
                this.getCurrentMapProject().setMapDataFile(mapDataFile);
                jsonFileManaging.writeToFile(mapObjectsDataPack, new File(this.getCurrentMapProject().getCurrentProjectPath().getAbsolutePathDirectory().getFullPath(), mapDataFile), null);
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
    private void createMapSystemFiles(JGemsPath path, String name) {
        File scripts = new File(new JGemsPath(path, WBenchMapProjectManager.SCRIPTS_PATH).getFullPath());
        scripts.mkdirs();
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
            File projectFolder = new File(path.getFullPath());
            if (!projectFolder.exists() || !projectFolder.isDirectory()) {
                throw new JGemsIOException("Invalid path: " + path);
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

            this.createMapSystemFiles(path, wBenchMapProject.getMapName());
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
        try {
            JSONFileManaging jsonFileManaging = JSONFileManaging.create();
            WBenchMapProject wBenchMapProject = jsonFileManaging.readFromFile(new File(path.toString()), WBenchMapProject.class, null);
            wBenchMapProject.setCurrentProjectPath(path);
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

    public ProjectMapObjectTemplates getMapObjectTemplates() {
        return this.projectMapObjectTemplates;
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