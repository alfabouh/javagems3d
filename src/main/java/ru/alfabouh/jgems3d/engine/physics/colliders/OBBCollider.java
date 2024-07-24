package ru.alfabouh.jgems3d.engine.physics.colliders;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsUtils;

public class OBBCollider implements IColliderConstructor {
    private final Vector3f size;

    public OBBCollider(Vector3f size) {
        this.size = size;
    }

    @Override
    public CollisionShape createGeom(DynamicsSystem dynamicsSystem) {
        Vector3f vector3f = new Vector3f(this.size);
        return new BoxCollisionShape(DynamicsUtils.convertV3F_JME(vector3f).mult(0.5f));
    }
}
