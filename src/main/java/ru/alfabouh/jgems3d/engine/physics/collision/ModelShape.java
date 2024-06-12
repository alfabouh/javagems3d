package ru.alfabouh.jgems3d.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.proxy.exception.JGemsException;

public class ModelShape implements AbstractCollision {
    private final MeshDataGroup meshDataGroup;
    private btCollisionShape btCollisionShape;

    public ModelShape(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public btCollisionShape buildCollisionShape(double scale) {
        if (this.meshDataGroup.getCollisionShape() == null || this.meshDataGroup.getCollisionShape().isNull()) {
            throw new JGemsException("MeshDataGroup doesn't keep collision data!");
        }
        this.btCollisionShape = this.meshDataGroup.getCollisionShape();
        btCollisionShape.setLocalScaling(this.getScaling(scale));
        return btCollisionShape;
    }
}