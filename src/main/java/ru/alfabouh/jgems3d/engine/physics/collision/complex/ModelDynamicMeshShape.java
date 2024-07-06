package ru.alfabouh.jgems3d.engine.physics.collision.complex;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

@SuppressWarnings("all")
public class ModelDynamicMeshShape implements AbstractCollision {
    private final MeshDataGroup meshDataGroup;
    private btCollisionShape btCollisionShape;

    public ModelDynamicMeshShape(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public btCollisionShape buildCollisionShape(Vector3f scale) {
        if (this.meshDataGroup.getDynamicMesh() == null || this.meshDataGroup.getDynamicMesh().isNull()) {
            throw new JGemsException("MeshDataGroup doesn't keep collision data!");
        }
        this.btCollisionShape = this.meshDataGroup.getDynamicMesh();
        btCollisionShape.setLocalScaling(this.getScaling(scale));
        return btCollisionShape;
    }
}