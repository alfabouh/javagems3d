package ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.collisions.DynamicCollisionModelMesh;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.collisions.StaticCollisionModelMesh;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ICached;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.ArrayList;
import java.util.List;

public class MeshDataGroup implements ICached {
    private final List<ModelNode> modelNodeList;
    private StaticCollisionModelMesh staticCollisionModelMesh;
    private DynamicCollisionModelMesh dynamicCollisionModelMesh;

    public MeshDataGroup() {
        this.modelNodeList = new ArrayList<>();
    }

    public MeshDataGroup(ModelNode modelNode) {
        this();
        this.putNode(modelNode);
    }

    public MeshDataGroup(Mesh mesh) {
        this(new ModelNode(mesh));
    }

    public float calcDistanceToMostFarPoint(Vector3f scaling) {
        float max = Float.MIN_VALUE;

        for (ModelNode modelNode : this.getModelNodeList()) {
            List<Float> floats = modelNode.getMesh().getAttributePositions();
            for (int i = 0; i < floats.size(); i += 3) {
                float i1 = floats.get(i);
                float i2 = floats.get(i + 1);
                float i3 = floats.get(i + 2);

                float scaledX = i1 * scaling.x;
                float scaledY = i2 * scaling.y;
                float scaledZ = i3 * scaling.z;

                float length = (float) Math.sqrt(scaledX * scaledX + scaledY * scaledY + scaledZ * scaledZ);

                if (length > max) {
                    max = length;
                }
            }
        }
        return max;
    }

    public void constructCollisionMesh(boolean cStatic, boolean cDynamic) {
        if (cStatic) {
            SystemLogging.get().getLogManager().log("Constructing static mesh...");
            this.staticCollisionModelMesh = new StaticCollisionModelMesh(this);
            this.staticCollisionModelMesh.constructCollisionMeshForStaticObject();
        }

        if (cDynamic) {
            SystemLogging.get().getLogManager().log("Constructing dynamic mesh...");
            this.dynamicCollisionModelMesh = new DynamicCollisionModelMesh(this);
            this.dynamicCollisionModelMesh.constructCollisionMeshForDynamicObject();
        }
    }

    public btCollisionShape getStaticMesh() {
        return this.staticCollisionModelMesh.getMeshShape();
    }

    public btCollisionShape getDynamicMesh() {
        return this.dynamicCollisionModelMesh.getMeshShape();
    }

    public void putNode(ModelNode modelNode) {
        this.modelNodeList.add(modelNode);
    }

    public List<ModelNode> getModelNodeList() {
        return this.modelNodeList;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        if (this.staticCollisionModelMesh != null) {
            this.staticCollisionModelMesh.clean();
        }

        if (this.dynamicCollisionModelMesh != null) {
            this.dynamicCollisionModelMesh.clean();
        }

        this.getModelNodeList().forEach(ModelNode::cleanMesh);
        this.getModelNodeList().clear();
    }
}
