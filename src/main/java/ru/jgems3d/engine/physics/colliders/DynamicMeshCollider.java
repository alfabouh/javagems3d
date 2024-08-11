package ru.jgems3d.engine.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.collision.MeshCollisionData;
import ru.jgems3d.engine.system.service.exceptions.JGemsNullException;

public class DynamicMeshCollider implements IColliderConstructor {
    private final MeshDataGroup meshDataGroup;

    public DynamicMeshCollider(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public CollisionShape createGeom(DynamicsSystem dynamicsSystem) {
        MeshCollisionData meshCollisionData = this.meshDataGroup.getMeshDataContainer(MeshCollisionData.class);
        if (meshCollisionData == null) {
            throw new JGemsNullException("Couldn't get mesh collision data! " + this.meshDataGroup);
        }
        HullCollisionShape hullCollisionShape = new HullCollisionShape(meshCollisionData.getAllPositions());
        hullCollisionShape.setMargin(0.01f);
        return hullCollisionShape;
    }
}