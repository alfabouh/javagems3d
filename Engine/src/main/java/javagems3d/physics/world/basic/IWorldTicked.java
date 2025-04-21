package javagems3d.physics.world.basic;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.physics.world.IWorld;

public interface IWorldTicked {
    default void onUpdateWithEvent(IWorld world) {
        if (!EventLauncher.pushEvent(new EventBus.WorldObjectUpdate(EventBus.Run.PRE, this)).isCancelled()) {
            this.onUpdate(world);
            EventLauncher.pushEvent(new EventBus.WorldObjectUpdate(EventBus.Run.POST, this));
        }
    }

    void onUpdate(IWorld iWorld);
}
