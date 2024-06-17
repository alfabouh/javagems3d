package ru.alfabouh.jgems3d.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;

@SuppressWarnings("all")
public class ModelDynamicShape implements AbstractCollision {
    private final MeshDataGroup meshDataGroup;
    private btCollisionShape btCollisionShape;

    public ModelDynamicShape(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public btCollisionShape buildCollisionShape(double scale) {
        if (this.meshDataGroup.getDynamicMesh() == null || this.meshDataGroup.getDynamicMesh().isNull()) {
            throw new JGemsException("MeshDataGroup doesn't keep collision data!");
        }
        this.btCollisionShape = this.meshDataGroup.getDynamicMesh();
        btCollisionShape.setLocalScaling(this.getScaling(scale));
        return btCollisionShape;
    }
}