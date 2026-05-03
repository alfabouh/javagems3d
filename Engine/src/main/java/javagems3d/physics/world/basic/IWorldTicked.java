package javagems3d.physics.world.basic;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.physics.world.IWorld;

public interface IWorldTicked {
    void onUpdate(IWorld iWorld);
}
