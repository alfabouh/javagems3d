package api.application.workbench.resources;

import api.application.workbench.resources.data.JGemsEntityData;
import api.application.workbench.resources.data.WBenchObjectData;
import org.jetbrains.annotations.NotNull;

public class ResourceEntity extends Resource<WBenchObjectData, JGemsEntityData> {
    public ResourceEntity(@NotNull String id, @NotNull WFabric<WBenchObjectData> fabricWBench, @NotNull WFabric<JGemsEntityData> fabricGame) {
        super(id, fabricWBench, fabricGame);
    }
}
