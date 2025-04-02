package api.application.workbench.resources;

import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import org.jetbrains.annotations.NotNull;

public class ResourceMarker extends Resource<WBenchMarkerData, JGemsMarkerData> {
    public ResourceMarker(@NotNull String id, @NotNull WFabric<WBenchMarkerData> fabricWBench) {
        super(id, fabricWBench, JGemsMarkerData::new);
    }
}
