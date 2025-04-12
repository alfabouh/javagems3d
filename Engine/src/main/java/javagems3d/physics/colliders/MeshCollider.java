package javagems3d.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.service.exceptions.JGemsNullException;

public class MeshCollider implements IColliderConstructor {
    private final MeshStructure3D<?> meshStructure;
    private final boolean isBodyDynamic;

    public MeshCollider(MeshStructure3D<?> meshStructure, boolean isBodyDynamic) {
        this.meshStructure = meshStructure;
        this.isBodyDynamic = isBodyDynamic;
    }

    public static IColliderConstructor getDynamic(MeshStructure3D<?> meshStructure) {
        return new MeshCollider(meshStructure, true);
    }

    public static IColliderConstructor getStatic(MeshStructure3D<?> meshStructure) {
        return new MeshCollider(meshStructure, false);
    }

    public static IColliderConstructor get(MeshStructure3D<?> meshStructure, boolean isBodyDynamic) {
        return new MeshCollider(meshStructure, isBodyDynamic);
    }

    @Override
    public CollisionShape execute() {
        MeshCollisionData meshCollisionData = this.meshStructure.getMeshCollisionData();
        if (meshCollisionData == null) {
            throw new JGemsNullException("Couldn't get mesh collision collections! " + this.meshStructure);
        }
        CollisionShape collisionShape;
        if (this.isBodyDynamic) {
            collisionShape = meshCollisionData.getDynamicCollision();
        } else {
            collisionShape = meshCollisionData.getStaticCollision();
        }
        collisionShape.setMargin(this.margin());
        return collisionShape;
    }

    protected float margin() {
        return 0.005f;
    }
}
