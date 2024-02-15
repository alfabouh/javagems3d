package ru.BouH.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;

public class ModelShape implements AbstractCollision {
    private final MeshDataGroup meshDataGroup;

    public ModelShape(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
        if (this.meshDataGroup.getCollisionShape() == null) {
            Game.getGame().getLogManager().error("MeshDataGroup doesn't keep collision data!");
        }
    }

    @Override
    public btCollisionShape buildCollisionShape(double scale) {
        btCollisionShape btCollisionShape = this.meshDataGroup.getCollisionShape();
        btCollisionShape.setLocalScaling(this.getScaling(scale));
        return btCollisionShape;
    }
}