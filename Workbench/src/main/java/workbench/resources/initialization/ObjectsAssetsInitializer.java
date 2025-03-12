package workbench.resources.initialization;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.data.WBenchObjectData;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.configuration.RenderProperties;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.ModelLoaderFlags;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;
import workbench.WBench;
import workbench.graphics.objects.templates.Type;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.resources.shaders.WBenchShaderManager;

import java.util.Map;
import java.util.Set;

public class ObjectsAssetsInitializer implements IAssetsInitializer {

    public void load(SystemResources systemResources) {
        APIWBenchDataManager apiwBenchDataManager = WBench.APIEditorResources().getEditorResourcesManager();
        Set<Map.Entry<String, ResourceEntity>> entityEntry = apiwBenchDataManager.getResourceEntityMap().entrySet();
        for (Map.Entry<String, ResourceEntity> entry : entityEntry) {
            ResourceEntity resourceEntity = entry.getValue();
            WBenchObjectData wBenchObjectData = resourceEntity.getFabricWBench().create();
            WBenchObjectTemplate wBenchObjectTemplate = this.constructObjectTemplate(systemResources, resourceEntity.getId(), Type.W_ENTITY, wBenchObjectData.getPathToModel(), wBenchObjectData.getRenderProperties());
            WBench.get().getProjectObjects().addEntity(resourceEntity.getGroupId(), wBenchObjectTemplate);
        }
    }

    private WBenchObjectTemplate constructObjectTemplate(SystemResources systemResources, String id, Type type, JGemsPath pathToModel, RenderProperties renderProperties) {
        MeshGroup meshGroup = systemResources.createMeshGroup(pathToModel, ModelLoaderFlags.DEFAULT & ~ModelLoaderFlags.CREATE_COLLISION_UD, true);
        return new WBenchObjectTemplate(id, type, meshGroup, RenderAttributes.get(RenderTable.getDefaultIndirect(), renderProperties));
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
