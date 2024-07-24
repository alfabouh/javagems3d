package ru.alfabouh.jgems3d.engine.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.data.MeshCollisionData;

public class DynamicMeshCollider implements IColliderConstructor {
    private final MeshDataGroup meshDataGroup;

    public DynamicMeshCollider(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public CollisionShape createGeom(DynamicsSystem dynamicsSystem) {
        MeshCollisionData meshCollisionData = this.meshDataGroup.getMeshDataContainer(MeshCollisionData.class);
        if (meshCollisionData == null) {
            throw new JGemsException("Couldn't get mesh collision data! " + this.meshDataGroup);
        }
        HullCollisionShape hullCollisionShape = new HullCollisionShape(meshCollisionData.getAllPositions());
        hullCollisionShape.setMargin(0.01f);
        return hullCollisionShape;
    }
}