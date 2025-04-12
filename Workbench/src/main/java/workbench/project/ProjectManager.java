package workbench.project;

import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import com.google.gson.JsonSyntaxException;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.environment.fog.FogScene;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.mapping.data.*;
import javagems3d.mapping.data.items.FogData;
import javagems3d.mapping.data.items.ObjectsData;
import javagems3d.mapping.data.items.SkyData;
import javagems3d.mapping.data.items.SunData;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.JGemsMapping;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.objects.*;
import workbench.graphics.scene.renderer.IProjectActionsCallback;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.EditorInterface;
import workbench.graphics.scene.world.WBenchWorld;
import javagems3d.mapping.data.templates.MapObjectTemplate;
import workbench.resources.WBenchResourceManager;
import workbench.resources.frame.LoadingInterfaceSwing;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public final class ProjectManager {
    private final ProjectTemplates projectTemplates;
    private WBenchProject currentWBenchProject;
    private WBenchWorld world;

    public ProjectManager() {
        this.projectTemplates = new ProjectTemplates();
        this.currentWBenchProject = null;
        this.world = null;
    }

    private void setCurrentProject(JGemsPath path, WBenchProject currentWBenchProject) {
        this.currentWBenchProject = currentWBenchProject;
        currentWBenchProject.setCurrentProjectPath(path);
    }

    public void setWorld(@NotNull WBenchWorld world) {
        this.world = world;
    }

    @SuppressWarnings("all")
    public boolean createProject(JGemsPath absPath, JGemsPath path, String name) {
        try {
            WBenchProject WBenchProject = new WBenchProject(JGemsMapping.DATA_VERSION, name);
            this.setCurrentProject(path, WBenchProject);
            this.createProjectSystemFiles(absPath, name);
            this.saveProjectFile();
            Log.get().debug("Created WBenchProject: " + WBenchProject);

            this.initLocalResources(WBenchProject);
            this.initWorkingSpace(WBenchOpenGLRenderer.getEditorInterface());
            LoadingInterfaceSwing.dispose();

            return true;
        } catch (JGemsIOException e) {
            Log.get().showExceptionDialog("Internal error! Couldn't create project!\n" + e.getMessage());
            Log.get().exception(e);
            return false;
        }
    }

    private void saveProjectFile() {
        JSONFileManaging jsonFileManaging = JSONFileManaging.create();
        jsonFileManaging.writeToFile(this.getCurrentProject(), this.getCurrentProject().getCurrentProjectPath().toFile(), null);
    }

    private <T> void handleObjects(Set<MapObjectTemplate> templates, BiFunction<String, String, T> templateFinder, BiFunction<T, MapObjectTemplate, WBenchObject> objectCreator) {
        for (MapObjectTemplate template : templates) {
            final String name = template.getObjectId();
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
            this.getWorld().addObject(object);
        }
    }

    public void readProject() {
        final WBenchWorld world = this.getWorld();
        final String mapDataFile = this.getCurrentProject().getMapDataFile();
        if (mapDataFile == null || mapDataFile.isEmpty()) {
            return;
        }

        final File file = new File(this.getCurrentProject().getCurrentProjectPath().getDirectory().getFullPath(), mapDataFile);
        if (!file.exists()) {
            return;
        }

        JSONFileManaging jsonFileManaging = JSONFileManaging.create(new Pair<>(TagsContainer.class, TagsContainer.TAGS_CONTAINER_SERIALIZATION_RULE));
        MapDataPack mapDataPack = new MapDataPack();
        try {
            mapDataPack = jsonFileManaging.readFromFile(file, MapDataPack.class, null);

            final SkyData skyData = mapDataPack.getSkyData();
            final SunData sunData = mapDataPack.getSunData();
            final FogData fogData = mapDataPack.getFogData();
            final ObjectsData objectsData = mapDataPack.getObjectsData();

            if (skyData != null) {
                ICubeMapProgram cubeMapProgram = this.getProjectObjects().getSkyBoxes().get(skyData.skyboxPath);
                world.getEnvironment().getSkyBox().setSky2DTexture(cubeMapProgram);
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
                world.getEnvironment().getFogManager().setColor(fogData.color);
                world.getEnvironment().getFogManager().setDensity(fogData.density);
                Log.get().debug("Read FogData");
            } else {
                Log.get().error("Couldn't get FogData");
            }

            if (objectsData != null) {
                if (objectsData.propObjects != null) {
                    this.handleObjects(objectsData.propObjects, (group, name) -> this.getProjectObjects().getPropGroups().get(group).find(name), (tpl, t) -> new WBenchCommonObject(this.getWorld(), tpl, t.getTagsContainer()));
                    Log.get().debug("Read Props: " + objectsData.propObjects.size());
                } else {
                    Log.get().warn("Map has no props");
                }

                if (objectsData.entityObjects != null) {
                    this.handleObjects(objectsData.entityObjects, (group, name) -> this.getProjectObjects().getEntityGroups().get(group).find(name), (tpl, t) -> new WBenchCommonObject(this.getWorld(), tpl, t.getTagsContainer()));
                    Log.get().debug("Read Entities: " + objectsData.entityObjects.size());
                } else {
                    Log.get().warn("Map has no entities");
                }

                if (objectsData.markerObjects != null) {
                    this.handleObjects(objectsData.markerObjects, (group, name) -> this.getProjectObjects().getMarkerGroups().get(group).find(name), (tpl, t) -> new WBenchMarkerObject(this.getWorld(), tpl, t.getTagsContainer(), tpl.getColor(), tpl.isTransparent()));
                    Log.get().debug("Read Markers: " + objectsData.markerObjects.size());
                } else {
                    Log.get().warn("Map has no markers");
                }

                if (objectsData.pointLights != null) {
                    for (MapObjectTemplate template : objectsData.pointLights) {
                        WBenchPointLightObject object = WBenchPointLightObject.create(template.getObjectId(), this.getWorld(), template.getTagsContainer());
                        object.setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
                        object.setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
                        object.setScaling(template.getScaling() == null ? new Vector3f(0.0f) : template.getScaling());
                        object.setId(template.getId());
                        this.getWorld().addObject(object);
                    }
                    Log.get().debug("Read Point Lights: " + objectsData.pointLights.size());
                } else {
                    Log.get().warn("Map has no point lights");
                }

                world.calcFreeIds();
            } else {
                Log.get().error("Couldn't get ObjectsData");
            }
            Log.get().info("Saved project");
        } catch (Exception e) {
            throw new JGemsIOException(e);
        }
    }

    public void compile() { //TODO
        this.saveProjectFile();
        this.saveProject(false);
    }

    @SuppressWarnings("all")
    public void saveProject(boolean wait) {
        final WBenchWorld world = this.getWorld();

        JSONFileManaging jsonFileManaging = JSONFileManaging.create(new Pair<>(TagsContainer.class, TagsContainer.TAGS_CONTAINER_SERIALIZATION_RULE));
        final SunLight sunLight = world.getEnvironment().getSkyBox().getSun();
        final FogScene fogScene = world.getEnvironment().getFogManager();
        final Set<SceneObject> objectsCopy = new HashSet<>(world.getSceneObjects());
        final Set<MapObjectTemplate> props = new HashSet<>();
        final Set<MapObjectTemplate> entities = new HashSet<>();
        final Set<MapObjectTemplate> markers = new HashSet<>();
        final Set<MapObjectTemplate> pointLights = new HashSet<>();
        final SkyBox skyBox = world.getEnvironment().getSkyBox();

        final SunData sunData = new SunData(sunLight.getSunBrightness(), sunLight.getLightColor(), sunLight.getLightPosition());
        final FogData fogData = new FogData(skyBox.isSkyCoveredByFog(), fogScene.getDensity(), fogScene.getColor());
        final SkyData skyData = new SkyData(this.getProjectObjects().getSkyBoxes().inverse().get(skyBox.getTexture()));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Map<String, Set<MapObjectTemplate>> categoryMap = new HashMap<>();
                categoryMap.put(MapObjectsIdentifiers.PROP, props);
                categoryMap.put(MapObjectsIdentifiers.ENTITY, entities);
                categoryMap.put(MapObjectsIdentifiers.MARKER, markers);
                categoryMap.put(MapObjectsIdentifiers.POINT_LIGHT, pointLights);

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
                final ObjectsData objectsData = new ObjectsData(props, markers, entities, pointLights);
                MapDataPack mapDataPack = new MapDataPack();
                jsonFileManaging.setMatch(MapDataPack.class, mapDataPack.getSerializationRules());
                mapDataPack.set(fogData, sunData, objectsData, skyData);

                final String mapDataFile = this.getCurrentProject().getProjectName() + JGemsMapping.MAP_DATA_FILE;
                this.getCurrentProject().setMapDataFile(mapDataFile);
                jsonFileManaging.writeToFile(mapDataPack, new File(this.getCurrentProject().getCurrentProjectPath().getDirectory().getFullPath(), mapDataFile), null);
                this.saveProjectFile();
            } catch (Exception e) {
                Log.get().exception(e);
                LoggingManager.showExceptionDialog("Where was an error, while saving map!\n\n" + e.getMessage());
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
    private void createProjectSystemFiles(JGemsPath path, String name) {
       // File compiled = new File(new JGemsPath(path, "compiled").getFullPath());
        File scripts = new File(new JGemsPath(path, "scripts").getFullPath());

       // compiled.mkdirs();
        scripts.mkdirs();
    }

    public void closeProject(boolean total) {
        if (this.getCurrentProject() != null) {
            Log.get().info("Closing project " + this.getCurrentProject());
            if (!total) {
                this.closeWorkingSpace(WBenchOpenGLRenderer.getProjectInterface());
            }
            this.destroyLocalResources(this.getCurrentProject());
            this.currentWBenchProject = null;
            Log.get().info("WBenchProject successfully closed");
        }
    }

    @SuppressWarnings("all")
    public boolean openProject(JGemsPath path) {
        try {
            File projectFolder = new File(path.getFullPath());
            if (!projectFolder.exists() || !projectFolder.isDirectory()) {
                throw new JGemsIOException("Invalid path: " + path);
            }

            File[] files = projectFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(JGemsMapping.MAP_PROJECT_FILE));
            if (files == null || files.length != 1) {
                throw new JGemsIOException("Couldn't find WBenchProject file: " + path);
            }

            File projectFile = files[0];
            WBenchProject WBenchProject = this.readMainFile(new JGemsPath(projectFile.getPath()));
            if (WBenchProject == null) {
                return false;
            }
            WBenchProject.checkVersion();

            this.createProjectSystemFiles(path, WBenchProject.getProjectName());
            Log.get().info("Opened WBenchProject: " + WBenchProject);
            Log.get().info(WBenchProject.getInformation());

            this.initLocalResources(WBenchProject);
            LoadingInterfaceSwing.setResource("JSON Processing...");
            this.readProject();
            this.initWorkingSpace(WBenchOpenGLRenderer.getEditorInterface());

            return true;
        } catch (JGemsIOException e) {
            if (this.getCurrentProject() != null) {
                this.destroyLocalResources(this.getCurrentProject());
                this.currentWBenchProject = null;
            }
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!\n" + e.getMessage());
            Log.get().exception(e);
            return false;
        } finally {
            LoadingInterfaceSwing.dispose();
        }
    }

    public WBenchProject readMainFile(JGemsPath path) {
        try {
            JSONFileManaging jsonFileManaging = JSONFileManaging.create();
            WBenchProject WBenchProject = jsonFileManaging.readFromFile(new File(path.toString()), WBenchProject.class, null);
            this.setCurrentProject(path, WBenchProject);
            return WBenchProject;
        } catch (JGemsIOException | JsonSyntaxException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!\n" + e.getMessage());
            Log.get().exception(e);
            return null;
        }
    }

    private void initLocalResources(WBenchProject WBenchProject) {
        LoadingInterfaceSwing.invoke();
        WBenchResourceManager.createLocalShaders();
        WBenchResourceManager.setDefaultRenderTableValues();
        WBench.get().getResourceManager().initLocalResources();
        WBench.get().getResourceManager().loadLocalResources();
        this.getWorld().onWorldStart();
        ((IProjectActionsCallback) WBench.get().getScreen().getScene().getSceneRenderer()).onOpeningProject(WBench.get().getResourceManager(), WBenchProject);
    }

    private void destroyLocalResources(WBenchProject WBenchProject) {
        ((IProjectActionsCallback) WBench.get().getScreen().getScene().getSceneRenderer()).onClosingProject(WBench.get().getResourceManager(), WBenchProject);
        this.getWorld().onWorldEnd();
        WBench.get().getResourceManager().destroyLocalResources();
        this.getProjectObjects().clear();
        ((EditorInterface) WBenchOpenGLRenderer.getEditorInterface()).clear();
    }

    private void initWorkingSpace(DearUIInterface dearUIInterface) {
        this.getWorld().setCamera(new ControlledCamera(WBench.get().getControllerDispatcher().getCurrentController(), new Vector3f(0.0f, 5.0f, 0.0f), new Vector3f()));
        WBench.get().openInterface(dearUIInterface);
    }

    public ProjectTemplates getProjectObjects() {
        return this.projectTemplates;
    }

    private void closeWorkingSpace(DearUIInterface dearUIInterface) {
        this.saveProject(true);
        world.setCamera(null);
        WBench.get().openInterface(dearUIInterface);
    }

    public WBenchWorld getWorld() {
        return this.world;
    }

    public WBenchProject getCurrentProject() {
        return this.currentWBenchProject;
    }
}