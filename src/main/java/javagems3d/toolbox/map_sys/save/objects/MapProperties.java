/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.toolbox.map_sys.save.objects;

import org.jetbrains.annotations.NotNull;
import javagems3d.toolbox.map_sys.save.objects.map_prop.FogProp;
import javagems3d.toolbox.map_sys.save.objects.map_prop.SkyProp;

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
