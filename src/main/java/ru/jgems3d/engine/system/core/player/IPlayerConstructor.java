package ru.jgems3d.engine.system.core.player;

import org.joml.Vector3f;
import ru.jgems3d.engine.physics.entities.player.Player;
import ru.jgems3d.engine.physics.world.PhysicsWorld;

@FunctionalInterface
public interface IPlayerConstructor {
    Player constructPlayer(PhysicsWorld world, Vector3f startPos, Vector3f startRot);
}
