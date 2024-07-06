package ru.alfabouh.jgems3d.engine.physics.collision.base;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3f;

public interface AbstractCollision {
    btCollisionShape buildCollisionShape(Vector3f scale);

    default btVector3 getScaling(Vector3f d1) {
        return new btVector3(d1.x, d1.y, d1.z);
    }
}
