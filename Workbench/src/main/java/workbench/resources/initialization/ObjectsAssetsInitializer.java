package workbench.resources.initialization;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.resources.Resource;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.ResourceMarker;
import api.application.workbench.resources.ResourceProp;
import api.application.workbench.resources.data.DefaultMarker;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.ModelLoaderFlags;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import workbench.WBench;
import workbench.graphics.objects.WBenchIdentifiers;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.resources.WBenchResourceManager;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ObjectsAssetsInitializer implements IAssetsInitializer {
    public void load(SystemResources systemResources) {
        APIWBenchDataManager apiwBenchDataManager = WBench.APIEditorResources().getEditorResourcesManager();

        {
            Set<Map.Entry<String, ResourceEntity>> entityEntry = apiwBenchDataManager.getResourceEntityMap().entrySet();
            for (Map.Entry<String, ResourceEntity> entry : entityEntry) {
                ResourceEntity resourceEntity = entry.getValue();
                WBenchObjectData wBenchObjectData = resourceEntity.getFabricWBench().create();
                WBenchObjectTemplate wBenchObjectTemplate = this.constructObjectTemplate(systemResources, this.getId(WBenchIdentifiers.ENTITY, resourceEntity), wBenchObjectData);
                WBench.get().getProjectObjects().addEntity(resourceEntity.getGroupId(), wBenchObjectTemplate);
            }
        }

        {
            Set<Map.Entry<String, ResourceProp>> propEntry = apiwBenchDataManager.getResourcePropMap().entrySet();
            for (Map.Entry<String, ResourceProp> entry : propEntry) {
                ResourceProp resourceProp = entry.getValue();
                WBenchObjectData wBenchObjectData = resourceProp.getFabricWBench().create();
                WBenchObjectTemplate wBenchObjectTemplate = this.constructObjectTemplate(systemResources, this.getId(WBenchIdentifiers.PROP, resourceProp), wBenchObjectData);
                WBench.get().getProjectObjects().addProp(resourceProp.getGroupId(), wBenchObjectTemplate);
            }
        }

        {
            Set<Map.Entry<String, ResourceMarker>> markerEntry = apiwBenchDataManager.getResourceMarker().entrySet();
            for (Map.Entry<String, ResourceMarker> entry : markerEntry) {
                ResourceMarker resourceMarker = entry.getValue();
                WBenchMarkerData wBenchObjectData = resourceMarker.getFabricWBench().create();
                WBenchMarkerTemplate wBenchMarkerTemplate = this.constructMarkerTemplate(systemResources, this.getId(WBenchIdentifiers.MARKER, resourceMarker), wBenchObjectData);
                WBench.get().getProjectObjects().addMarker(resourceMarker.getGroupId(), wBenchMarkerTemplate);
            }
        }
    }

    private WBenchObject.ID getId(String prefix, Resource<?, ?> resourceEntity) {
        return new WBenchObject.ID(prefix + resourceEntity.getNameId(), resourceEntity.getGroupId());
    }

    private WBenchObjectTemplate constructObjectTemplate(SystemResources systemResources, WBenchObject.ID objectId, WBenchObjectData wBenchObjectData) {
        MeshGroup meshGroup = systemResources.createMeshGroup(wBenchObjectData.getPathToModel(), ModelLoaderFlags.DEFAULT & ~ModelLoaderFlags.CREATE_COLLISION_UD, true, true);
        return new WBenchObjectTemplate(objectId, meshGroup, RenderAttributes.get(RenderTable.getIndirect(), wBenchObjectData.getRenderProperties()), wBenchObjectData.getTagsContainer(), wBenchObjectData.getTranslationConstraints());
    }

    private WBenchMarkerTemplate constructMarkerTemplate(SystemResources systemResources, WBenchObject.ID objectId, WBenchMarkerData wBenchMarkerData) {
        MeshGroup meshGroup = null;
        if (wBenchMarkerData.getDefaultMarker() != null) {
            meshGroup = this.getModelFromDefaultMarker(systemResources, wBenchMarkerData.getDefaultMarker());
        } else {
            meshGroup = systemResources.createMeshGroup(wBenchMarkerData.getPathToModel(), ModelLoaderFlags.DEFAULT & ~ModelLoaderFlags.CREATE_COLLISION_UD, false, true);
        }
        return new WBenchMarkerTemplate(objectId, meshGroup, wBenchMarkerData.getTagsContainer(), wBenchMarkerData.getTranslationConstraints(), wBenchMarkerData.getColor(), wBenchMarkerData.isTransparent());
    }

    private MeshGroup getModelFromDefaultMarker(SystemResources systemResources, @NotNull DefaultMarker defaultMarker) {
        switch (defaultMarker) {
            case CONE: {
                return WBenchResourceManager.localModelAssets.markerDefault;
            }
            case CURSOR_CONE: {
                return WBenchResourceManager.localModelAssets.markerCursor;
            }
            case AABB_ZONE:
            case POINT: {
                return WBenchResourceManager.localModelAssets.markerCube;
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
