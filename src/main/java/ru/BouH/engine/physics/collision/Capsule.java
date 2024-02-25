package ru.BouH.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCapsuleShape;
import org.bytedeco.bullet.BulletCollision.btCollisionShape;

public class Capsule implements AbstractCollision {
    private final double size;

    public Capsule(double size) {
        this.size = size;
    }

    @Override
    public btCollisionShape buildCollisionShape(double scale) {
        btCollisionShape shape = new btCapsuleShape(this.size / 2.0f, this.size);
        shape.setLocalScaling(this.getScaling(scale));
        return shape;
    }
}
