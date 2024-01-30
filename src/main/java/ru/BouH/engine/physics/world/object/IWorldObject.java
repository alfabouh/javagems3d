package ru.BouH.engine.physics.world.object;

import ru.BouH.engine.proxy.IWorld;

public interface IWorldObject {
    void onSpawn(IWorld iWorld);

    void onDestroy(IWorld iWorld);
}
