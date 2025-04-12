package api.application.workbench.resources;

import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import org.jetbrains.annotations.NotNull;

public class ResourceEntity extends Resource<WBenchObjectData, JGemsEntityData> {
    public ResourceEntity(@NotNull String id, @NotNull Resource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull Resource.MapObjectFabric<JGemsEntityData> fabricGame) {
        super(MapObjectsIdentifiers.ENTITY + id, fabricWBench, fabricGame);
    }
}
