package ru.alfabouh.jgems3d.mapsys.file.save.objects;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.mapsys.file.save.objects.map_prop.FogProp;
import ru.alfabouh.jgems3d.mapsys.file.save.objects.map_prop.SkyProp;

public class MapProperties {
    private String mapName;
    private final SkyProp skyProp;
    private final FogProp fogProp;

    public MapProperties(@NotNull String mapName, @NotNull SkyProp skyProp, @NotNull FogProp fogProp) {
        this.mapName = mapName;
        this.skyProp = skyProp;
        this.fogProp = fogProp;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return this.mapName;
    }

    public SkyProp getSkyProp() {
        return this.skyProp;
    }

    public FogProp getFogProp() {
        return this.fogProp;
    }
}
