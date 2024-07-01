package ru.alfabouh.jgems3d.engine.physics.collision.primitive;

import org.bytedeco.bullet.BulletCollision.btCapsuleShape;
import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;

public class CapsuleShape implements AbstractCollision {
    private final double size;
    private btCollisionShape shape;

    public CapsuleShape(double size) {
        this.size = size;
    }

    @Override
    public btCollisionShape buildCollisionShape(Vector3f scale) {
        this.shape = new btCapsuleShape(this.size / 2.0f, this.size);
        shape.setLocalScaling(this.getScaling(scale));
        return shape;
    }
}
