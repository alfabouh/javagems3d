package ru.BouH.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletCollision.btStaticPlaneShape;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;

public class StaticPlane implements AbstractCollision {
    private final Vector3d normal;

    public StaticPlane(Vector3d normal) {
        this.normal = normal;
    }

    @Override
    public btCollisionShape buildCollisionShape(double scale) {
        btCollisionShape btCollisionShape = new btStaticPlaneShape(new btVector3(normal.x, normal.y, normal.z), 0.0f);
        btCollisionShape.setLocalScaling(this.getScaling(scale));
        return btCollisionShape;
    }
}
