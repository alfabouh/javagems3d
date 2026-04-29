package javagems3d.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;

@FunctionalInterface
public interface IColliderConstructor {
    CollisionShape execute();

    static IColliderConstructor get(CollisionShape collisionShape) {
        return () -> collisionShape;
    }
}