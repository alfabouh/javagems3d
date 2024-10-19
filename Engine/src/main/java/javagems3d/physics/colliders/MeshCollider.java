package javagems3d.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import javagems3d.system.resources.assets.models.mesh.MeshDataGroup;
import javagems3d.system.resources.assets.models.mesh.data.collision.MeshCollisionData;
import javagems3d.system.service.exceptions.JGemsNullException;

public class MeshCollider implements IColliderConstructor {
    private final MeshDataGroup meshDataGroup;
    private final boolean isBodyDynamic;

    public MeshCollider(MeshDataGroup meshDataGroup, boolean isBodyDynamic) {
        this.meshDataGroup = meshDataGroup;
        this.isBodyDynamic = isBodyDynamic;
    }

    public static IColliderConstructor getDynamic(MeshDataGroup meshDataGroup) {
        return new MeshCollider(meshDataGroup, true);
    }

    public static IColliderConstructor getStatic(MeshDataGroup meshDataGroup) {
        return new MeshCollider(meshDataGroup, false);
    }

    public static IColliderConstructor get(MeshDataGroup meshDataGroup, boolean isBodyDynamic) {
        return new MeshCollider(meshDataGroup, isBodyDynamic);
    }

    @Override
    public CollisionShape createCollisionShape() {
        MeshCollisionData meshCollisionData = this.meshDataGroup.getMeshUserData(MeshDataGroup.MESH_COLLISION_UD, MeshCollisionData.class);
        if (meshCollisionData == null) {
            throw new JGemsNullException("Couldn't get mesh collision collections! " + this.meshDataGroup);
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
