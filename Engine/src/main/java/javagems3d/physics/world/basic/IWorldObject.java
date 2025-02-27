package javagems3d.physics.world.basic;

import javagems3d.physics.world.IWorld;

public interface IWorldObject {
    void onSpawn(IWorld iWorld);

    void onDestroy(IWorld iWorld);

    boolean isDead();
}
