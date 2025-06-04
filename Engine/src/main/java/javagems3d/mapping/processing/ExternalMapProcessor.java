package javagems3d.mapping.processing;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.resources.Resource;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.ResourceMarker;
import api.application.workbench.resources.ResourceProp;
import api.application.workbench.resources.data.jgems.IJGemsObjectData;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.system.JGemsAPI;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.help.JGemsUtils;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.data.MapDataPack;
import javagems3d.mapping.data.ProjectData;
import javagems3d.mapping.data.items.FogData;
import javagems3d.mapping.data.items.ShadowsData;
import javagems3d.mapping.data.items.SkyData;
import javagems3d.mapping.data.items.SunData;
import javagems3d.mapping.data.templates.MapObjectTemplate;
import javagems3d.mapping.processing.base.MapProcessor;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.items.*;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.entities.kinematic.player.JGemsKinematicPlayer;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.Water;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class ExternalMapProcessor extends MapProcessor {
    private final JGemsPath pathToJG3DFile;
    private MapDataPack mapDataPack;
    private ProjectData projectData;

    public ExternalMapProcessor(JGemsPath pathToJG3DFile, boolean inJar) {
        super();
        this.pathToJG3DFile = pathToJG3DFile;

        this.readMap(pathToJG3DFile, inJar);
    }


    protected void readMap(JGemsPath pathToJG3DFile, boolean inJar) {
        final JSONFileManaging jsonFileManaging = TagsContainer.createJSONFileManaging();

        try {
            this.projectData = this.loadJson(jsonFileManaging, pathToJG3DFile, inJar, ProjectData.class);
            if (this.projectData == null) {
                throw new JGemsIOException("Couldn't load map(no project data): " + pathToJG3DFile);
            }
            this.projectData.checkVersion();

            this.mapDataPack = this.loadJson(jsonFileManaging, new JGemsPath(pathToJG3DFile.getDirectory(), this.projectData.getMapDataFile()), inJar, MapDataPack.class);
            if (this.mapDataPack == null) {
                throw new JGemsIOException("Couldn't load map(no map data): " + pathToJG3DFile);
            }

            Log.get().info("Found " + this.projectData.getScriptFiles().size() + " scripts");
            this.loadScripts(pathToJG3DFile, this.projectData.getScriptFiles(), inJar);
        } catch (Exception e) {
            throw new JGemsIOException(e);
        }
    }

    private void loadScripts(JGemsPath pathToJG3DFile, List<String> scriptPaths, boolean isJar) {
        try {
            for (String path : scriptPaths) {
                if (isJar) {
                    JGemsAPI.executeScript(JGemsHelper.files().readTextFromFileInJar(new JGemsPath(pathToJG3DFile.getDirectory(), "scripts", path)));
                } else {
                    JGemsAPI.executeScript(JGemsHelper.files().readTextFromFileOutsideJar(new JGemsPath(pathToJG3DFile.getDirectory(), "scripts",path)));
                }
            }
        } catch (JGemsIOException e) {
            Log.get().exception(e);
        }
    }

    private <T> T loadJson(JSONFileManaging jsonFileManaging, JGemsPath path, boolean inJar, Class<T> clazz) {
        if (inJar) {
            try (InputStream stream = JGems3D.loadFileFromJar(path)) {
                return jsonFileManaging.readFromInputStream(stream, clazz, null);
            } catch (IOException e) {
                throw new JGemsIOException(e);
            }
        } else {
            File file = path.toFile();
            if (!file.exists()) {
                throw new JGemsNullException("File " + path + " does not exist");
            }
            return jsonFileManaging.readFromFile(file, clazz, null);
        }
    }

    @Override
    public void init() {
    }

    protected abstract @Nullable SceneProp onProcessBackgroundProp(MapObjectTemplate template, JGemsPropData propData, SceneWorld sceneWorld, ISkyBackground background);
    protected abstract @Nullable SceneProp onProcessProp(MapObjectTemplate template, JGemsPropData propData, PhysicsWorld physicsWorld, SceneWorld sceneWorld, @Nullable List<PointLight> pointLightsToAttach);
    protected abstract @Nullable WorldItem onProcessEntity(MapObjectTemplate template, JGemsEntityData entityData, PhysicsWorld physicsWorld, SceneWorld sceneWorld, @Nullable List<PointLight> pointLightsToAttach);
    protected abstract void onProcessMarker(MapObjectTemplate template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld);

    protected abstract void preProcessing(MapDataPack mapDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld);
    protected abstract void postProcessing(MapDataPack mapDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld);

    protected @Nullable Pair<PointLight, Integer> onProcessPointLight(MapObjectTemplate template, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        final Vector4f tagColor = template.getTagsContainer().<TagColor>getTagItem(TagID.DEFAULT.COLOR3).getColorVector();
        final float brightness = template.getTagsContainer().<TagFloat>getTagItem(TagID.DEFAULT.BRIGHTNESS).getValue();
        final int attachedTo = template.getTagsContainer().<TagObjectsList>getTagItem(TagID.DEFAULT.OBJECT_LIST).getValue();
        final Vector3f offset = template.getTagsContainer().<TagVector>getTagItem(TagID.DEFAULT.FLOAT3).getValues().xyz(new Vector3f());

        PointLight pointLight = new PointLight();
        pointLight.setLightPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
        pointLight.setLightColor(tagColor.xyz(new Vector3f()));
        pointLight.setBrightness(brightness);
        pointLight.setOffset(offset);
        pointLight.on();
        sceneWorld.addLight(pointLight, null);

        return new Pair<>(pointLight, attachedTo);
    }

    @SuppressWarnings("all")
    private <T extends IJGemsObjectData, R extends Resource<?, ?>> void processMapObjects(Collection<MapObjectTemplate> templates, Map<String, APIWBenchDataManager.TemplatesTable<R>> resourceMap, BiConsumer<MapObjectTemplate, T> processor) {
        for (MapObjectTemplate template : templates) {
            final String name = template.getObjectNameId();
            final String group = template.getObjectGroup();
            if (!resourceMap.containsKey(group) || !resourceMap.get(group).getTemplateMap().containsKey(name)) {
                Log.get().error("Couldn't spawn " + name + " from group: " + group);
                continue;
            }
            T data = (T) resourceMap.get(group).getTemplateMap().get(name).getFabricGame().create();
            processor.accept(template, data);
        }
    }

    protected void onProcessing(Set<MapObjectTemplate> backgroundPropObjects, Set<MapObjectTemplate> propObjects, Set<MapObjectTemplate> markerObjects, Set<MapObjectTemplate> entityObjects, Set<MapObjectTemplate> pointLights, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        final Map<String, APIWBenchDataManager.TemplatesTable<ResourceProp>> resourcePropMap = JGemsAPI.APIEditorResources().getEditorResourcesManager().getResourcePropMap();
        final Map<String, APIWBenchDataManager.TemplatesTable<ResourceEntity>> resourceEntityMap = JGemsAPI.APIEditorResources().getEditorResourcesManager().getResourceEntityMap();
        final Map<String, APIWBenchDataManager.TemplatesTable<ResourceMarker>> resourceMarkerMap = JGemsAPI.APIEditorResources().getEditorResourcesManager().getResourceMarkerMap();

        Map<Integer, List<PointLight>> pointLightIdMap = new HashMap<>();
        for (MapObjectTemplate template : pointLights) {
            Pair<PointLight, Integer> pair = this.onProcessPointLight(template, physicsWorld, sceneWorld);
            if (pair != null) {
                if (pair.getSecond() >= 0) {
                    JGemsUtils.putObjectInMapOrUpdate(pointLightIdMap, pair.getSecond(), new ArrayList<PointLight>() {{
                        add(pair.getFirst());
                    }}, (ex, nw) -> {
                        ex.add(nw);
                        return ex;
                    }, pair.getFirst());
                    JGemsAPI.getAPIScripting().getGameWorldJS().onMapSpawnedPointLightEvent(pair.getFirst(), pair.getSecond());
                }
            }
        }

        this.processMapObjects(propObjects, resourcePropMap, (template, data) -> {
            SceneProp sceneProp = this.onProcessProp(template, (JGemsPropData) data, physicsWorld, sceneWorld, pointLightIdMap.getOrDefault(template.getId(), null));
            if (sceneProp != null) {
                JGemsAPI.getAPIScripting().getGameWorldJS().onMapSpawnedPropEvent(sceneProp, template.getId());
            }
        });
        this.processMapObjects(backgroundPropObjects, resourcePropMap, (template, data) -> {
            SceneProp prop = this.onProcessBackgroundProp(template, (JGemsPropData) data, sceneWorld, sceneWorld.getEnvironment().getSkyBox().getBackground());
            if (prop != null) {
                JGemsAPI.getAPIScripting().getGameWorldJS().getBackgroundJS().onMapSpawnedBackgroundPropEvent(prop, template.getId());
            }
        });
        this.processMapObjects(entityObjects, resourceEntityMap, (template, data) -> {
            WorldItem worldItem = this.onProcessEntity(template, (JGemsEntityData) data, physicsWorld, sceneWorld, pointLightIdMap.getOrDefault(template.getId(), null));
            if (worldItem != null) {
                JGemsAPI.getAPIScripting().getGameWorldJS().onMapSpawnedWorldItemEvent(worldItem, template.getId());
            }
        });
        this.processMapObjects(markerObjects, resourceMarkerMap, (template, data) -> this.onProcessMarker(template, (JGemsMarkerData) data, physicsWorld, sceneWorld));
    }

    protected void onSetupSkyBox(SunData sunData, SkyData skyData, ISkyBox skyBox, ISkyBackground background) {
        if (skyData != null) {
            final Map<String, Pair<String, JGemsPath>> skyBoxesSet = JGemsAPI.APIEditorResources().getEditorResourcesManager().getSkyBoxesMap();
            if (skyBoxesSet.containsKey(skyData.skyboxPath)) {
                ICubeMapProgram cubeMapProgram = this.getLocalResources().createCubeMapTexture(null, skyBoxesSet.get(skyData.skyboxPath).getSecond(), skyBoxesSet.get(skyData.skyboxPath).getFirst(), new CubeMapTexture.Properties(true));
                skyBox.setSky2DTexture(cubeMapProgram);
            } else {
                Log.get().error("Couldn't create cubeMap: " + skyData.skyboxPath);
            }
        }

        if (sunData != null) {
            skyBox.getSun().setLightPosition(sunData.position);
            skyBox.getSun().setLightColor(sunData.color);
            skyBox.getSun().setSunBrightness(sunData.brightness);
        }
    }

    protected void onSetupShadows(ShadowsData shadowsData, IShadowScene shadowScene) {
        if (shadowsData != null) {
            shadowScene.getSunLightShadow().setCascadeSplits(shadowsData.splits);
        }
    }

    protected void onSetupFog(FogData fogData, IFogScene fogScene) {
        if (fogData != null) {
            fogScene.setFogColor(fogData.color);
            fogScene.setFogDensity(fogData.density);
        }
    }

    @Override
    public final void preProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        this.preProcessing(this.getMapDataPack(), world, sceneWorld);
    }

    @Override
    public final void onProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        final Set<MapObjectTemplate> propObjects = this.getMapDataPack().getObjectsData().getPropObjects();
        final Set<MapObjectTemplate> markerObjects = this.getMapDataPack().getObjectsData().getMarkerObjects();
        final Set<MapObjectTemplate> entityObjects = this.getMapDataPack().getObjectsData().getEntityObjects();
        final Set<MapObjectTemplate> pointLights = this.getMapDataPack().getObjectsData().getPointLights();
        final Set<MapObjectTemplate> backgroundProps = this.getMapDataPack().getObjectsData().getBackgroundProps();
        this.onProcessing(backgroundProps, propObjects, markerObjects, entityObjects, pointLights, world, sceneWorld);
    }

    @Override
    public final void postProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        this.postProcessing(this.getMapDataPack(), world, sceneWorld);
    }

    @Override
    public final void onSetupSkyBox(ISkyBox skyBox, ISkyBackground background) {
        final SunData sunData = this.getMapDataPack().getSunData();
        final SkyData skyData = this.getMapDataPack().getSkyData();
        background.setViewScaling(skyData.backGroundScaling);
        this.onSetupSkyBox(sunData, skyData, skyBox, background);
    }

    @Override
    public final void onSetupFog(IFogScene fogScene) {
        final FogData fogData = this.getMapDataPack().getFogData();
        this.onSetupFog(fogData, fogScene);
    }

    @Override
    public final void onSetupShadows(IShadowScene shadowScene) {
        final ShadowsData shadowsData = this.getMapDataPack().getShadowsData();
        this.onSetupShadows(shadowsData, shadowScene);
    }

    @Override
    public final @NotNull String getMapName() {
        return this.getProjectData().getProjectName();
    }

    @Override
    public final @NotNull String getMapInformation() {
        return this.getProjectData().getInformation();
    }

    protected ProjectData getProjectData() {
        return this.projectData;
    }

    protected MapDataPack getMapDataPack() {
        return this.mapDataPack;
    }

    public JGemsPath getPathToJG3DFile() {
        return this.pathToJG3DFile;
    }

    public static class Default extends ExternalMapProcessor {
        protected Vector3f playerSpawnPoint;
        protected Vector3f playerSpawnRotation;

        public Default(JGemsPath pathToJG3DFile, boolean inJar) {
            super(pathToJG3DFile, inJar);
        }

        @Override
        protected @Nullable SceneProp onProcessBackgroundProp(MapObjectTemplate template, JGemsPropData propData, SceneWorld sceneWorld, ISkyBackground background) {
            MeshBuffer buffer = this.getLocalResources().createMeshBuffer(propData.getPathToModel(), false);
            SceneWorldProp sceneWorldProp = new SceneWorldProp(template.getObjectNameId(), sceneWorld, new PropRenderData(propData.getPropRenderData(), buffer));
            sceneWorldProp.getModel().getPose().setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
            sceneWorldProp.getModel().getPose().setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
            sceneWorldProp.getModel().getPose().setScaling(template.getScaling() == null ? new Vector3f(1.0f) : template.getScaling());
            background.addObject(sceneWorldProp);
            return sceneWorldProp;
        }

        @Override
        protected @Nullable SceneProp onProcessProp(MapObjectTemplate template, JGemsPropData propData, PhysicsWorld physicsWorld, SceneWorld sceneWorld, @Nullable List<PointLight> pointLightsToAttach) {
            MeshBuffer buffer = this.getLocalResources().createMeshBuffer(propData.getPathToModel(), false);
            SceneWorldProp sceneWorldProp = new SceneWorldProp(template.getObjectNameId(), sceneWorld, new PropRenderData(propData.getPropRenderData(), buffer));
            sceneWorldProp.getModel().getPose().setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
            sceneWorldProp.getModel().getPose().setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
            sceneWorldProp.getModel().getPose().setScaling(template.getScaling() == null ? new Vector3f(1.0f) : template.getScaling());
            sceneWorld.addObject(sceneWorldProp);

            if (pointLightsToAttach != null) {
                for (PointLight pointLight : pointLightsToAttach) {
                    sceneWorldProp.addLightAttachment(pointLight);
                }
            }

            return sceneWorldProp;
        }

        @Override
        protected @Nullable WorldItem onProcessEntity(MapObjectTemplate template, JGemsEntityData entityData, PhysicsWorld physicsWorld, SceneWorld sceneWorld, @Nullable List<PointLight> pointLightsToAttach) {
            final TagRadioBoolean tagPhysics = template.getTagsContainer().getTagItem(TagID.DEFAULT.PHYSICS_STATE);
            MeshBuffer buffer = this.getLocalResources().createMeshBuffer(entityData.getPathToModel(), false);

            JGemsBody jGemsBody = null;
            if (tagPhysics == null || tagPhysics.getValues()[0].isFlag()) {
                jGemsBody = new JGemsStaticBody(MeshCollider.getStatic(buffer), physicsWorld, new Vector3f(0.0f), template.getObjectNameId()).setCanBeDestroyed(false);
            } else {
                jGemsBody = new JGemsDynamicBody(MeshCollider.getDynamic(buffer), physicsWorld, new Vector3f(0.0f), template.getObjectNameId()).setCanBeDestroyed(false);
            }
            JGemsHelper.world().addWorldItem(jGemsBody, new EntityRenderData(entityData.getEntityRenderData(), buffer));
            jGemsBody.setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
            jGemsBody.setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
            jGemsBody.setScaling(template.getScaling() == null ? new Vector3f(0.0f) : template.getScaling());

            if (pointLightsToAttach != null) {
                for (PointLight pointLight : pointLightsToAttach) {
                    sceneWorld.addWorldItemLight(jGemsBody, pointLight);
                }
            }

            return jGemsBody;
        }

        @Override
        protected void onProcessMarker(MapObjectTemplate template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
            if (template.checkGroupName("generic", MapObjectsIdentifiers.MARKER + "water")) {
                Vector3f pos = template.getPosition();
                Vector3f scale = template.getScaling();
                Water water = new Water(new Zone(new Vector3f(pos), new Vector3f(scale).mul(2.0f)));
                JGemsHelper.world().addLiquid(water, JGemsResourceManager.globalRenderDataAssets.water);
            }

            if (template.checkGroupName("generic", MapObjectsIdentifiers.MARKER + "player_spawn")) {
                Matrix4f rotMatrix = new Matrix4f().rotateXYZ(template.getRotation().x, template.getRotation().y, template.getRotation().z);
                Vector3f forward = new Vector3f(0, 0, -1);
                rotMatrix.transformDirection(forward);
                Vector3f flatForward = new Vector3f(forward.x, 0, forward.z).normalize();
                float angleY = (float) Math.atan2(-flatForward.x, -flatForward.z);

                this.playerSpawnPoint = template.getPosition();
                this.playerSpawnRotation = new Vector3f(0.0f, angleY, 0.0f);
            }
        }

        @Override
        protected void preProcessing(MapDataPack mapDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {

        }

        @Override
        protected void postProcessing(MapDataPack mapDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        }

        @Override
        public @Nullable IGameMap.IPlayerConstructor getPlayerConstructor() {
            if (this.playerSpawnPoint == null || this.playerSpawnRotation == null) {
                return null;
            }
            return (world -> new Pair<>(this.createPlayer(world, this.playerSpawnPoint, this.playerSpawnRotation), JGemsResourceManager.globalRenderDataAssets.defaultPlayer));
        }

        protected IPlayer createPlayer(PhysicsWorld world, Vector3f pos, Vector3f rot) {
            return new JGemsKinematicPlayer(world, new Vector3f(pos), new Vector3f(rot));
        }
    }
}