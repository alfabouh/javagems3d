package javagems3d.physics.colliders;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import org.joml.Vector2f;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;

public class CapsuleCollider implements IColliderConstructor {
    private final Vector2f size;

    public CapsuleCollider(Vector2f size) {
        this.size = size;
    }

    @Override
    public CollisionShape execute() {
        return new CapsuleCollisionShape(this.size.x, size.y);
    }
}
