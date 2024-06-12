package ru.alfabouh.jgems3d.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btBoxShape;
import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;

public class OBB implements AbstractCollision {
    private final Vector3d size;
    private btCollisionShape shape;

    public OBB(Vector3d size) {
        this.size = size;
    }

    public Vector3d getSize() {
        return new Vector3d(this.size);
    }

    @Override
    public btCollisionShape buildCollisionShape(double scale) {
        Vector3d vector3d = this.getSize();
        this.shape = new btBoxShape(new btVector3(vector3d.x / 2.0f, vector3d.y / 2.0f, vector3d.z / 2.0f));
        this.shape.setLocalScaling(this.getScaling(scale));
        return shape;
    }
}
