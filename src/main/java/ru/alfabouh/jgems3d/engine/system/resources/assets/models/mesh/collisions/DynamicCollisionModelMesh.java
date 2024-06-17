package ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.collisions;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.bytedeco.bullet.global.BulletCollision;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;

import java.util.List;

@SuppressWarnings("all")
public class DynamicCollisionModelMesh {
    private final MeshDataGroup meshDataGroup;
    private btTriangleMesh triangleMesh;
    private btCollisionShape btCollisionShape;
    private btTriangleInfoMap btTriangleInfoMap;
    private btConcaveShape meshShape;

    public DynamicCollisionModelMesh(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    public void constructCollisionMeshForDynamicObject() {
        this.triangleMesh = new btTriangleMesh(true, true);
        this.triangleMesh.m_weldingThreshold(0.001f);
        for (ModelNode modelNode : this.meshDataGroup.getModelNodeList()) {
            List<Float> floats = modelNode.getMesh().getAttributePositions();
            for (int i = 0; i < modelNode.getMesh().getTotalVertices(); i += 3) {
                int i1 = modelNode.getMesh().getIndexes().get(i) * 3;
                int i2 = modelNode.getMesh().getIndexes().get(i + 1) * 3;
                int i3 = modelNode.getMesh().getIndexes().get(i + 2) * 3;
                this.triangleMesh.addTriangle(new btVector3(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2)), new btVector3(floats.get(i2), floats.get(i2 + 1), floats.get(i2 + 2)), new btVector3(floats.get(i3), floats.get(i3 + 1), floats.get(i3 + 2)), true);
            }
        }
        this.btTriangleInfoMap = new btTriangleInfoMap();
        this.meshShape = new btBvhTriangleMeshShape(this.triangleMesh, true, true);
        btBvhTriangleMeshShape bvhTriangleMeshShape = (btBvhTriangleMeshShape) this.meshShape;
        bvhTriangleMeshShape.recalcLocalAabb();
        BulletCollision.btGenerateInternalEdgeInfo(bvhTriangleMeshShape, btTriangleInfoMap);
        bvhTriangleMeshShape.setTriangleInfoMap(btTriangleInfoMap);
        this.btCollisionShape = meshShape;
    }

    public btCollisionShape getMeshShape() {
        return this.btCollisionShape;
    }
}
