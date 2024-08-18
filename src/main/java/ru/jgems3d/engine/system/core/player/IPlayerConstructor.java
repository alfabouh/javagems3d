/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.system.core.player;

import org.joml.Vector3f;
import ru.jgems3d.engine.physics.entities.player.Player;
import ru.jgems3d.engine.physics.world.PhysicsWorld;

@FunctionalInterface
public interface IPlayerConstructor {
    Player constructPlayer(PhysicsWorld world, Vector3f startPos, Vector3f startRot);
}
