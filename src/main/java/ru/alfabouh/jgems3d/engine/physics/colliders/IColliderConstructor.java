package ru.alfabouh.jgems3d.engine.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;

public interface IColliderConstructor {
    CollisionShape createGeom(DynamicsSystem dynamicsSystem);
}
