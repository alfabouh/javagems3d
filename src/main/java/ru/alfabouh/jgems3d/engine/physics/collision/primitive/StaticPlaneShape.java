package ru.alfabouh.jgems3d.engine.physics.collision.primitive;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletCollision.btStaticPlaneShape;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;

public class StaticPlaneShape implements AbstractCollision {
    private final Vector3f normal;
    private btCollisionShape btCollisionShape;

    public StaticPlaneShape(Vector3f normal) {
        this.normal = normal;
    }

    @Override
    public btCollisionShape buildCollisionShape(Vector3f scale) {
        this.btCollisionShape = new btStaticPlaneShape(new btVector3(normal.x, normal.y, normal.z), 0.0f);
        btCollisionShape.setLocalScaling(this.getScaling(scale));
        return btCollisionShape;
    }
}
