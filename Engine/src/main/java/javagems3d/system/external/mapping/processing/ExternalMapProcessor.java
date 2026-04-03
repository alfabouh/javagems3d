package javagems3d.system.external.mapping.processing;

import api.application.workbench.manager.ApiResourceObjectsFolder;
import api.application.workbench.resources.APIResource;
import api.application.workbench.resources.ApiResourceEntity;
import api.application.workbench.resources.ApiResourceMarker;
import api.application.workbench.resources.ApiResourceProp;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import api.system.JGemsAPI;
import com.google.gson.reflect.TypeToken;
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
import javagems3d.system.external.gaming.JGemsGaming;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.data.MapObjectsDataPack;
import javagems3d.system.external.mapping.data.MapProjectData;
import javagems3d.system.external.mapping.data.items.FogData;
import javagems3d.system.external.mapping.data.items.ShadowsData;
import javagems3d.system.external.mapping.data.items.SkyData;
import javagems3d.system.external.mapping.data.items.SunData;
import javagems3d.system.external.mapping.data.templates.RowMapObjectData;
import javagems3d.system.external.mapping.processing.base.MapProcessor;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.entities.kinematic.player.JGemsKinematicPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.Water;
import javagems3d.system.external.mapping.tags.items.*;
import javagems3d.system.resources.assets.loading.samples.CubeMapsLoader;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class ExternalMapProcessor extends MapProcessor {
    private final JGemsPathSource pathToJG3DFile;
    private MapObjectsDataPack mapObjectsDataPack;
    private MapProjectData mapProjectData;

    public ExternalMapProcessor(JGemsPathSource pathToJG3DFile) {
        super();
        this.pathToJG3DFile = pathToJG3DFile;
        this.readMap(pathToJG3DFile);
    }

    protected void readMap(JGemsPathSource pathToJG3DFile) {
        final JSONFileManaging jsonFileManaging = TagsContainer.createJSONFileManaging();

        try {
            this.mapProjectData = this.loadJson(jsonFileManaging, pathToJG3DFile, new TypeToken<>() {
            });
            if (this.mapProjectData == null) {
                throw new JGemsIOException("Couldn't load map(no project data): " + pathToJG3DFile);
            }
            this.mapProjectData.setAbsolutePath(pathToJG3DFile.getPath().getAbsolutePathDirectory());
            this.mapProjectData.checkVersion();

            this.mapObjectsDataPack = this.loadJson(jsonFileManaging, new JGemsPathSource(this.mapProjectData.getPathToDataMapFile(), pathToJG3DFile.getSource()), new TypeToken<>() {
            });
            if (this.mapObjectsDataPack == null) {
                throw new JGemsIOException("Couldn't load map(no map data): " + pathToJG3DFile);
            }

            JGemsAPI.getAPIScriptingCore().initMap(JGemsGaming.getScriptsFolder(pathToJG3DFile.getPath().getAbsolutePathDirectory()));
        } catch (Exception e) {
            throw new JGemsIOException(e);
        }
    }

    //private void loadScripts(JGemsPathSource pathToJG3DFile, List<String> scriptPaths) {
    //    try {
    //        for (String path : scriptPaths) {
    //            JGemsAPI.executeScript(JGemsHelper.files().readTextFromFile(new JGemsPathSource(new JGemsPath(pathToJG3DFile.getPath().getAbsolutePathDirectory(), "scripts", path), pathToJG3DFile.getSource())));
    //        }
    //    } catch (JGemsIOException e) {
    //        Log.get().exception(e);
    //    }
    //}

    private <T> T loadJson(JSONFileManaging jsonFileManaging, JGemsPathSource path, TypeToken<T> typeToken) {
        try (InputStream stream = JGems3D.getInputStream(path)) {
            return jsonFileManaging.readFromInputStream(stream, typeToken, null);
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    @Override
    public void init() {
    }

    protected abstract @Nullable SceneProp onProcessBackgroundProp(RowMapObjectData template, JGemsPropData propData, SceneWorld sceneWorld, ISkyBackground background);
    protected abstract @Nullable SceneProp onProcessProp(RowMapObjectData template, JGemsPropData propData, PhysicsWorld physicsWorld, SceneWorld sceneWorld, @Nullable List<PointLight> pointLightsToAttach);
    protected abstract @Nullable WorldItem onProcessEntity(RowMapObjectData template, JGemsEntityData entityData, PhysicsWorld physicsWorld, SceneWorld sceneWorld, @Nullable List<PointLight> pointLightsToAttach);
    protected abstract void onProcessMarker(RowMapObjectData template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld);

    protected abstract void preProcessing(MapObjectsDataPack mapObjectsDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld);
    protected abstract void postProcessing(MapObjectsDataPack mapObjectsDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld);

    protected @Nullable Pair<PointLight, Integer> onProcessPointLight(RowMapObjectData template, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        final Vector4f tagColor = Objects.requireNonNull(template.getTagsContainer().<TagColor>getTagItem(TagID.DEFAULT.COLOR3)).getColorVector();
        final float brightness = Objects.requireNonNull(template.getTagsContainer().<TagFloat>getTagItem(TagID.DEFAULT.BRIGHTNESS)).getValue();
        final int attachedTo = Objects.requireNonNull(template.getTagsContainer().<TagObjectsList>getTagItem(TagID.DEFAULT.OBJECT_LIST)).getValue();
        final Vector3f offset = Objects.requireNonNull(template.getTagsContainer().<TagVector>getTagItem(TagID.DEFAULT.FLOAT3)).getValues().xyz(new Vector3f());

        PointLight pointLight = new PointLight();
        pointLight.setLightPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
        pointLight.setLightColor(tagColor.xyz(new Vector3f()));
        pointLight.setBrightness(brightness);
        pointLight.setOffset(offset);
        pointLight.on();
        sceneWorld.addLight(pointLight, null);

        return new Pair<>(pointLight, attachedTo);
    }

    //Map<String, APIWBenchDataManager.TemplatesTable<R>>
    @SuppressWarnings("all")
    private <A, B, T extends APIResource<A, B>> void processMapObjects(Collection<RowMapObjectData> templates, ApiResourceObjectsFolder<A, B, T> resourceMap, BiConsumer<RowMapObjectData, B> processor) {
        for (RowMapObjectData template : templates) {
            @Nullable T t = resourceMap.find(template.getObjectPath(), template.getObjectNameId());
            if (t == null) {
                Log.get().error("Couldn't spawn " + template.getObjectPath() + "/" + template.getObjectNameId());
                continue;
            }
            processor.accept(template, t.getFabricGame().create());
        }
    }

    protected void onProcessing(Set<RowMapObjectData> backgroundPropObjects, Set<RowMapObjectData> propObjects, Set<RowMapObjectData> markerObjects, Set<RowMapObjectData> entityObjects, Set<RowMapObjectData> pointLights, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        final ApiResourceObjectsFolder<WBenchObjectData, JGemsPropData, ApiResourceProp> resourcePropMap = JGemsAPI.APIEditorResources().getEditorResourcesManager().getProps();
        final ApiResourceObjectsFolder<WBenchObjectData, JGemsEntityData, ApiResourceEntity> resourceEntityMap = JGemsAPI.APIEditorResources().getEditorResourcesManager().getEntities();
        final ApiResourceObjectsFolder<WBenchMarkerData, JGemsMarkerData, ApiResourceMarker> resourceMarkerMap = JGemsAPI.APIEditorResources().getEditorResourcesManager().getMarkers();

        Map<Integer, List<PointLight>> pointLightIdMap = new HashMap<>();
        for (RowMapObjectData template : pointLights) {
            Pair<PointLight, Integer> pair = this.onProcessPointLight(template, physicsWorld, sceneWorld);
            if (pair != null) {
                if (pair.second() >= 0) {
                    JGemsUtils.putObjectInMapOrUpdate(pointLightIdMap, pair.second(), new ArrayList<PointLight>() {{
                        add(pair.first());
                    }}, (ex, nw) -> {
                        ex.add(nw);
                        return ex;
                    }, pair.first());
                    //JGemsAPI.getAPIScripting().getGameWorldJS().onMapSpawnedPointLightEvent(pair.first(), pair.second());
                }
            }
        }
        this.processMapObjects(propObjects, resourcePropMap, (template, data) -> {
            SceneProp sceneProp = this.onProcessProp(template, data, physicsWorld, sceneWorld, pointLightIdMap.getOrDefault(template.getId(), null));
            if (sceneProp != null) {
                //JGemsAPI.getAPIScripting().getGameWorldJS().onMapSpawnedPropEvent(sceneProp, template.getId());
            }
        });
        this.processMapObjects(backgroundPropObjects, resourcePropMap, (template, data) -> {
            SceneProp prop = this.onProcessBackgroundProp(template, data, sceneWorld, sceneWorld.getEnvironment().getSkyBox().getBackground());
            if (prop != null) {
                //JGemsAPI.getAPIScripting().getGameWorldJS().getBackgroundJS().onMapSpawnedBackgroundPropEvent(prop, template.getId());
            }
        });
        this.processMapObjects(entityObjects, resourceEntityMap, (template, data) -> {
            WorldItem worldItem = this.onProcessEntity(template, data, physicsWorld, sceneWorld, pointLightIdMap.getOrDefault(template.getId(), null));
            if (worldItem != null) {
               // JGemsAPI.getAPIScripting().getGameWorldJS().onMapSpawnedWorldItemEvent(worldItem, template.getId());
            }
        });
        this.processMapObjects(markerObjects, resourceMarkerMap, (template, data) -> this.onProcessMarker(template, data, physicsWorld, sceneWorld));
    }

    protected void onSetupSkyBox(SunData sunData, SkyData skyData, ISkyBox skyBox, ISkyBackground background) {
        if (skyData != null) {
           final Map<String, ICubeMapProgram.CMTextures> skyBoxesSet = JGemsAPI.APIEditorResources().getEditorResourcesManager().getSkyBoxesMap();
           if (skyBoxesSet.containsKey(skyData.getNameId())) {
               ICubeMapProgram cubeMapProgram = this.getLocalResources().createCubeMapTexture(null, new CubeMapsLoader.CubeMapTexturesContainer(skyBoxesSet.get(skyData.getNameId())), new CubeMapTexture.Properties(true));
               skyBox.setSky2DTexture(cubeMapProgram);
           } else {
               Log.get().error("Couldn't create cubeMap: " + skyData.getNameId());
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
        final Set<RowMapObjectData> propObjects = this.getMapDataPack().getObjectsData().getPropObjects();
        final Set<RowMapObjectData> markerObjects = this.getMapDataPack().getObjectsData().getMarkerObjects();
        final Set<RowMapObjectData> entityObjects = this.getMapDataPack().getObjectsData().getEntityObjects();
        final Set<RowMapObjectData> pointLights = this.getMapDataPack().getObjectsData().getPointLights();
        final Set<RowMapObjectData> backgroundProps = this.getMapDataPack().getObjectsData().getBackgroundProps();
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
        return this.getProjectData().getMapName();
    }

    @Override
    public final @NotNull String getMapInformation() {
        return this.getProjectData().getMapDescription();
    }

    protected MapProjectData getProjectData() {
        return this.mapProjectData;
    }

    protected MapObjectsDataPack getMapDataPack() {
        return this.mapObjectsDataPack;
    }

    public JGemsPathSource getPathToJG3DFile() {
        return this.pathToJG3DFile;
    }

    public static class Default extends ExternalMapProcessor {
        protected Vector3f playerSpawnPoint;
        protected Vector3f playerSpawnRotation;

        public Default(JGemsPath pathToJG3DFile) {
            super(new JGemsPathSource(pathToJG3DFile, ISource.Source.OUTSIDE_JAR));
        }

        @Override
        protected @Nullable SceneProp onProcessBackgroundProp(RowMapObjectData template, JGemsPropData propData, SceneWorld sceneWorld, ISkyBackground background) {
            MeshBuffer buffer = this.getLocalResources().createMeshBuffer(propData.pathToModel(), false);
            final PropRenderData propRenderData = new PropRenderData(propData.propRenderData(), buffer);
            propRenderData.getObjectRenderAttributes().setRenderProperties(template.getRenderProperties() != null ? template.getRenderProperties() : propRenderData.getObjectRenderAttributes().getProperties());
            SceneWorldProp sceneWorldProp = new SceneWorldProp(template.getObjectNameId(), sceneWorld, propRenderData);
            sceneWorldProp.getModel().getPose().setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
            sceneWorldProp.getModel().getPose().setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
            sceneWorldProp.getModel().getPose().setScaling(template.getScaling() == null ? new Vector3f(1.0f) : template.getScaling());
            background.addObject(sceneWorldProp);
            return sceneWorldProp;
        }

        @Override
        protected @Nullable SceneProp onProcessProp(RowMapObjectData template, JGemsPropData propData, PhysicsWorld physicsWorld, SceneWorld sceneWorld, @Nullable List<PointLight> pointLightsToAttach) {
            MeshBuffer buffer = this.getLocalResources().createMeshBuffer(propData.pathToModel(), false);
            final PropRenderData propRenderData = new PropRenderData(propData.propRenderData(), buffer);
            propRenderData.getObjectRenderAttributes().setRenderProperties(template.getRenderProperties() != null ? template.getRenderProperties() : propRenderData.getObjectRenderAttributes().getProperties());
            SceneWorldProp sceneWorldProp = new SceneWorldProp(template.getObjectNameId(), sceneWorld, propRenderData);
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
        protected @Nullable WorldItem onProcessEntity(RowMapObjectData template, JGemsEntityData entityData, PhysicsWorld physicsWorld, SceneWorld sceneWorld, @Nullable List<PointLight> pointLightsToAttach) {
            final TagRadioBoolean tagPhysics = template.getTagsContainer().getTagItem(TagID.DEFAULT.PHYSICS_STATE);
            MeshBuffer buffer = this.getLocalResources().createMeshBuffer(entityData.pathToModel(), false);
            JGemsBody jGemsBody = null;
            if (tagPhysics == null || tagPhysics.getValues()[0].isFlag()) {
                jGemsBody = new JGemsStaticBody(MeshCollider.getStatic(buffer), physicsWorld, new Vector3f(0.0f), template.getObjectNameId()).setCanBeDestroyed(false);
            } else {
                jGemsBody = new JGemsDynamicBody(MeshCollider.getDynamic(buffer), physicsWorld, new Vector3f(0.0f), template.getObjectNameId()).setCanBeDestroyed(false);
            }
            final EntityRenderData entityRenderData = new EntityRenderData(entityData.entityRenderData(), buffer);
            entityRenderData.getObjectRenderAttributes().setRenderProperties(template.getRenderProperties() != null ? template.getRenderProperties() : entityRenderData.getObjectRenderAttributes().getProperties());
            JGemsHelper.world().addWorldItem(jGemsBody, entityRenderData);
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
        protected void onProcessMarker(RowMapObjectData template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
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
        protected void preProcessing(MapObjectsDataPack mapObjectsDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {

        }

        @Override
        protected void postProcessing(MapObjectsDataPack mapObjectsDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
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