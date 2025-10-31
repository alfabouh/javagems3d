package workbench.resources.initialization;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.resources.APIResource;
import api.application.workbench.resources.ApiResourceEntity;
import api.application.workbench.resources.ApiResourceMarker;
import api.application.workbench.resources.ApiResourceProp;
import api.application.workbench.resources.data.DefaultMarker;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import workbench.WBench;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.resources.WBenchResourceManager;

import java.util.Map;
import java.util.Set;

public class ObjectsAssetsInitializer implements IAssetsInitializer {
    public void load(SystemResources systemResources) {
        APIWBenchDataManager apiwBenchDataManager = WBench.APIEditorResources().getEditorResourcesManager();

        {
            Set<Map.Entry<String, APIWBenchDataManager.TemplatesTable<ApiResourceEntity>>> entityEntry = apiwBenchDataManager.getResourceEntityMap().entrySet();
            for (Map.Entry<String, APIWBenchDataManager.TemplatesTable<ApiResourceEntity>> entry : entityEntry) {
                final APIWBenchDataManager.TemplatesTable<ApiResourceEntity> table = entry.getValue();
                for (ApiResourceEntity resource : table.getTemplateMap().values()) {
                    WBenchObjectData wBenchObjectData = resource.getFabricWBench().create();
                    WBenchObjectTemplate wBenchObjectTemplate = this.constructObjectTemplate(systemResources, this.getId(resource), wBenchObjectData);
                    WBench.get().getMapProjectManager().getMapObjectTemplates().addEntity(entry.getKey(), wBenchObjectTemplate);
                }
            }
        }

        {
            Set<Map.Entry<String, APIWBenchDataManager.TemplatesTable<ApiResourceProp>>> propEntry = apiwBenchDataManager.getResourcePropMap().entrySet();
            for (Map.Entry<String, APIWBenchDataManager.TemplatesTable<ApiResourceProp>> entry : propEntry) {
                final APIWBenchDataManager.TemplatesTable<ApiResourceProp> table = entry.getValue();
                for (ApiResourceProp resource : table.getTemplateMap().values()) {
                    WBenchObjectData wBenchObjectData = resource.getFabricWBench().create();
                    WBenchObjectTemplate wBenchObjectTemplate = this.constructObjectTemplate(systemResources, this.getId(resource), wBenchObjectData);
                    WBench.get().getMapProjectManager().getMapObjectTemplates().addProp(entry.getKey(), wBenchObjectTemplate);
                }
            }
        }

        {
            Set<Map.Entry<String, APIWBenchDataManager.TemplatesTable<ApiResourceMarker>>> markerEntry = apiwBenchDataManager.getResourceMarkerMap().entrySet();
            for (Map.Entry<String, APIWBenchDataManager.TemplatesTable<ApiResourceMarker>> entry : markerEntry) {
                final APIWBenchDataManager.TemplatesTable<ApiResourceMarker> table = entry.getValue();
                for (ApiResourceMarker resource : table.getTemplateMap().values()) {
                    WBenchMarkerData wBenchObjectData = resource.getFabricWBench().create();
                    WBenchMarkerTemplate wBenchObjectTemplate = this.constructMarkerTemplate(systemResources, this.getId(resource), wBenchObjectData);
                    WBench.get().getMapProjectManager().getMapObjectTemplates().addMarker(entry.getKey(), wBenchObjectTemplate);
                }
            }
        }
    }

    private WBenchObject.ID getId(APIResource<?, ?> apiResourceEntity) {
        return new WBenchObject.ID(apiResourceEntity.getNameId(), apiResourceEntity.getGroupId());
    }

    private WBenchObjectTemplate constructObjectTemplate(SystemResources systemResources, WBenchObject.ID objectId, WBenchObjectData wBenchObjectData) {
        MeshGroup meshGroup = systemResources.createMeshGroup_Buffer(wBenchObjectData.getPathToModel(),true);
        return new WBenchObjectTemplate(objectId, meshGroup, RenderAttributes.get(RenderTable.getIndirect(), wBenchObjectData.getRenderProperties()), wBenchObjectData.getTagsContainer(), wBenchObjectData.getTranslationConstraints());
    }

    private WBenchMarkerTemplate constructMarkerTemplate(SystemResources systemResources, WBenchObject.ID objectId, WBenchMarkerData wBenchMarkerData) {
        MeshGroup meshGroup = null;
        if (wBenchMarkerData.getDefaultMarker() != null) {
            meshGroup = this.getModelFromDefaultMarker(systemResources, wBenchMarkerData.getDefaultMarker());
        } else {
            meshGroup = systemResources.createMeshGroup_Buffer(wBenchMarkerData.getPathToModel(), false);
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
            case AABB_ZONE: {
                return WBenchResourceManager.localModelAssets.markerAabb;
            }
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
