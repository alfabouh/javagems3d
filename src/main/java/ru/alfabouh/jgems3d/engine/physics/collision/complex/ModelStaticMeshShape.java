package ru.alfabouh.jgems3d.engine.physics.collision.complex;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;

@SuppressWarnings("all")
public class ModelStaticMeshShape implements AbstractCollision {
    private final MeshDataGroup meshDataGroup;
    private btCollisionShape btCollisionShape;

    public ModelStaticMeshShape(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public btCollisionShape buildCollisionShape(Vector3f scale) {
        if (this.meshDataGroup.getStaticMesh() == null || this.meshDataGroup.getStaticMesh().isNull()) {
            throw new JGemsException("MeshDataGroup doesn't keep collision data!");
        }
        this.btCollisionShape = this.meshDataGroup.getStaticMesh();
        btCollisionShape.setLocalScaling(this.getScaling(scale));
        return btCollisionShape;
    }
}