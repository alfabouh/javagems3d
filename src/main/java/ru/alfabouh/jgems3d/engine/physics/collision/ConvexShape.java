package ru.alfabouh.jgems3d.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletCollision.btConvexHullShape;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;

public class ConvexShape implements AbstractCollision {
    private final Vector3d[] points;
    private btConvexHullShape convexHullShape;

    public ConvexShape(Vector3d[] points) {
        this.points = points;
    }

    public Vector3d[] getPoints() {
        return this.points;
    }

    @Override
    public btCollisionShape buildCollisionShape(double scale) {
        this.convexHullShape = new btConvexHullShape();
        for (Vector3d vector3d : this.points) {
            convexHullShape.addPoint(new btVector3(vector3d.x, vector3d.y, vector3d.z));
        }
        convexHullShape.setLocalScaling(this.getScaling(scale));
        return convexHullShape;
    }
}
