package api.application.workbench.resources;

import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import org.jetbrains.annotations.NotNull;

public class ApiResourceEntity extends APIResource<WBenchObjectData, JGemsEntityData> {
    public ApiResourceEntity(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame) {
        super(MapObjectsIdentifiers.ENTITY + id, fabricWBench, fabricGame);
    }
}
