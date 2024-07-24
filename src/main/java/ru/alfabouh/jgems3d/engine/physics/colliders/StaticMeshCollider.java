package ru.alfabouh.jgems3d.engine.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.infos.CompoundMesh;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.data.MeshCollisionData;

import java.util.ArrayList;
import java.util.List;

public class StaticMeshCollider implements IColliderConstructor {
    private final MeshDataGroup meshDataGroup;

    public StaticMeshCollider(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public CollisionShape createGeom(DynamicsSystem dynamicsSystem) {
        MeshCollisionData meshCollisionData = this.meshDataGroup.getMeshDataContainer(MeshCollisionData.class);
        if (meshCollisionData == null) {
            throw new JGemsException("Couldn't get mesh collision data! " + this.meshDataGroup);
        }
        return new MeshCollisionShape(true, meshCollisionData.getCompoundMesh());
    }
}