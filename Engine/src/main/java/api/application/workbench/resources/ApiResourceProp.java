package api.application.workbench.resources;

import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import org.jetbrains.annotations.NotNull;

public class ApiResourceProp extends APIResource<WBenchObjectData, JGemsPropData> {
    public ApiResourceProp(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsPropData> fabricGame) {
        super(MapObjectsIdentifiers.PROP + id, fabricWBench, fabricGame);
    }
}
