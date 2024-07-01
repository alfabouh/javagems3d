package ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.player.IPlayer;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items.PhysicsObject;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class AttachedCamera extends Camera {
    private PhysicsObject physicsObject;

    public AttachedCamera(@NotNull PhysicsObject physicsObject) {
        this.physicsObject = physicsObject;
    }

    public AttachedCamera(WorldItem worldItem) {
        this.attachCameraToItem(worldItem);
    }

    public Vector3f getCamPosition() {
        return super.getCamPosition();
    }

    @Override
    public void updateCamera(float partialTicks) {
        PhysicsObject physicsObject = this.getPhysXObject();
        if (physicsObject != null) {
            Vector3f pos = new Vector3f(this.getPhysXObject().getRenderPosition()).add(this.cameraOffset());
            Vector3f rot = new Vector3f(this.getPhysXObject().getRenderRotation());
            this.setCameraPos(pos);
            this.setCameraRot(rot);
        }
    }

    private Vector3f cameraOffset() {
        Vector3f vector3f = new Vector3f(0.0f);
        if (this.getPhysXObject() != null && this.getPhysXObject().getWorldItem() instanceof IPlayer) {
            IPlayer entityPlayerSP = (IPlayer) this.getPhysXObject().getWorldItem();
            vector3f.add(0, entityPlayerSP.getEyeHeight(), 0);
        }
        return vector3f;
    }

    public void attachCameraToItem(WorldItem worldItem) {
        if (!worldItem.tryAttachRenderCamera(this)) {
            SystemLogging.get().getLogManager().warn("Unable to attach camera to " + worldItem.getItemName());
        }
    }

    public void attachCameraToItem(PhysicsObject physicsObject) {
        SystemLogging.get().getLogManager().log("Attached camera to: " + physicsObject.getWorldItem().getItemName());
        this.physicsObject = physicsObject;
    }

    public PhysicsObject getPhysXObject() {
        return this.physicsObject;
    }
}
