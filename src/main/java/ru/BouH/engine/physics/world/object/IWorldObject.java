package ru.BouH.engine.physics.world.object;

import ru.BouH.engine.physics.world.IWorld;

public interface IWorldObject {
    void onSpawn(IWorld iWorld);

    void onDestroy(IWorld iWorld);
}
