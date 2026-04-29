package javagems3d.physics.entities.kinematic.player;

import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.PhysicsWorld;

public interface IPlayer extends IControllable {
    float getScalarSpeed();
    float getPlayerHeight();
}
