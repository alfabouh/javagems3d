package workbench.resources.initialization.map;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.manager.ApiResourceObjectsFolder;
import api.application.workbench.resources.APIResource;
import api.application.workbench.resources.ApiResourceEntity;
import api.application.workbench.resources.ApiResourceMarker;
import api.application.workbench.resources.ApiResourceProp;
import api.application.workbench.resources.data.DefaultMarker;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import javagems3d.JGems3D;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import workbench.WBench;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.objects.templates.WBenchTemplate;
import workbench.project.managing.instances.IAsset;
import javagems3d.system.service.collections.AbstractObjectsFolder;
import workbench.project.managing.instances.group.GameResourceAssetsFolder;
import workbench.project.managing.instances.misc.GameResourceModelAsset;
import workbench.project.managing.instances.world.GameResourceEntityObjectAsset;
import workbench.project.managing.instances.world.GameResourcePropObjectAsset;
import workbench.project.managing.instances.world.GameResourceWorldObjectAsset;
import workbench.project.map.MapObjectTemplatesFolder;
import workbench.resources.WBenchResourceManager;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WBenchMapEditorObjectsAssetsInitializer implements IAssetsInitializer {
    private WBenchObjectTemplate createMapObjectTemplateFromGameSource(String prefix, String path, GameResourceWorldObjectAsset gameResourceWorldObjectAsset) {
        final WBenchObject.ID ID = new WBenchObject.ID(prefix + gameResourceWorldObjectAsset.getID(), path);
        final GameResourceModelAsset modelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(gameResourceWorldObjectAsset.getModelAssetRelativePath());
        final MeshGroup meshGroup = modelAsset == null ? null : modelAsset.getMeshGroup();
        final RenderAttributes renderAttributes = RenderAttributes.get(RenderTable.getIndirect(), WBenchRenderProperties.getDefault());
        final TagsContainer tagsContainer = gameResourceWorldObjectAsset.getTagsContainer();
        final TranslationConstraints translationConstraints = gameResourceWorldObjectAsset.getAxisConstraints();
        return new WBenchObjectTemplate(ID, meshGroup, renderAttributes, tagsContainer, translationConstraints);
    }

    private WBenchObjectTemplate createMapObjectTemplateFromApiPropSource(SystemResources systemResources, String path, APIResource<WBenchObjectData, ?> apiResourceProp) {
        final WBenchObjectData wBenchObjectData = apiResourceProp.getFabricWBench().create();
        final WBenchObject.ID ID = new WBenchObject.ID(apiResourceProp.getName(), path);
        final MeshGroup meshGroup = systemResources.createMeshGroupWithBindlessBufferAttachment(JGems3D.GetSource.JAR, wBenchObjectData.getPathToModel(), true);
        final RenderAttributes renderAttributes = RenderAttributes.get(RenderTable.getIndirect(), wBenchObjectData.getRenderProperties());
        final TagsContainer tagsContainer = wBenchObjectData.getTagsContainer();
        final TranslationConstraints translationConstraints = wBenchObjectData.getTranslationConstraints();
        return new WBenchObjectTemplate(ID, meshGroup, renderAttributes, tagsContainer, translationConstraints);
    }

    private WBenchMarkerTemplate createMapObjectTemplateFromApiMarkerSource(SystemResources systemResources, String path, APIResource<WBenchMarkerData, ?> apiResourceProp) {
        final WBenchMarkerData wBenchMarkerData = apiResourceProp.getFabricWBench().create();
        final WBenchObject.ID ID = new WBenchObject.ID(apiResourceProp.getName(), path);
        MeshGroup meshGroup = null;
        if (wBenchMarkerData.getDefaultMarker() != null) {
            meshGroup = this.getModelFromDefaultMarker(systemResources, wBenchMarkerData.getDefaultMarker());
        } else {
            meshGroup = systemResources.createMeshGroupWithBindlessBufferAttachment(JGems3D.GetSource.EXTERNAL, wBenchMarkerData.getPathToModel(), false);
        }
        final TagsContainer tagsContainer = wBenchMarkerData.getTagsContainer();
        final TranslationConstraints translationConstraints = wBenchMarkerData.getTranslationConstraints();
        return new WBenchMarkerTemplate(ID, meshGroup, tagsContainer, translationConstraints, wBenchMarkerData.getColor(), wBenchMarkerData.isTransparent());
    }

    private <T extends AbstractObjectsFolder.ObjectWithName, E extends WBenchTemplate> void copyPlusConvertFolder(AbstractObjectsFolder<T> from, MapObjectTemplatesFolder<E> to, BiFunction<String, T, E> convert) {
        for (T obj : from.getObjectsThere()) {
            if (to.getObjectsThereMap().containsKey(obj.getName())) {
                Log.get().error("MapObjectTemplatesFolder folder " + to.getHierarchy() + " already contains object " + obj.getName());
            } else {
                to.putObjectThere(convert.apply(to.getHierarchy(), obj));
            }
        }

        MapObjectTemplatesFolder<E> newFolderPut = null;
        for (AbstractObjectsFolder<T> folderInside : from.getFoldersThere()) {
            final String folderName = folderInside.getName();
            if (to.getFoldersThereMap().containsKey(folderName)) {
                Log.get().warn("MapObjectTemplatesFolder folder " + to.getHierarchy() + " already contains folder " + folderInside.getHierarchy());
                newFolderPut = (MapObjectTemplatesFolder<E>) to.getFolderThere(folderName);
            } else {
                newFolderPut = new MapObjectTemplatesFolder<>(folderName);
                to.putFolderThere(newFolderPut);
            }
            this.copyPlusConvertFolder(folderInside, newFolderPut, convert);
        }
    }

    public void load(SystemResources systemResources) {
        final APIWBenchDataManager api = WBench.APIEditorResources().getEditorResourcesManager();
        final GameResourceAssetsFolder<GameResourcePropObjectAsset> propAssetsFolder = WBench.get().getGameProjectManager().getGameResourcesManager().getPropAssetsFolder();
        final GameResourceAssetsFolder<GameResourceEntityObjectAsset> entityAssetsFolder = WBench.get().getGameProjectManager().getGameResourcesManager().getEntityAssetsFolder();
        final ApiResourceObjectsFolder<?, ?, ApiResourceEntity> apiEntities = api.getEntities();
        final ApiResourceObjectsFolder<?, ?, ApiResourceProp> apiProps = api.getProps();
        final ApiResourceObjectsFolder<?, ?, ApiResourceMarker> apiMarkers = api.getMarkers();

        {
            this.copyPlusConvertFolder(propAssetsFolder, WBench.get().getMapProjectManager().getMapObjectTemplates().getProps(), (path, e) -> this.createMapObjectTemplateFromGameSource(MapObjectsIdentifiers.PROP, path, e));
            this.copyPlusConvertFolder(entityAssetsFolder, WBench.get().getMapProjectManager().getMapObjectTemplates().getEntities(), (path, e) -> this.createMapObjectTemplateFromGameSource(MapObjectsIdentifiers.ENTITY, path, e));
        }
        {
            this.copyPlusConvertFolder(apiProps, WBench.get().getMapProjectManager().getMapObjectTemplates().getProps(), (path, e) -> this.createMapObjectTemplateFromApiPropSource(systemResources, path, e));
            this.copyPlusConvertFolder(apiEntities, WBench.get().getMapProjectManager().getMapObjectTemplates().getEntities(), (path, e) -> this.createMapObjectTemplateFromApiPropSource(systemResources, path, e));
            this.copyPlusConvertFolder(apiMarkers, WBench.get().getMapProjectManager().getMapObjectTemplates().getMarkers(), (path, e) -> this.createMapObjectTemplateFromApiMarkerSource(systemResources, path, e));
        }
    }

    private MeshGroup getModelFromDefaultMarker(SystemResources systemResources, @NotNull DefaultMarker defaultMarker) {
        switch (defaultMarker) {
            case CONE: {
                return WBenchResourceManager.gameEditorModelAssets.markerDefault;
            }
            case CURSOR_CONE: {
                return WBenchResourceManager.gameEditorModelAssets.markerCursor;
            }
            case AABB_ZONE: {
                return WBenchResourceManager.gameEditorModelAssets.markerAabb;
            }
            case POINT: {
                return WBenchResourceManager.gameEditorModelAssets.markerCube;
            }
            default:
                throw new JGemsRuntimeException("NULL: " + defaultMarker);
        }
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}
