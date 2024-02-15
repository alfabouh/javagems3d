package ru.BouH.engine.game.resources.assets.models.mesh;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletCollision.btConvexHullShape;
import org.bytedeco.bullet.BulletCollision.btMultimaterialTriangleMeshShape;
import org.bytedeco.bullet.BulletCollision.btTriangleMesh;
import org.bytedeco.bullet.LinearMath.btVector3;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.game.resources.cache.ICached;

import java.util.ArrayList;
import java.util.List;

public class MeshDataGroup implements ICached {
    private final List<ModelNode> modelNodeList;
    private btTriangleMesh triangleMesh;
    private btCollisionShape btCollisionShape;

    public MeshDataGroup() {
        this.modelNodeList = new ArrayList<>();
        this.triangleMesh = null;
        this.btCollisionShape = null;
    }

    public void constructCollisionMesh(boolean detailed) {
        if (!detailed) {
            btConvexHullShape convexHullShape = new btConvexHullShape();
            for (ModelNode modelNode : this.getModelNodeList()) {
                List<Float> floats = modelNode.getMesh().getAttributePositions();
                for (int i1 : modelNode.getMesh().getIndexes()) {
                    convexHullShape.addPoint(new btVector3(floats.get(i1 * 3), floats.get(i1 * 3+ 1), floats.get(i1 * 3+ 2)), false);
                }
            }
            this.btCollisionShape = convexHullShape;
        } else {
            this.triangleMesh = new btTriangleMesh();
            for (ModelNode modelNode : this.getModelNodeList()) {
                List<Float> floats = modelNode.getMesh().getAttributePositions();
                for (int i = 0; i < modelNode.getMesh().getIndexes().size(); i += 3) {
                    int i1 = modelNode.getMesh().getIndexes().get(i) * 3;
                    int i2 = modelNode.getMesh().getIndexes().get(i + 1) * 3;
                    int i3 = modelNode.getMesh().getIndexes().get(i + 2) * 3;

                    triangleMesh.addTriangle(new btVector3(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2)), new btVector3(floats.get(i2), floats.get(i2 + 1), floats.get(i2 + 2)), new btVector3(floats.get(i3), floats.get(i3 + 1), floats.get(i3 + 2)), true);
                }
            }
            btMultimaterialTriangleMeshShape btBvhTriangleMeshShape = new btMultimaterialTriangleMeshShape(triangleMesh, true, true);
            btBvhTriangleMeshShape.recalcLocalAabb();
            this.btCollisionShape = btBvhTriangleMeshShape;
        }
    }

    public org.bytedeco.bullet.BulletCollision.btCollisionShape getCollisionShape() {
        return new btCollisionShape(this.btCollisionShape);
    }

    public MeshDataGroup(ModelNode modelNode) {
        this.modelNodeList = new ArrayList<>();
        this.putNode(modelNode);
    }

    public MeshDataGroup(Mesh mesh) {
        this(new ModelNode(mesh));
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
    }
}
