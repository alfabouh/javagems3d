package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.camera;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.player.IPlayer;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.AbstractSceneItemObject;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class AttachedCamera extends Camera {
    private AbstractSceneItemObject abstractSceneItemObject;

    public AttachedCamera(@NotNull AbstractSceneItemObject abstractSceneItemObject) {
        this.attachCameraToItem(abstractSceneItemObject);
    }

    public AttachedCamera(WorldItem worldItem, Vector3f startPos, Vector3f startRot) {
        this.attachCameraToItem(worldItem);
        this.setCameraPos(startPos);
        this.setCameraRot(startRot);
    }

    public Vector3f getCamPosition() {
        return super.getCamPosition();
    }

    @Override
    public void updateCamera(float partialTicks) {
        AbstractSceneItemObject abstractSceneItemObject = this.getPhysXObject();
        if (abstractSceneItemObject != null) {
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

    public void attachCameraToItem(AbstractSceneItemObject abstractSceneItemObject) {
        SystemLogging.get().getLogManager().log("Attached camera to: " + abstractSceneItemObject.getWorldItem().getItemName());
        this.abstractSceneItemObject = abstractSceneItemObject;
        this.setCameraPos(abstractSceneItemObject.getRenderPosition());
    }

    public AbstractSceneItemObject getPhysXObject() {
        return this.abstractSceneItemObject;
    }
}
