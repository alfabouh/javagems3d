package javagems3d.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.service.exceptions.JGemsNullException;

public class MeshCollider implements IColliderConstructor {
    private final MeshGroup meshGroup;
    private final boolean isBodyDynamic;

    public MeshCollider(MeshGroup meshGroup, boolean isBodyDynamic) {
        this.meshGroup = meshGroup;
        this.isBodyDynamic = isBodyDynamic;
    }

    public static IColliderConstructor getDynamic(MeshGroup meshGroup) {
        return new MeshCollider(meshGroup, true);
    }

    public static IColliderConstructor getStatic(MeshGroup meshGroup) {
        return new MeshCollider(meshGroup, false);
    }

    public static IColliderConstructor get(MeshGroup meshGroup, boolean isBodyDynamic) {
        return new MeshCollider(meshGroup, isBodyDynamic);
    }

    @Override
    public CollisionShape createCollisionShape() {
        MeshCollisionData meshCollisionData = this.meshGroup.getMeshUserData(MeshGroup.MESH_COLLISION_UD, MeshCollisionData.class);
        if (meshCollisionData == null) {
            throw new JGemsNullException("Couldn't get mesh collision collections! " + this.meshGroup);
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
