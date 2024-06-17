package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects;

import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.map_prop.FogProp;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.map_prop.SkyProp;

public class SaveMapProperties {
    private final String mapName;
    private final SkyProp skyProp;
    private final FogProp fogProp;

    public SaveMapProperties(String mapName, SkyProp skyProp, FogProp fogProp) {
        this.mapName = mapName;
        this.skyProp = skyProp;
        this.fogProp = fogProp;
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
