package ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.collisions;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.LinearMath.btVector3;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;

import java.util.List;

@SuppressWarnings("all")
public class DynamicCollisionModelMesh {
    private final MeshDataGroup meshDataGroup;
    private btCollisionShape btCollisionShape;

    public DynamicCollisionModelMesh(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    public void constructCollisionMeshForDynamicObject() {
        btConvexHullShape btConvexHullShape = new btConvexHullShape();
        for (ModelNode modelNode : this.meshDataGroup.getModelNodeList()) {
            List<Float> floats = modelNode.getMesh().getAttributePositions();
            for (int i = 0; i < floats.size(); i += 3) {
                float i1 = floats.get(i);
                float i2 = floats.get(i + 1);
                float i3 = floats.get(i + 2);
                btConvexHullShape.addPoint(new btVector3(i1, i2, i3), true);
            }
        }
        btConvexHullShape.optimizeConvexHull();
        btConvexHullShape.setMargin(0.01f);
        this.btCollisionShape = btConvexHullShape;
    }

    public void clean() {
        this.btCollisionShape = null;
    }

    public btCollisionShape getMeshShape() {
        return this.btCollisionShape;
    }
}
