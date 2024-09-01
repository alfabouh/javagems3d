/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package toolbox.render.scene.items.collision;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import javagems3d.engine.graphics.transformation.Transformation;
import javagems3d.engine.system.resources.assets.models.Model;
import javagems3d.engine.system.resources.assets.models.formats.Format3D;
import javagems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import javagems3d.engine.system.resources.assets.models.mesh.ModelNode;

import java.util.List;

public final class LocalCollision {
    private final MeshDataGroup meshDataGroup;
    private AABB aabb;

    public LocalCollision(Model<Format3D> model) {
        this.meshDataGroup = model.getMeshDataGroup();
        this.calcAABB(model.getFormat());
    }

    public void calcAABB(Format3D format3D) {
        Matrix4f modelMatrix = Transformation.getModelMatrix(format3D);

        Vector3f min = new Vector3f((float) Double.POSITIVE_INFINITY);
        Vector3f max = new Vector3f((float) Double.NEGATIVE_INFINITY);

        for (ModelNode modelNode : this.getMeshDataGroup().getModelNodeList()) {
            List<Float> positions = modelNode.getMesh().getAttributePositions();
            List<Integer> indices = modelNode.getMesh().getIndexes();

            for (int index : indices) {
                int i1 = index * 3;
                Vector4f Vector4f = new Vector4f(positions.get(i1), positions.get(i1 + 1), positions.get(i1 + 2), 1.0f).mul(modelMatrix);
                Vector3f vector3f = new Vector3f(Vector4f.x, Vector4f.y, Vector4f.z);

                min.min(vector3f);
                max.max(vector3f);
            }
        }

        this.aabb = new AABB(min, max);
    }

    public Vector3f findClosesPointRayIntersectObjectMesh(Format3D format3D, Vector3f rayStart, Vector3f rayEnd) {
        Matrix4f modelMatrix = Transformation.getModelMatrix(format3D);

        Vector3f closestVector = null;
        for (ModelNode modelNode : this.getMeshDataGroup().getModelNodeList()) {
            List<Float> floats = modelNode.getMesh().getAttributePositions();
            for (int i = 0; i < modelNode.getMesh().getTotalVertices(); i += 3) {
                int i1 = modelNode.getMesh().getIndexes().get(i) * 3;
                int i2 = modelNode.getMesh().getIndexes().get(i + 1) * 3;
                int i3 = modelNode.getMesh().getIndexes().get(i + 2) * 3;
                Vector4f Vector4f1 = new Vector4f(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0f).mul(modelMatrix);
                Vector4f Vector4f2 = new Vector4f(floats.get(i2), floats.get(i2 + 1), floats.get(i2 + 2), 1.0f).mul(modelMatrix);
                Vector4f Vector4f3 = new Vector4f(floats.get(i3), floats.get(i3 + 1), floats.get(i3 + 2), 1.0f).mul(modelMatrix);

                Vector3f vertex1 = new Vector3f(Vector4f1.x, Vector4f1.y, Vector4f1.z);
                Vector3f vertex2 = new Vector3f(Vector4f2.x, Vector4f2.y, Vector4f2.z);
                Vector3f vertex3 = new Vector3f(Vector4f3.x, Vector4f3.y, Vector4f3.z);

                float d = Intersectionf.intersectRayTriangleFront(rayStart, rayEnd, vertex1, vertex2, vertex3, 1.0e-12f);
                if (d > 0.0f) {
                    Vector3f vector3f = new Vector3f(rayStart).add(new Vector3f(rayEnd).mul(d));
                    if (closestVector == null || rayStart.distance(vector3f) < rayStart.distance(closestVector)) {
                        closestVector = vector3f;
                    }
                }
            }
        }
        return closestVector;
    }

    //969FE1C0623D4457
    public boolean isRayIntersectObjectMesh(Format3D format3D, Vector3f rayStart, Vector3f rayEnd) {
        Matrix4f modelMatrix = Transformation.getModelMatrix(format3D);

        for (ModelNode modelNode : this.getMeshDataGroup().getModelNodeList()) {
            List<Float> floats = modelNode.getMesh().getAttributePositions();
            for (int i = 0; i < modelNode.getMesh().getTotalVertices(); i += 3) {
                int i1 = modelNode.getMesh().getIndexes().get(i) * 3;
                int i2 = modelNode.getMesh().getIndexes().get(i + 1) * 3;
                int i3 = modelNode.getMesh().getIndexes().get(i + 2) * 3;
                Vector4f Vector4f1 = new Vector4f(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0f).mul(modelMatrix);
                Vector4f Vector4f2 = new Vector4f(floats.get(i2), floats.get(i2 + 1), floats.get(i2 + 2), 1.0f).mul(modelMatrix);
                Vector4f Vector4f3 = new Vector4f(floats.get(i3), floats.get(i3 + 1), floats.get(i3 + 2), 1.0f).mul(modelMatrix);

                Vector3f vertex1 = new Vector3f(Vector4f1.x, Vector4f1.y, Vector4f1.z);
                Vector3f vertex2 = new Vector3f(Vector4f2.x, Vector4f2.y, Vector4f2.z);
                Vector3f vertex3 = new Vector3f(Vector4f3.x, Vector4f3.y, Vector4f3.z);

                if (Intersectionf.testRayTriangle(rayStart, rayEnd, vertex1, vertex2, vertex3, 1.0e-5f)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRayIntersectObjectAABB(Vector3f rayStart, Vector3f rayEnd) {
        return Intersectionf.testRayAab(rayStart, rayEnd, this.getAabb().getMin(), this.getAabb().getMax());
    }

    public AABB getAabb() {
        return this.aabb;
    }

    public MeshDataGroup getMeshDataGroup() {
        return this.meshDataGroup;
    }

    public static class AABB {
        private final Vector3f min;
        private final Vector3f max;

        public AABB(Vector3f min, Vector3f max) {
            this.min = min;
            this.max = max;
        }

        public Vector3f getMax() {
            return this.max;
        }

        public Vector3f getMin() {
            return this.min;
        }
    }
}
