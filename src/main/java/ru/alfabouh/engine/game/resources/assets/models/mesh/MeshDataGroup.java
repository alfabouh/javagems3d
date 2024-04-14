package ru.alfabouh.engine.game.resources.assets.models.mesh;

import org.bytedeco.bullet.BulletCollision.btBvhTriangleMeshShape;
import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletCollision.btTriangleInfoMap;
import org.bytedeco.bullet.BulletCollision.btTriangleMesh;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.bytedeco.bullet.global.BulletCollision;
import ru.alfabouh.engine.game.resources.cache.GameCache;
import ru.alfabouh.engine.game.resources.cache.ICached;

import java.util.ArrayList;
import java.util.List;

public class MeshDataGroup implements ICached {
    private final List<ModelNode> modelNodeList;
    private btTriangleMesh triangleMesh;
    private btCollisionShape btCollisionShape;
    private btTriangleInfoMap btTriangleInfoMap;
    private btBvhTriangleMeshShape btBvhTriangleMeshShape;

    public MeshDataGroup() {
        this.modelNodeList = new ArrayList<>();
        this.triangleMesh = null;
        this.btCollisionShape = null;
    }

    public MeshDataGroup(ModelNode modelNode) {
        this.modelNodeList = new ArrayList<>();
        this.putNode(modelNode);
    }

    public MeshDataGroup(Mesh mesh) {
        this(new ModelNode(mesh));
    }

    public void constructCollisionMesh() {
        this.triangleMesh = new btTriangleMesh(true, true);
        this.triangleMesh.m_weldingThreshold(0.001f);
        for (ModelNode modelNode : this.getModelNodeList()) {
            List<Float> floats = modelNode.getMesh().getAttributePositions();
            for (int i = 0; i < modelNode.getMesh().getTotalVertices(); i += 3) {
                int i1 = modelNode.getMesh().getIndexes().get(i) * 3;
                int i2 = modelNode.getMesh().getIndexes().get(i + 1) * 3;
                int i3 = modelNode.getMesh().getIndexes().get(i + 2) * 3;
                this.triangleMesh.addTriangle(new btVector3(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2)), new btVector3(floats.get(i2), floats.get(i2 + 1), floats.get(i2 + 2)), new btVector3(floats.get(i3), floats.get(i3 + 1), floats.get(i3 + 2)), true);
            }
        }
        this.btTriangleInfoMap = new btTriangleInfoMap();
        this.btBvhTriangleMeshShape = new btBvhTriangleMeshShape(this.triangleMesh, true, true);
        btBvhTriangleMeshShape.recalcLocalAabb();
        BulletCollision.btGenerateInternalEdgeInfo(btBvhTriangleMeshShape, btTriangleInfoMap);
        btBvhTriangleMeshShape.setTriangleInfoMap(btTriangleInfoMap);
        this.btCollisionShape = btBvhTriangleMeshShape;
    }

    public org.bytedeco.bullet.BulletCollision.btCollisionShape getCollisionShape() {
        return this.btCollisionShape;
    }

    public void putNode(ModelNode modelNode) {
        this.modelNodeList.add(modelNode);
    }

    public List<ModelNode> getModelNodeList() {
        return this.modelNodeList;
    }

    @Override
    public void onCleaningCache(GameCache gameCache) {
        this.getModelNodeList().forEach(ModelNode::cleanMesh);
        this.getModelNodeList().clear();
    }
}
