package ru.alfabouh.jgems3d.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;

@SuppressWarnings("all")
public class ModelStaticShape implements AbstractCollision {
    private final MeshDataGroup meshDataGroup;
    private btCollisionShape btCollisionShape;

    public ModelStaticShape(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public btCollisionShape buildCollisionShape(double scale) {
        if (this.meshDataGroup.getStaticMesh() == null || this.meshDataGroup.getStaticMesh().isNull()) {
            throw new JGemsException("MeshDataGroup doesn't keep collision data!");
        }
        this.btCollisionShape = this.meshDataGroup.getStaticMesh();
        btCollisionShape.setLocalScaling(this.getScaling(scale));
        return btCollisionShape;
    }
}