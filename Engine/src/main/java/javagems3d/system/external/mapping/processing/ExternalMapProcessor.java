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
import api.events.EventBus;
import api.events.EventLauncher;
import api.scripting.coding.env.internal.map.events.mapping.*;
import api.scripting.coding.env.internal.map.events.mapping.data.JSEntityData;
import api.scripting.coding.env.internal.map.events.mapping.data.JSPropData;
import api.scripting.coding.env.internal.util.mapping.player.JSSpawnPlayerTranslateData;
import api.scripting.coding.env.internal.util.mapping.row.JSRowMapObjectData;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;
import api.system.JGemsAPI;
import api.system.scripting.JavaToJsAPI;
import com.google.gson.reflect.TypeToken;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.shadows.scene.JGemsShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.gaming.JGemsGaming;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.data.MapObjectsDataPack;
import javagems3d.system.external.mapping.data.MapProjectData;
import javagems3d.system.external.mapping.data.items.*;
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
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
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
import java.util.stream.Collectors;

public abstract class ExternalMapProcessor extends MapProcessor {
    private final JGemsPathSource pathToJG3DFile;
    private MapObjectsDataPack mapObjectsDataPack;
    private MapProjectData mapProjectData;
    private final Set<IGameMap.SpawnPlayerData> spawnForPlayersData;

