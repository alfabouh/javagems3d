package ru.alfabouh.engine.physics.world.object;

import ru.alfabouh.engine.physics.world.IWorld;

public interface IWorldObject {
    void onSpawn(IWorld iWorld);

    void onDestroy(IWorld iWorld);
}
