package api.application.workbench.resources;

import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import org.jetbrains.annotations.NotNull;

public class ResourceProp extends Resource<WBenchObjectData, JGemsPropData> {
    public ResourceProp(@NotNull String id, @NotNull WFabric<WBenchObjectData> fabricWBench, @NotNull WFabric<JGemsPropData> fabricGame) {
        super(id, fabricWBench, fabricGame);
    }
}
