package ru.alfabouh.jgems3d.engine.physics.world.object;

import ru.alfabouh.jgems3d.engine.physics.world.IWorld;

public interface IWorldObject {
    void onSpawn(IWorld iWorld);

    void onDestroy(IWorld iWorld);
}
