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

package ru.jgems3d.engine.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;

public interface IColliderConstructor {
    CollisionShape createGeom(DynamicsSystem dynamicsSystem);
}
