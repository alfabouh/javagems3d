package ru.alfabouh.jgems3d.toolbox.render.scene.items.collision;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.checkerframework.checker.units.qual.A;
import org.joml.*;
import ru.alfabouh.jgems3d.engine.math.Triple;
import ru.alfabouh.jgems3d.engine.render.transformation.Transformation;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;

import java.util.*;

public final class LocalCollision {
    private final MeshDataGroup meshDataGroup;
    private AABB aabb;

    public LocalCollision(Model<Format3D> model) {
        this.meshDataGroup = model.getMeshDataGroup();
        this.calcAABB(model.getFormat());
    }

    public void calcAABB(Format3D format3D) {
        Matrix4d modelMatrix = Transformation.getModelMatrix(format3D);

        Vector3d min = new Vector3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        Vector3d max = new Vector3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        for (ModelNode modelNode : this.getMeshDataGroup().getModelNodeList()) {
            List<Float> positions = modelNode.getMesh().getAttributePositions();
            List<Integer> indices = modelNode.getMesh().getIndexes();

            for (int index : indices) {
                int i1 = index * 3;
                Vector4d vector4d = new Vector4d(positions.get(i1), positions.get(i1 + 1), positions.get(i1 + 2), 1.0d).mul(modelMatrix);
                Vector3d vector3d = new Vector3d(vector4d.x, vector4d.y, vector4d.z);

                min.min(vector3d);
                max.max(vector3d);
            }
        }

        this.aabb = new AABB(min, max);
    }

    public Vector3d findClosesPointRayIntersectObjectMesh(Format3D format3D, Vector3d rayStart, Vector3d rayEnd) {
        Matrix4d modelMatrix = Transformation.getModelMatrix(format3D);

        Vector3d closestVector = null;
        for (ModelNode modelNode : this.getMeshDataGroup().getModelNodeList()) {
            List<Float> floats = modelNode.getMesh().getAttributePositions();
            for (int i = 0; i < modelNode.getMesh().getTotalVertices(); i += 3) {
                int i1 = modelNode.getMesh().getIndexes().get(i) * 3;
                int i2 = modelNode.getMesh().getIndexes().get(i + 1) * 3;
                int i3 = modelNode.getMesh().getIndexes().get(i + 2) * 3;
                Vector4d vector4d1 = new Vector4d(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0d).mul(modelMatrix);
                Vector4d vector4d2 = new Vector4d(floats.get(i2), floats.get(i2 + 1), floats.get(i2 + 2), 1.0d).mul(modelMatrix);
                Vector4d vector4d3 = new Vector4d(floats.get(i3), floats.get(i3 + 1), floats.get(i3 + 2), 1.0d).mul(modelMatrix);

                Vector3d vertex1 = new Vector3d(vector4d1.x, vector4d1.y, vector4d1.z);
                Vector3d vertex2 = new Vector3d(vector4d2.x, vector4d2.y, vector4d2.z);
                Vector3d vertex3 = new Vector3d(vector4d3.x, vector4d3.y, vector4d3.z);

                double d = Intersectiond.intersectRayTriangle(rayStart, rayEnd, vertex1, vertex2, vertex3, 1.0e-12f);
                if (d > 0.0d) {
                    Vector3d vector3d = new Vector3d(rayStart).add(new Vector3d(rayEnd).mul(d));
                    if (closestVector == null || rayStart.distance(vector3d) < rayStart.distance(closestVector)) {
                        closestVector = vector3d;
                    }
                }
            }
        }
        return closestVector;
    }

//969FE1C0623D4457
    public boolean isRayIntersectObjectMesh(Format3D format3D, Vector3d rayStart, Vector3d rayEnd) {
        Matrix4d modelMatrix = Transformation.getModelMatrix(format3D);

        for (ModelNode modelNode : this.getMeshDataGroup().getModelNodeList()) {
            List<Float> floats = modelNode.getMesh().getAttributePositions();
            for (int i = 0; i < modelNode.getMesh().getTotalVertices(); i += 3) {
                int i1 = modelNode.getMesh().getIndexes().get(i) * 3;
                int i2 = modelNode.getMesh().getIndexes().get(i + 1) * 3;
                int i3 = modelNode.getMesh().getIndexes().get(i + 2) * 3;
                Vector4d vector4d1 = new Vector4d(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0d).mul(modelMatrix);
                Vector4d vector4d2 = new Vector4d(floats.get(i2), floats.get(i2 + 1), floats.get(i2 + 2), 1.0d).mul(modelMatrix);
                Vector4d vector4d3 = new Vector4d(floats.get(i3), floats.get(i3 + 1), floats.get(i3 + 2), 1.0d).mul(modelMatrix);

                Vector3d vertex1 = new Vector3d(vector4d1.x, vector4d1.y, vector4d1.z);
                Vector3d vertex2 = new Vector3d(vector4d2.x, vector4d2.y, vector4d2.z);
                Vector3d vertex3 = new Vector3d(vector4d3.x, vector4d3.y, vector4d3.z);

                if (Intersectiond.testRayTriangle(rayStart, rayEnd, vertex1, vertex2, vertex3, 1.0e-5f)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRayIntersectObjectAABB(Vector3d rayStart, Vector3d rayEnd) {
        return Intersectiond.testRayAab(rayStart, rayEnd, this.getAabb().getMin(), this.getAabb().getMax());
    }

    public AABB getAabb() {
        return this.aabb;
    }

    public MeshDataGroup getMeshDataGroup() {
        return this.meshDataGroup;
    }

    public static class AABB {
        private final Vector3d min;
        private final Vector3d max;

        public AABB(Vector3d min, Vector3d max) {
            this.min = min;
            this.max = max;
        }

        public Vector3d getMax() {
            return this.max;
        }

        public Vector3d getMin() {
            return this.min;
        }
    }
}
