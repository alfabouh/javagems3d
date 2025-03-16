package workbench.resources.initialization;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.data.WBenchObjectData;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.ModelLoaderFlags;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import workbench.WBench;
import workbench.graphics.objects.templates.WBenchObjectTemplate;

import java.util.Map;
import java.util.Set;

public class ObjectsAssetsInitializer implements IAssetsInitializer {
    public void load(SystemResources systemResources) {
        APIWBenchDataManager apiwBenchDataManager = WBench.APIEditorResources().getEditorResourcesManager();
        Set<Map.Entry<String, ResourceEntity>> entityEntry = apiwBenchDataManager.getResourceEntityMap().entrySet();
        for (Map.Entry<String, ResourceEntity> entry : entityEntry) {
            ResourceEntity resourceEntity = entry.getValue();
            WBenchObjectData wBenchObjectData = resourceEntity.getFabricWBench().create();
            WBenchObjectTemplate wBenchObjectTemplate = this.constructObjectTemplate(systemResources, resourceEntity.getId(), wBenchObjectData);
            WBench.get().getProjectObjects().addEntity(resourceEntity.getGroupId(), wBenchObjectTemplate);
        }
    }

    private WBenchObjectTemplate constructObjectTemplate(SystemResources systemResources, String id, WBenchObjectData wBenchObjectData) {
        MeshGroup meshGroup = systemResources.createMeshGroup(wBenchObjectData.getPathToModel(), ModelLoaderFlags.DEFAULT & ~ModelLoaderFlags.CREATE_COLLISION_UD, true);
        return new WBenchObjectTemplate(id, meshGroup, RenderAttributes.get(RenderTable.getIndirect(), wBenchObjectData.getRenderProperties()), wBenchObjectData.getTagsContainer(), wBenchObjectData.getTranslationConstraints());
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
