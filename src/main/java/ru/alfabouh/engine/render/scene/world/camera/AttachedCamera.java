package ru.alfabouh.engine.render.scene.world.camera;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.physics.entities.player.IPlayer;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.scene.objects.items.PhysicsObject;

public class AttachedCamera extends Camera {
    private PhysicsObject physicsObject;

    public AttachedCamera(@NotNull PhysicsObject physicsObject) {
        this.physicsObject = physicsObject;
    }

    public AttachedCamera(WorldItem worldItem) {
        this.attachCameraToItem(worldItem);
    }

    public Vector3d getCamPosition() {
        return super.getCamPosition();
    }

    @Override
    public void updateCamera(double partialTicks) {
        PhysicsObject physicsObject = this.getPhysXObject();
        if (physicsObject != null) {
            Vector3d pos = new Vector3d(this.getPhysXObject().getRenderPosition()).add(this.cameraOffset());
            Vector3d rot = new Vector3d(this.getPhysXObject().getRenderRotation());
            this.setCameraPos(pos);
            this.setCameraRot(rot);
        }
    }

    private Vector3d cameraOffset() {
        Vector3d vector3d = new Vector3d(0.0d);
        if (this.getPhysXObject() != null && this.getPhysXObject().getWorldItem() instanceof IPlayer) {
            IPlayer entityPlayerSP = (IPlayer) this.getPhysXObject().getWorldItem();
            vector3d.add(0, entityPlayerSP.getEyeHeight(), 0);
        }
        return vector3d;
    }

    public void attachCameraToItem(WorldItem worldItem) {
        if (!worldItem.tryAttachRenderCamera(this)) {
            Game.getGame().getLogManager().warn("Unable to attach camera to " + worldItem.getItemName());
        }
    }

    public void attachCameraToItem(PhysicsObject physicsObject) {
        Game.getGame().getLogManager().log("Attached camera to: " + physicsObject.getWorldItem().getItemName());
        this.physicsObject = physicsObject;
    }

    public PhysicsObject getPhysXObject() {
        return this.physicsObject;
    }
}
