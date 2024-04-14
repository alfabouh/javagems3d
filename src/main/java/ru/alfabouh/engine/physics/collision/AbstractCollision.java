package ru.alfabouh.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.LinearMath.btVector3;

public interface AbstractCollision {
    btCollisionShape buildCollisionShape(double scale);

    default btVector3 getScaling(double d1) {
        return new btVector3(d1, d1, d1);
    }
}
