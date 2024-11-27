package javagems3d.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.udata.MeshCollisionData;
import javagems3d.system.service.exceptions.JGemsNullException;

public class MeshCollider implements IColliderConstructor {
    private final MeshStructure<?> meshStructure;
    private final boolean isBodyDynamic;

    public MeshCollider(MeshStructure<?> meshStructure, boolean isBodyDynamic) {
        this.meshStructure = meshStructure;
        this.isBodyDynamic = isBodyDynamic;
    }

    public static IColliderConstructor getDynamic(MeshStructure<?> meshStructure) {
        return new MeshCollider(meshStructure, true);
    }

    public static IColliderConstructor getStatic(MeshStructure<?> meshStructure) {
        return new MeshCollider(meshStructure, false);
    }

    public static IColliderConstructor get(MeshStructure<?> meshStructure, boolean isBodyDynamic) {
        return new MeshCollider(meshStructure, isBodyDynamic);
    }

    @Override
    public CollisionShape execute() {
        MeshCollisionData meshCollisionData = this.meshStructure.getMeshUserData(MeshStructure.MESH_COLLISION_UD, MeshCollisionData.class);
        if (meshCollisionData == null) {
            throw new JGemsNullException("Couldn't get mesh collision collections! " + this.meshStructure);
        }
        CollisionShape collisionShape;
        if (this.isBodyDynamic) {
            collisionShape = new HullCollisionShape(meshCollisionData.getAllPositions());
        } else {
            collisionShape = new MeshCollisionShape(true, meshCollisionData.getCompoundMesh());
        }
        collisionShape.setMargin(this.margin());
        return collisionShape;
    }

    protected float margin() {
        return 0.005f;
    }
}
