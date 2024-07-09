package ru.alfabouh.jgems3d.engine.system.map.loaders;

import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.system.map.MapInfo;

public interface IMapLoader {
    void createMap(World world);
    void postLoad(World world);

    MapInfo getLevelInfo();
}
