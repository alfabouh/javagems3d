package ru.jgems3d.engine.physics.world.basic;

import ru.jgems3d.engine.physics.world.IWorld;

public interface IWorldObject {
    void onSpawn(IWorld iWorld);

    void onDestroy(IWorld iWorld);

    boolean isDead();
}
