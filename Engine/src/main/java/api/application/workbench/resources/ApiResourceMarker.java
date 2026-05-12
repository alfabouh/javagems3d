package api.application.workbench.resources;

import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import org.jetbrains.annotations.NotNull;

public class ApiResourceMarker extends APIResource<WBenchMarkerData, JGemsMarkerData> {
    private final boolean canBeUsedInBackgroundSkyBox;

    public ApiResourceMarker(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchMarkerData> fabricWBench, boolean canBeUsedInBackgroundSkyBox) {
        super(MapObjectsIdentifiers.MARKER + id, fabricWBench, JGemsMarkerData::new);
        this.canBeUsedInBackgroundSkyBox = canBeUsedInBackgroundSkyBox;
    }

    public boolean isCanBeUsedInBackgroundSkyBox() {
        return this.canBeUsedInBackgroundSkyBox;
    }
}
