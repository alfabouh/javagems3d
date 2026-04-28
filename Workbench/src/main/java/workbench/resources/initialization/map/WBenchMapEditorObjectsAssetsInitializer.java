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
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.external.gaming.def.world.GameResourceMarkerObjectAsset;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.external.mapping.tags.items.TagCheckBoolean;
import javagems3d.system.external.mapping.tags.items.TagRadioBoolean;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.WBench;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.objects.templates.WBenchTemplate;
import javagems3d.system.service.files.VirtualObjectsFolder;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import javagems3d.system.external.gaming.def.misc.GameResourceModelAsset;
import javagems3d.system.external.gaming.def.world.GameResourceEntityObjectAsset;
import javagems3d.system.external.gaming.def.world.GameResourcePropObjectAsset;
import javagems3d.system.external.gaming.def.world.GameResourceWorldObjectAsset;
import workbench.project.map.MapObjectTemplatesFolder;
import workbench.resources.WBenchResourceManager;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class WBenchMapEditorObjectsAssetsInitializer implements IAssetsInitializer {
    private WBenchObjectTemplate createMapObjectTemplateFromGameSource(String prefix, String path, GameResourceWorldObjectAsset gameResourceWorldObjectAsset, @Nullable Consumer<TagsContainer> doSomeTags) {
        final boolean validProps = gameResourceWorldObjectAsset.getRenderProperties() != null;
        final WBenchObject.ID ID = new WBenchObject.ID(prefix + gameResourceWorldObjectAsset.getID(), path);
        final GameResourceModelAsset modelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(gameResourceWorldObjectAsset.getModelAssetRelativePath());
        final MeshGroup meshGroup = modelAsset == null ? null : modelAsset.meshGroup();
        final RenderAttributes renderAttributes = RenderAttributes.get(RenderTable.getDirect(), new WBenchRenderProperties()
                .setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, !validProps || !gameResourceWorldObjectAsset.getRenderProperties().has(JGemsRenderProperties.KEY_SHADOW_CASTER) || gameResourceWorldObjectAsset.getRenderProperties().getBool(JGemsRenderProperties.KEY_SHADOW_CASTER))
                .setValueFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, (!validProps || !gameResourceWorldObjectAsset.getRenderProperties().has(JGemsRenderProperties.KEY_SHADOW_CASTER)) ? 1.0f : gameResourceWorldObjectAsset.getRenderProperties().getFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD))
        );
        final TagsContainer tagsContainer = gameResourceWorldObjectAsset.getTagsContainer();
        if (doSomeTags != null) {
            doSomeTags.accept(tagsContainer);
        }
        final TranslationConstraints translationConstraints = gameResourceWorldObjectAsset.getAxisConstraints();
        return new WBenchObjectTemplate(ID, meshGroup, renderAttributes, tagsContainer, translationConstraints).setModelDef(gameResourceWorldObjectAsset.getModelAssetRelativePath());
    }

    private WBenchMarkerTemplate createMapMarkerTemplateFromGameSource(String prefix, String path, GameResourceMarkerObjectAsset gameResourceMarkerObjectAsset) {
        final WBenchObject.ID ID = new WBenchObject.ID(prefix + gameResourceMarkerObjectAsset.getID(), path);
        final GameResourceModelAsset modelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(gameResourceMarkerObjectAsset.getModelAssetRelativePath());
        final MeshGroup meshGroup = modelAsset == null ? null : modelAsset.meshGroup();
        final TagsContainer tagsContainer = gameResourceMarkerObjectAsset.getTagsContainer();
        final TranslationConstraints translationConstraints = gameResourceMarkerObjectAsset.getAxisConstraints();
        return new WBenchMarkerTemplate(ID, meshGroup, tagsContainer, translationConstraints, gameResourceMarkerObjectAsset.getColor(), gameResourceMarkerObjectAsset.isTransparent()).setModelDef(gameResourceMarkerObjectAsset.getModelAssetRelativePath());
    }

    private WBenchObjectTemplate createMapObjectTemplateFromApiPropSource(SystemResources systemResources, String path, APIResource<WBenchObjectData, ?> apiResourceProp, @Nullable Consumer<TagsContainer> doSomeTags) {
        final WBenchObjectData wBenchObjectData = apiResourceProp.getFabricWBench().create();
        final WBenchObject.ID ID = new WBenchObject.ID(apiResourceProp.name(), path);
        final MeshGroup meshGroup = systemResources.createMeshGroup(wBenchObjectData.getPathToModel(), true);
        final RenderAttributes renderAttributes = RenderAttributes.get(RenderTable.getDirect(), wBenchObjectData.getRenderProperties());
        final TagsContainer tagsContainer = wBenchObjectData.getTagsContainer();
        if (doSomeTags != null) {
            doSomeTags.accept(tagsContainer);
        }
        final TranslationConstraints translationConstraints = wBenchObjectData.getTranslationConstraints();
        return new WBenchObjectTemplate(ID, meshGroup, renderAttributes, tagsContainer, translationConstraints).setModelDef(wBenchObjectData.getPathToModel().toString()).setModelDef(wBenchObjectData.getPathToModel() == null ? null : wBenchObjectData.getPathToModel().toString());
    }

    private WBenchMarkerTemplate createMapObjectTemplateFromApiMarkerSource(SystemResources systemResources, String path, APIResource<WBenchMarkerData, ?> apiResourceProp) {
        final WBenchMarkerData wBenchMarkerData = apiResourceProp.getFabricWBench().create();
        final WBenchObject.ID ID = new WBenchObject.ID(apiResourceProp.name(), path);
        MeshGroup meshGroup = null;
        if (wBenchMarkerData.getDefaultMarker() != null) {
            meshGroup = this.getModelFromDefaultMarker(systemResources, wBenchMarkerData.getDefaultMarker());
        } else {
            meshGroup = systemResources.createMeshGroup(new JGemsPathSource(wBenchMarkerData.getPathToModel(), ISource.Source.OUTSIDE_JAR), true);
        }
        final TagsContainer tagsContainer = wBenchMarkerData.getTagsContainer();
        final TranslationConstraints translationConstraints = wBenchMarkerData.getTranslationConstraints();
        return new WBenchMarkerTemplate(ID, meshGroup, tagsContainer, translationConstraints, wBenchMarkerData.getColor(), wBenchMarkerData.isTransparent()).setModelDef(wBenchMarkerData.getDefaultMarker() != null ? "DEFAULT" : (wBenchMarkerData.getPathToModel() == null ? null : wBenchMarkerData.getPathToModel().toString()));
    }

    private <T extends VirtualObjectsFolder.ObjectWithName, E extends WBenchTemplate> void copyPlusConvertFolder(VirtualObjectsFolder<T> from, MapObjectTemplatesFolder<E> to, BiFunction<String, T, E> convert) {
        for (T obj : from.getObjectsThere()) {
            if (to.getObjectsThereMap().containsKey(obj.name())) {
                Log.get().error("MapObjectTemplatesFolder folder " + to.getHierarchy() + " already contains object " + obj.name());
            } else {
                to.putObjectThere(convert.apply(to.getHierarchy(), obj));
            }
        }

        MapObjectTemplatesFolder<E> newFolderPut = null;
        for (VirtualObjectsFolder<T> folderInside : from.getFoldersThere()) {
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
        final GameResourceAssetsFolder<GameResourceMarkerObjectAsset> markerAssetsFolder = WBench.get().getGameProjectManager().getGameResourcesManager().getMarkerAssetsFolder();
        final ApiResourceObjectsFolder<?, ?, ApiResourceEntity> apiEntities = api.getEntities();
        final ApiResourceObjectsFolder<?, ?, ApiResourceProp> apiProps = api.getProps();
        final ApiResourceObjectsFolder<?, ?, ApiResourceMarker> apiMarkers = api.getMarkers();

        {
            this.copyPlusConvertFolder(propAssetsFolder, WBench.get().getMapProjectManager().getMapObjectTemplates().getProps(), (path, e) -> this.createMapObjectTemplateFromGameSource(MapObjectsIdentifiers.PROP, path, e, tagsContainer -> {
                final Tag<TagRadioBoolean> directIndirect = Tag.create(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, new TagRadioBoolean(new TagRadioBoolean.Info("Direct", true), new TagRadioBoolean.Info("Indirect", false)));
                tagsContainer.addTag(directIndirect);
            }));
            this.copyPlusConvertFolder(entityAssetsFolder, WBench.get().getMapProjectManager().getMapObjectTemplates().getEntities(), (path, e) -> this.createMapObjectTemplateFromGameSource(MapObjectsIdentifiers.ENTITY, path, e, tagsContainer -> {
                final Tag<TagRadioBoolean> physical = Tag.create(TagID.DEFAULT.PHYSICS_STATE, new TagRadioBoolean(new TagRadioBoolean.Info("Dynamic", false), new TagRadioBoolean.Info("Static", true)));
                final Tag<TagRadioBoolean> directIndirect = Tag.create(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, new TagRadioBoolean(new TagRadioBoolean.Info("Direct", true), new TagRadioBoolean.Info("Indirect", false)));
                tagsContainer.addTag(physical);
                tagsContainer.addTag(directIndirect);
            }));
            this.copyPlusConvertFolder(markerAssetsFolder, WBench.get().getMapProjectManager().getMapObjectTemplates().getMarkers(), (path, e) -> this.createMapMarkerTemplateFromGameSource(MapObjectsIdentifiers.MARKER, path, e));
        }
        {
            this.copyPlusConvertFolder(apiProps, WBench.get().getMapProjectManager().getMapObjectTemplates().getProps(), (path, e) -> this.createMapObjectTemplateFromApiPropSource(systemResources, path, e, tagsContainer -> {
                final Tag<TagRadioBoolean> directIndirect = Tag.create(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, new TagRadioBoolean(new TagRadioBoolean.Info("Direct", true), new TagRadioBoolean.Info("Indirect", false)));
                tagsContainer.addTag(directIndirect);
            }));
            this.copyPlusConvertFolder(apiEntities, WBench.get().getMapProjectManager().getMapObjectTemplates().getEntities(), (path, e) -> this.createMapObjectTemplateFromApiPropSource(systemResources, path, e, tagsContainer -> {
                final Tag<TagRadioBoolean> physical = Tag.create(TagID.DEFAULT.PHYSICS_STATE, new TagRadioBoolean(new TagRadioBoolean.Info("Dynamic", false), new TagRadioBoolean.Info("Static", true)));
                final Tag<TagRadioBoolean> directIndirect = Tag.create(TagID.DEFAULT.DIRECT_INDIRECT_RENDERING, new TagRadioBoolean(new TagRadioBoolean.Info("Direct", true), new TagRadioBoolean.Info("Indirect", false)));
                tagsContainer.addTag(physical);
                tagsContainer.addTag(directIndirect);
            }));
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
