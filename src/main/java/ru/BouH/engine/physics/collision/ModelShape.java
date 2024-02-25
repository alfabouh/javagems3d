package ru.BouH.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;

public class ModelShape implements AbstractCollision {
    private final MeshDataGroup meshDataGroup;

    public ModelShape(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public btCollisionShape buildCollisionShape(double scale) {
        if (this.meshDataGroup.getCollisionShape() == null || this.meshDataGroup.getCollisionShape().isNull()) {
            throw new GameException("MeshDataGroup doesn't keep collision data!");
        }
        btCollisionShape btCollisionShape = this.meshDataGroup.getCollisionShape();
        btCollisionShape.setLocalScaling(this.getScaling(scale));
        return btCollisionShape;
    }
}