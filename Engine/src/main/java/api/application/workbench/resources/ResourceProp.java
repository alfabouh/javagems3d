package api.application.workbench.resources;

import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import org.jetbrains.annotations.NotNull;

public class ResourceProp extends Resource<WBenchObjectData, JGemsPropData> {
    public ResourceProp(@NotNull String id, @NotNull Resource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull Resource.MapObjectFabric<JGemsPropData> fabricGame) {
        super(MapObjectsIdentifiers.PROP + id, fabricWBench, fabricGame);
    }
}