    public ExternalMapProcessor(JGemsPathSource pathToJG3DFile) {
        super();
        this.pathToJG3DFile = pathToJG3DFile;
        this.spawnForPlayersData = new HashSet<>();
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
        final Vector4f tagColor = Objects.requireNonNull(template.getTagsContainer().<TagColor>getTagUnSafeItem(TagID.DEFAULT.COLOR3)).getColorVector();
        final float brightness = Objects.requireNonNull(template.getTagsContainer().<TagFloat>getTagUnSafeItem(TagID.DEFAULT.BRIGHTNESS)).getValue();
        final int attachedTo = Objects.requireNonNull(template.getTagsContainer().<TagObjectsList>getTagUnSafeItem(TagID.DEFAULT.OBJECT_LIST)).getValue();
        final Vector3f offset = Objects.requireNonNull(template.getTagsContainer().<TagVector>getTagUnSafeItem(TagID.DEFAULT.FLOAT3)).getValues().xyz(new Vector3f());
        final boolean shadows = template.getTagsContainer().hasTag(TagID.DEFAULT.SHADOW_MAP) && Objects.requireNonNull(template.getTagsContainer().<TagCheckBoolean>getTagUnSafeItem(TagID.DEFAULT.SHADOW_MAP)).isFlag();

        PointLight pointLight = new PointLight();
        pointLight.setLightPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
        pointLight.setLightColor(tagColor.xyz(new Vector3f()));
        pointLight.setBrightness(brightness);
        pointLight.setOffset(offset);
        pointLight.setEnableShadowMap(shadows);
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
            EventBus.MapPointLightConvertEvent event = new EventBus.MapPointLightConvertEvent(sceneWorld, physicsWorld, template);
            EventLauncher.pushEvent(event, new Pair<>(new JSMapPointLightConvertEvent(new JSSceneWorld(sceneWorld), new JSPhysicsWorld(physicsWorld), new JSRowMapObjectData(template)), JavaToJsAPI.Target.Map));
            if (event.isCancelled()) {
                continue;
            }

            Pair<PointLight, Integer> pair = event.getResult() != null ? event.getResult() : this.onProcessPointLight(template, physicsWorld, sceneWorld);
            if (pair != null) {
                if (pair.second() >= 0) {
                    JGemsHelper.Files.putObjectInMapOrUpdate(pointLightIdMap, pair.second(), new ArrayList<PointLight>() {{
                        add(pair.first());
                    }}, (ex, nw) -> {
                        ex.add(nw);
                        return ex;
                    }, pair.first());
                }
            }
        }
        this.processMapObjects(backgroundPropObjects, resourcePropMap, (template, data) -> {
            EventBus.MapPropConvertEvent event = new EventBus.MapPropConvertEvent(true, sceneWorld, physicsWorld, template, data);
            EventLauncher.pushEvent(event, new Pair<>(new JSMapPropConvertEvent(true, new JSSceneWorld(sceneWorld), new JSPhysicsWorld(physicsWorld), new JSRowMapObjectData(template), new JSPropData(data)), JavaToJsAPI.Target.Map));
            if (event.isCancelled()) {
                return;
            }

            SceneProp sceneProp = event.getResult() != null ? event.getResult() : this.onProcessBackgroundProp(template, data, sceneWorld, sceneWorld.getEnvironment().getSkyBox().getBackground());
        });
        this.processMapObjects(propObjects, resourcePropMap, (template, data) -> {
            EventBus.MapPropConvertEvent event = new EventBus.MapPropConvertEvent(false, sceneWorld, physicsWorld, template, data);
            EventLauncher.pushEvent(event, new Pair<>(new JSMapPropConvertEvent(false, new JSSceneWorld(sceneWorld), new JSPhysicsWorld(physicsWorld), new JSRowMapObjectData(template), new JSPropData(data)), JavaToJsAPI.Target.Map));
            if (event.isCancelled()) {
                return;
            }

            SceneProp sceneProp = event.getResult() != null ? event.getResult() : this.onProcessProp(template, data, physicsWorld, sceneWorld, pointLightIdMap.getOrDefault(template.getId(), null));
        });
        this.processMapObjects(entityObjects, resourceEntityMap, (template, data) -> {
            EventBus.MapEntityConvertEvent event = new EventBus.MapEntityConvertEvent(sceneWorld, physicsWorld, template, data);
            EventLauncher.pushEvent(event, new Pair<>(new JSMapEntityConvertEvent(new JSSceneWorld(sceneWorld), new JSPhysicsWorld(physicsWorld), new JSRowMapObjectData(template), new JSEntityData(data)), JavaToJsAPI.Target.Map));
            if (event.isCancelled()) {
                return;
            }

            WorldItem worldItem = event.getResult() != null ? event.getResult() : this.onProcessEntity(template, data, physicsWorld, sceneWorld, pointLightIdMap.getOrDefault(template.getId(), null));
        });
        this.processMapObjects(markerObjects, resourceMarkerMap, (template, data) -> {
            this.processDefaultMarkers(template);
            EventBus.MapMarkerConvertEvent event = new EventBus.MapMarkerConvertEvent(sceneWorld, physicsWorld, template, data);
            EventLauncher.pushEvent(event, new Pair<>(new JSMapMarkerConvertEvent(new JSSceneWorld(sceneWorld), new JSPhysicsWorld(physicsWorld), new JSRowMapObjectData(template)), JavaToJsAPI.Target.Map));
            if (event.isCancelled()) {
                return;
            }

            this.onProcessMarker(template, data, physicsWorld, sceneWorld);
        });
        this.processMapObjects(markerObjects, resourceMarkerMap, (template, data) -> this.onProcessMarker(template, data, physicsWorld, sceneWorld));
    }

    protected void processDefaultMarkers(RowMapObjectData template) {
        if (template.checkGroupName("generic_marker", MapObjectsIdentifiers.MARKER + "water")) {
            Vector3f pos = template.getPosition();
            Vector3f scale = template.getScaling();
            Water water = new Water(new Zone(new Vector3f(pos), new Vector3f(scale).mul(2.0f)));
            JGemsHelper.world().addLiquid(water, JGemsResourceManager.globalRenderDataAssets.water);
            Log.get().debug("Processed marker: water");
        }

        if (template.checkGroupName("generic_marker", MapObjectsIdentifiers.MARKER + "player_spawn")) {
            Matrix4f rotMatrix = new Matrix4f().rotateXYZ(template.getRotation().x, template.getRotation().y, template.getRotation().z);
            Vector3f forward = new Vector3f(0, 0, -1);
            rotMatrix.transformDirection(forward);
            Vector3f flatForward = new Vector3f(forward.x, 0, forward.z).normalize();
            float angleY = (float) Math.atan2(-flatForward.x, -flatForward.z);

            this.spawnForPlayersData.add(new IGameMap.SpawnPlayerData(template.getPosition(), new Vector3f(0.0f, angleY, 0.0f)));
            Log.get().debug("Processed marker: player_spawn");
        }
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

    protected void onSetupLighting(LightingData lightingData, ILightScene lightScene) {
        if (lightingData != null) {
            lightScene.setHdrGamma(lightingData.gamma);
            lightScene.setHdrExposure(lightingData.exposure);
            lightScene.setBloomEnabled(lightingData.bloomEffect);
            lightScene.setSsaoRange(lightingData.ssaoRange);
            lightScene.setSsaoBias(lightingData.ssaoBias);
            lightScene.setSsaoRadius(lightingData.ssaoRadius);
        }
    }

    protected void onSetupShadows(ShadowsData shadowsData, IShadowScene shadowScene) {
        if (shadowsData != null) {
            shadowScene.getSunLightShadow().setCascadeSplits(shadowsData.splits);
            shadowScene.getSunLightShadow().setEnabled(shadowsData.sunShadows);
            ((JGemsShadowScene) shadowScene).setSunShadowMapsBasicResolution(shadowsData.sunShadowRes);
            ((JGemsShadowScene) shadowScene).setPointLightShadowMapsBasicResolution(shadowsData.pointLightShadowRes);
            //shadowScene.recreateResources((JGemsOpenGLRenderer) JGems3D.get().getSceneRenderer());
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
        EventBus.MapProcessingEvent pre = new EventBus.MapProcessingEvent(world, sceneWorld, this.getMapDataPack(), EventBus.Run.PRE);
        EventLauncher.pushEvent(pre, new Pair<>(new JSMapProcessingEvent(new JSPhysicsWorld(world), new JSSceneWorld(sceneWorld), EventBus.Run.PRE), JavaToJsAPI.Target.Map));
        if (pre.isCancelled()) {
            return;
        }

        final Set<RowMapObjectData> propObjects = this.getMapDataPack().getObjectsData().getPropObjects();
        final Set<RowMapObjectData> markerObjects = this.getMapDataPack().getObjectsData().getMarkerObjects();
        final Set<RowMapObjectData> entityObjects = this.getMapDataPack().getObjectsData().getEntityObjects();
        final Set<RowMapObjectData> pointLights = this.getMapDataPack().getObjectsData().getPointLights();
        final Set<RowMapObjectData> backgroundProps = this.getMapDataPack().getObjectsData().getBackgroundProps();

        this.onProcessing(backgroundProps, propObjects, markerObjects, entityObjects, pointLights, world, sceneWorld);

        EventLauncher.pushEvent(new EventBus.MapProcessingEvent(world, sceneWorld, this.getMapDataPack(), EventBus.Run.POST), new Pair<>(new JSMapProcessingEvent(new JSPhysicsWorld(world), new JSSceneWorld(sceneWorld), EventBus.Run.POST), JavaToJsAPI.Target.Map));
    }

    @Override
    public final void postProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        this.postProcessing(this.getMapDataPack(), world, sceneWorld);
    }

    @Override
    public final void onSetupSkyBox(ISkyBox skyBox, ISkyBackground background, IEnvironment environment) {
        final SunData sunData = this.getMapDataPack().getSunData();
        final SkyData skyData = this.getMapDataPack().getSkyData();

        EventBus.MapSkySetupEvent event = new EventBus.MapSkySetupEvent((SceneWorld) background.getWorld(), skyBox, background, sunData, skyData);
        EventLauncher.pushEvent(event, new Pair<>(new JSMapSkySetupEvent(new JSSceneWorld((SceneWorld) background.getWorld()), new JSEnvironment((JGemsEnvironment) environment)), JavaToJsAPI.Target.Map));
        if (event.isCancelled()) {
            return;
        }

        background.setViewScaling(skyData.backGroundScaling);
        this.onSetupSkyBox(sunData, skyData, skyBox, background);
    }

    @Override
    public final void onSetupFog(IFogScene fogScene, IEnvironment environment) {
        final FogData fogData = this.getMapDataPack().getFogData();

        EventBus.MapFogSetupEvent event = new EventBus.MapFogSetupEvent(fogScene, fogData);
        EventLauncher.pushEvent(event, new Pair<>(new JSMapFogSetupEvent(new JSSceneWorld((SceneWorld) environment.getWorld()), new JSEnvironment((JGemsEnvironment) environment)), JavaToJsAPI.Target.Map));
        if (event.isCancelled()) {
            return;
        }

        this.onSetupFog(fogData, fogScene);
    }

    @Override
    public final void onSetupShadows(IShadowScene shadowScene, IEnvironment environment) {
        final ShadowsData shadowsData = this.getMapDataPack().getShadowsData();

        EventBus.MapShadowsSetupEvent event = new EventBus.MapShadowsSetupEvent(shadowScene, shadowsData);
        EventLauncher.pushEvent(event, new Pair<>(new JSMapShadowsSetupEvent(new JSSceneWorld((SceneWorld) environment.getWorld()), new JSEnvironment((JGemsEnvironment) environment)), JavaToJsAPI.Target.Map));
        if (event.isCancelled()) {
            return;
        }

        this.onSetupShadows(shadowsData, shadowScene);
    }

    @Override
    public final void onSetupLighting(ILightScene lightScene, IEnvironment environment) {
        final LightingData lightingData = this.getMapDataPack().getLightingData();

        EventBus.MapLightingSetupEvent event = new EventBus.MapLightingSetupEvent(lightScene, lightingData);
        EventLauncher.pushEvent(event, new Pair<>(new JSMapLightingSetupEvent(new JSSceneWorld((SceneWorld) environment.getWorld()), new JSEnvironment((JGemsEnvironment) environment)), JavaToJsAPI.Target.Map));
        if (event.isCancelled()) {
            return;
        }

        this.onSetupLighting(lightingData, lightScene);
    }

    @Override
    public @Nullable Collection<IGameMap.SpawnPlayerData> getSpawnPlayersSet() {
        return this.spawnForPlayersData;
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
        private IGameMap.IPlayerConstructor playerConstructor;

        public Default(JGemsPath pathToJG3DFile, @Nullable IGameMap.IPlayerConstructor playerConstructor) {
            super(new JGemsPathSource(pathToJG3DFile, ISource.Source.OUTSIDE_JAR));
            this.playerConstructor = playerConstructor;
        }

        @Override
        protected @Nullable SceneProp onProcessBackgroundProp(RowMapObjectData template, JGemsPropData propData, SceneWorld sceneWorld, ISkyBackground background) {
            final TagRadioBoolean tagDirectIndirect = template.getTagsContainer().getTagItem(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, TagRadioBoolean.class);
            MeshStructure3D<?> meshStructure3D = null;
            PropRenderData propRenderData = null;
            if (tagDirectIndirect == null || tagDirectIndirect.getInfoMap().get("Indirect") == null || !tagDirectIndirect.getInfoMap().get("Indirect").isFlag()) {
                meshStructure3D = this.getLocalResources().createMeshGroup(propData.pathToModel(), false);
                propRenderData = new PropRenderData(propData.propRenderData(), new RenderAttributes(RenderTable.getDirect(), propData.propRenderData().getObjectRenderAttributes().getProperties()), meshStructure3D);
            } else {
                meshStructure3D = this.getLocalResources().createMeshBuffer(propData.pathToModel(), false);
                propRenderData = new PropRenderData(propData.propRenderData(), new RenderAttributes(RenderTable.getIndirect(), propData.propRenderData().getObjectRenderAttributes().getProperties()), meshStructure3D);
            }

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
            final TagRadioBoolean tagDirectIndirect = template.getTagsContainer().getTagItem(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, TagRadioBoolean.class);
            MeshStructure3D<?> meshStructure3D = null;
            PropRenderData propRenderData = null;
            if (tagDirectIndirect == null || tagDirectIndirect.getInfoMap().get("Indirect") == null || !tagDirectIndirect.getInfoMap().get("Indirect").isFlag()) {
                meshStructure3D = this.getLocalResources().createMeshGroup(propData.pathToModel(), false);
                propRenderData = new PropRenderData(propData.propRenderData(), new RenderAttributes(RenderTable.getDirect(), propData.propRenderData().getObjectRenderAttributes().getProperties()), meshStructure3D);
            } else {
                meshStructure3D = this.getLocalResources().createMeshBuffer(propData.pathToModel(), false);
                propRenderData = new PropRenderData(propData.propRenderData(), new RenderAttributes(RenderTable.getIndirect(), propData.propRenderData().getObjectRenderAttributes().getProperties()), meshStructure3D);
            }

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
            final TagRadioBoolean tagStaticBody = template.getTagsContainer().getTagItem(TagID.DEFAULT.PHYSICS_STATE, TagRadioBoolean.class);
            final TagRadioBoolean tagDirectIndirect = template.getTagsContainer().getTagItem(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, TagRadioBoolean.class);
            MeshStructure3D<?> meshStructure3D = null;
            EntityRenderData entityRenderData = null;
            if (tagDirectIndirect == null || tagDirectIndirect.getInfoMap().get("Indirect") == null || !tagDirectIndirect.getInfoMap().get("Indirect").isFlag()) {
                meshStructure3D = this.getLocalResources().createMeshGroup(entityData.pathToModel(), false);
                entityRenderData = new EntityRenderData(entityData.entityRenderData(), new RenderAttributes(RenderTable.getDirect(), entityData.entityRenderData().getObjectRenderAttributes().getProperties()), meshStructure3D);
            } else {
                meshStructure3D = this.getLocalResources().createMeshBuffer(entityData.pathToModel(), false);
                entityRenderData = new EntityRenderData(entityData.entityRenderData(), new RenderAttributes(RenderTable.getIndirect(), entityData.entityRenderData().getObjectRenderAttributes().getProperties()), meshStructure3D);
            }

            entityRenderData.getObjectRenderAttributes().setRenderProperties(template.getRenderProperties() != null ? template.getRenderProperties() : entityRenderData.getObjectRenderAttributes().getProperties());
            JGemsBody jGemsBody = null;
            if (tagStaticBody == null || (tagStaticBody.getInfoMap().get("Static") != null && tagStaticBody.getInfoMap().get("Static").isFlag())) {
                jGemsBody = new JGemsStaticBody(MeshCollider.getStatic(meshStructure3D), physicsWorld, new Vector3f(0.0f), template.getObjectNameId()).setCanBeDeleted(false);
            } else {
                jGemsBody = new JGemsDynamicBody(MeshCollider.getDynamic(meshStructure3D), physicsWorld, new Vector3f(0.0f), template.getObjectNameId()).setCanBeDeleted(false);
            }
            JGemsHelper.world().addWorldItem(jGemsBody, entityRenderData);
            jGemsBody.setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
            jGemsBody.setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
            jGemsBody.setScaling(template.getScaling() == null ? new Vector3f(1.0f) : template.getScaling());

            if (pointLightsToAttach != null) {
                for (PointLight pointLight : pointLightsToAttach) {
                    sceneWorld.addWorldItemLight(jGemsBody, pointLight);
                }
            }

            return jGemsBody;
        }

        @Override
        protected void onProcessMarker(RowMapObjectData template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        }

        @Override
        protected void preProcessing(MapObjectsDataPack mapObjectsDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {

        }

        @Override
        protected void postProcessing(MapObjectsDataPack mapObjectsDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        }

        public static IGameMap.IPlayerConstructor getDefaultPlayerConstructor() {
            return ((world, dataSet) -> {
                if (dataSet.isEmpty()) {
                    return null;
                }
                IGameMap.SpawnPlayerData randData = dataSet.stream().findAny().orElse(null);
                return new Pair<>(new JGemsKinematicPlayer(world, new Vector3f(randData.spawnPos()), new Vector3f(randData.spawnRot())), JGemsResourceManager.globalRenderDataAssets.defaultPlayer);
            });
        }

        @Override
        public IGameMap.IPlayerConstructor getPlayerConstructor(PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
            if (this.playerConstructor == null) {
                this.playerConstructor = ExternalMapProcessor.Default.getDefaultPlayerConstructor();
            }
            EventBus.PlayerConstructOnMapEvent event = new EventBus.PlayerConstructOnMapEvent(physicsWorld, this.getSpawnPlayersSet());
            JSPlayerConstructOnMapEvent playerConstructOnMapEventJS = new JSPlayerConstructOnMapEvent(new JSPhysicsWorld(physicsWorld), this.getSpawnPlayersSet() == null ? new ArrayList<>() : this.getSpawnPlayersSet().stream().map(e -> new JSSpawnPlayerTranslateData(new JSVector3f(e.spawnPos()), new JSVector3f(e.spawnRot()))).collect(Collectors.toSet()));
            EventLauncher.pushEvent(event, new Pair<>(playerConstructOnMapEventJS, JavaToJsAPI.Target.Map));
            try {
                IPlayer potentialPlayer = event.player == null ? playerConstructOnMapEventJS.getPlayer().getJavaPlayer() : event.player;
                EntityRenderData potentialRenderData = event.renderData == null ? playerConstructOnMapEventJS.getRenderData().getJavaEntityRenderData() : event.renderData;
                {
                    if (potentialPlayer == null && event.isCancelled()) {
                        return null;
                    }
                }
                {
                    if (potentialPlayer != null) {
                        this.playerConstructor = (world, spawnPlayerData) -> new Pair<>(potentialPlayer, potentialRenderData);
                    }
                }
            } catch (Exception e) {
                Log.get().warn("Couldn't create any player. Default returned");
            }
            return this.playerConstructor;
        }
    }
}