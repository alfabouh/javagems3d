package javagems3d.temp.map_sys.save.objects;

import javagems3d.temp.map_sys.save.objects.map_prop.SkyProp;
import org.jetbrains.annotations.NotNull;
import javagems3d.temp.map_sys.save.objects.map_prop.FogProp;

public class MapProperties {
    private final SkyProp skyProp;
    private final FogProp fogProp;
    private String mapName;

    public MapProperties(@NotNull String mapName, @NotNull SkyProp skyProp, @NotNull FogProp fogProp) {
        this.mapName = mapName;
        this.skyProp = skyProp;
        this.fogProp = fogProp;
    }

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public SkyProp getSkyProp() {
        return this.skyProp;
    }

    public FogProp getFogProp() {
        return this.fogProp;
    }
}
