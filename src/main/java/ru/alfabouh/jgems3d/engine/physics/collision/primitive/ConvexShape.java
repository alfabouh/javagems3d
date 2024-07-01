package ru.alfabouh.jgems3d.engine.physics.collision.primitive;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletCollision.btConvexHullShape;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;

public class ConvexShape implements AbstractCollision {
    private final Vector3f[] points;
    private btConvexHullShape convexHullShape;

    public ConvexShape(Vector3f[] points) {
        this.points = points;
    }

    public Vector3f[] getPoints() {
        return this.points;
    }

    @Override
    public btCollisionShape buildCollisionShape(Vector3f scale) {
        this.convexHullShape = new btConvexHullShape();
        for (Vector3f vector3f : this.points) {
            convexHullShape.addPoint(new btVector3(vector3f.x, vector3f.y, vector3f.z));
        }
        convexHullShape.setLocalScaling(this.getScaling(scale));
        return convexHullShape;
    }
}
