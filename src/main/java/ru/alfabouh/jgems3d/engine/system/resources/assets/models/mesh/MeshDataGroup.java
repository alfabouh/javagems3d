package ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.collisions.DynamicCollisionModelMesh;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.collisions.StaticCollisionModelMesh;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ICached;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;

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
        this.modelNodeList = new ArrayList<>();
        this.putNode(modelNode);
    }

    public MeshDataGroup(Mesh mesh) {
        this(new ModelNode(mesh));
    }

    public void constructDynamicMesh() {
        this.dynamicCollisionModelMesh = new DynamicCollisionModelMesh(this);
        this.dynamicCollisionModelMesh.constructCollisionMeshForDynamicObject();
    }

    public void constructStaticMesh() {
        this.staticCollisionModelMesh = new StaticCollisionModelMesh(this);
        this.staticCollisionModelMesh.constructCollisionMeshForStaticObject();
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
    public void onCleaningCache(ResourceCache ResourceCache) {
        this.getModelNodeList().forEach(ModelNode::cleanMesh);
        this.getModelNodeList().clear();
    }
}
