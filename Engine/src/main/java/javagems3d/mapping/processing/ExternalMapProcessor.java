package javagems3d.mapping.processing;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.ResourceMarker;
import api.application.workbench.resources.ResourceProp;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.system.JGemsAPI;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.JGemsWorldHelper;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.data.MapDataPack;
import javagems3d.mapping.data.ProjectData;
import javagems3d.mapping.data.items.FogData;
import javagems3d.mapping.data.items.SkyData;
import javagems3d.mapping.data.items.SunData;
import javagems3d.mapping.data.templates.MapObjectTemplate;
import javagems3d.mapping.processing.base.MapProcessor;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.items.TagColor;
import javagems3d.mapping.tags.items.TagFloat;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.resources.assets.loading.models.ModelLoaderFlags;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

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
        final JSONFileManaging jsonFileManaging = JSONFileManaging.create(new Pair<>(TagsContainer.class, TagsContainer.TAGS_CONTAINER_SERIALIZATION_RULE));

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
        } catch (Exception e) {
            throw new JGemsIOException(e);
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

    protected abstract void onProcessProp(MapObjectTemplate template, JGemsPropData propData, PhysicsWorld physicsWorld, SceneWorld sceneWorld);
    protected abstract void onProcessEntity(MapObjectTemplate template, JGemsEntityData entityData, PhysicsWorld physicsWorld, SceneWorld sceneWorld);
    protected abstract void onProcessMarker(MapObjectTemplate template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld);
    protected abstract void onProcessPointLight(MapObjectTemplate template, PhysicsWorld physicsWorld, SceneWorld sceneWorld);

    protected abstract void preProcessing(MapDataPack mapDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld);
    protected abstract void postProcessing(MapDataPack mapDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld);

    protected void onProcessing(Set<MapObjectTemplate> propObjects, Set<MapObjectTemplate> markerObjects, Set<MapObjectTemplate> entityObjects, Set<MapObjectTemplate> pointLights, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        final Map<String, APIWBenchDataManager.TemplatesTable<ResourceProp>> resourcePropMap = JGemsAPI.APIAppEditorResources().getEditorResourcesManager().getResourcePropMap();
        final Map<String, APIWBenchDataManager.TemplatesTable<ResourceEntity>> resourceEntityMap = JGemsAPI.APIAppEditorResources().getEditorResourcesManager().getResourceEntityMap();
        final Map<String, APIWBenchDataManager.TemplatesTable<ResourceMarker>> resourceMarkerMap = JGemsAPI.APIAppEditorResources().getEditorResourcesManager().getResourceMarkerMap();

        for (MapObjectTemplate template : propObjects) {
            final String name = template.getObjectId();
            final String group = template.getObjectGroup();
            final String nameNormalised = name.substring(MapObjectsIdentifiers.PROP.length());

            if (!resourcePropMap.containsKey(group) || !resourcePropMap.get(group).getTemplateMap().containsKey(nameNormalised)) {
                Log.get().error("Couldn't spawn prop: " + group + "/" + name);
                continue;
            }

            JGemsPropData propData = resourcePropMap.get(group).getTemplateMap().get(nameNormalised).getFabricGame().create();
            this.onProcessProp(template, propData, physicsWorld, sceneWorld);
        }

        for (MapObjectTemplate template : entityObjects) {
            final String name = template.getObjectId();
            final String group = template.getObjectGroup();
            final String nameNormalised = name.substring(MapObjectsIdentifiers.ENTITY.length());

            if (!resourceEntityMap.containsKey(group) || !resourceEntityMap.get(group).getTemplateMap().containsKey(nameNormalised)) {
                Log.get().error("Couldn't spawn entity: " + group + "/" + name);
                continue;
            }

            JGemsEntityData entityData = resourceEntityMap.get(group).getTemplateMap().get(nameNormalised).getFabricGame().create();
            this.onProcessEntity(template, entityData, physicsWorld, sceneWorld);
        }

        for (MapObjectTemplate template : markerObjects) {
            final String name = template.getObjectId();
            final String group = template.getObjectGroup();
            final String nameNormalised = name.substring(MapObjectsIdentifiers.MARKER.length());

            if (!resourceMarkerMap.containsKey(group) || !resourceMarkerMap.get(group).getTemplateMap().containsKey(nameNormalised)) {
                Log.get().error("Couldn't spawn marker: " + group + "/" + name);
                continue;
            }

            JGemsMarkerData markerData = resourceMarkerMap.get(group).getTemplateMap().get(nameNormalised).getFabricGame().create();
            this.onProcessMarker(template, markerData, physicsWorld, sceneWorld);
        }

        for (MapObjectTemplate template : pointLights) {
            this.onProcessPointLight(template, physicsWorld, sceneWorld);
        }
    }

    protected void onSetupSkyBox(SunData sunData, SkyData skyData, ISkyBox skyBox, SkyBox.Background background) {
        final Map<String, Pair<String, JGemsPath>> skyBoxesSet = JGemsAPI.APIAppEditorResources().getEditorResourcesManager().getSkyBoxesMap();

        if (skyBoxesSet.containsKey(skyData.skyboxPath)) {
            ICubeMapProgram cubeMapProgram = this.getLocalResources().createCubeMapTexture(null, skyBoxesSet.get(skyData.skyboxPath).getSecond(), skyBoxesSet.get(skyData.skyboxPath).getFirst(), new CubeMapTexture.Properties(true));
            skyBox.setSky2DTexture(cubeMapProgram);
        } else {
            Log.get().error("Couldn't create cubeMap: " + skyData.skyboxPath);
        }

        skyBox.getSun().setLightPosition(sunData.position);
        skyBox.getSun().setLightColor(sunData.color);
        skyBox.getSun().setSunBrightness(sunData.brightness);
    }

    protected void onSetupFog(FogData fogData, IFogScene fogScene) {
        fogScene.setColor(fogData.color);
        fogScene.setDensity(fogData.density);
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
        this.onProcessing(propObjects, markerObjects, entityObjects, pointLights, world, sceneWorld);
    }

    @Override
    public final void postProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        this.postProcessing(this.getMapDataPack(), world, sceneWorld);
    }

    @Override
    public final void onSetupSkyBox(ISkyBox skyBox, SkyBox.Background background) {
        final SunData sunData = this.getMapDataPack().getSunData();
        final SkyData skyData = this.getMapDataPack().getSkyData();
        this.onSetupSkyBox(sunData, skyData, skyBox, background);
    }

    @Override
    public final void onSetupFog(IFogScene fogScene) {
        final FogData fogData = this.getMapDataPack().getFogData();
        this.onSetupFog(fogData, fogScene);
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
        public Default(JGemsPath pathToJG3DFile, boolean inJar) {
            super(pathToJG3DFile, inJar);
        }

        @Override
        protected void onProcessProp(MapObjectTemplate template, JGemsPropData propData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
            MeshBuffer buffer = this.getLocalResources().createMeshBuffer(propData.getPathToModel(), ModelLoaderFlags.DEFAULT, false);
            SceneWorldProp sceneWorldProp = new SceneWorldProp(sceneWorld, new PropRenderData(propData.getPropRenderData(), buffer));
            sceneWorldProp.getModel().getPose().setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
            sceneWorldProp.getModel().getPose().setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
            sceneWorldProp.getModel().getPose().setScaling(template.getScaling() == null ? new Vector3f(0.0f) : template.getScaling());
            sceneWorld.addObject(sceneWorldProp);
        }

        @Override
        protected void onProcessEntity(MapObjectTemplate template, JGemsEntityData entityData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
            MeshBuffer buffer = this.getLocalResources().createMeshBuffer(entityData.getPathToModel(), ModelLoaderFlags.DEFAULT, false);
            JGemsStaticBody jGemsStaticBody = (JGemsStaticBody) new JGemsStaticBody(MeshCollider.getStatic(buffer), physicsWorld, new Vector3f(0.0f), template.getObjectId()).setCanBeDestroyed(false);
            JGemsWorldHelper.addItemInWorld(jGemsStaticBody, new EntityRenderData(entityData.getEntityRenderData(), buffer));
            jGemsStaticBody.setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
            jGemsStaticBody.setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
            jGemsStaticBody.setScaling(template.getScaling() == null ? new Vector3f(0.0f) : template.getScaling());
        }

        @Override
        protected void onProcessMarker(MapObjectTemplate template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {

        }

        @Override
        protected void onProcessPointLight(MapObjectTemplate template, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
            final Vector4f tagColor = template.getTagsContainer().getTag(TagID.DEFAULT.COLOR3).<TagColor>getTagItemUnsafeCast().getColorVector();
            final float brightness = template.getTagsContainer().getTag(TagID.DEFAULT.BRIGHTNESS).<TagFloat>getTagItemUnsafeCast().getValue();
            PointLight pointLight = new PointLight();
            pointLight.setLightPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
            pointLight.setLightColor(tagColor.xyz(new Vector3f()));
            pointLight.setBrightness(brightness);
            pointLight.on();
            sceneWorld.addLight(pointLight, null);
        }

        @Override
        protected void preProcessing(MapDataPack mapDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {

        }

        @Override
        protected void postProcessing(MapDataPack mapDataPack, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {

        }


        @Override
        public @Nullable IGameMap.IPlayerConstructor getPlayerConstructor() {
            return null;
        }
    }
}