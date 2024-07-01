package ru.alfabouh.jgems3d.engine.physics.collision.primitive;

import org.bytedeco.bullet.BulletCollision.btBoxShape;
import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;

public class OBBShape implements AbstractCollision {
    private final Vector3f size;
    private btCollisionShape shape;

    public OBBShape(Vector3f size) {
        this.size = size;
    }

    public Vector3f getSize() {
        return new Vector3f(this.size);
    }

    @Override
    public btCollisionShape buildCollisionShape(Vector3f scale) {
        Vector3f vector3f = this.getSize();
        this.shape = new btBoxShape(new btVector3(vector3f.x / 2.0f, vector3f.y / 2.0f, vector3f.z / 2.0f));
        this.shape.setLocalScaling(this.getScaling(scale));
        return shape;
    }
}
