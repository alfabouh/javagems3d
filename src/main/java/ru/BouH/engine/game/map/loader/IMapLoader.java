package ru.BouH.engine.game.map.loader;

import ru.BouH.engine.physics.world.World;

public interface IMapLoader {
    void onMapUpdate(World world);

    void addEntities(World world);

    void addBrushes(World world);

    void addLiquids(World world);

    void addTriggers(World world);

    void addSounds(World world);

    void readNavMesh(World world);

    MapInfo levelInfo();
}
