package javagems3d.physics.world.basic;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.physics.world.IWorld;

public interface IWorldObject {
    default void onSpawnWithEvent(IWorld world) {
        EventLauncher.pushEvent(new EventBus.WorldObjectState(EventBus.Run.PRE, EventBus.ObjectState.SPAWN, this));
        this.onSpawn(world);
        EventLauncher.pushEvent(new EventBus.WorldObjectState(EventBus.Run.POST, EventBus.ObjectState.SPAWN, this));
    }

    default void onDestroyWithEvent(IWorld world) {
        EventLauncher.pushEvent(new EventBus.WorldObjectState(EventBus.Run.PRE, EventBus.ObjectState.DESTROY, this));
        this.onDestroy(world);
        EventLauncher.pushEvent(new EventBus.WorldObjectState(EventBus.Run.POST, EventBus.ObjectState.DESTROY, this));
    }

    void onSpawn(IWorld iWorld);
    void onDestroy(IWorld iWorld);

    void setDead();
    boolean isDead();

    default boolean isAlive() {
        return !this.isDead();
    }
}
