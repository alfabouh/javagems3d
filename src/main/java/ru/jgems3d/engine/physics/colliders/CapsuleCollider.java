package ru.jgems3d.engine.physics.colliders;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import org.joml.Vector2f;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;

public class CapsuleCollider implements IColliderConstructor {
    private final Vector2f size;

    public CapsuleCollider(Vector2f size) {
        this.size = size;
    }

    @Override
    public CollisionShape createGeom(DynamicsSystem dynamicsSystem) {
        return new CapsuleCollisionShape(this.size.x, size.y);
    }
}
