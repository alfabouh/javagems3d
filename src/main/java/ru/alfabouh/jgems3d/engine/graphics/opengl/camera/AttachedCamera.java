package ru.alfabouh.jgems3d.engine.graphics.opengl.camera;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.alfabouh.jgems3d.engine.physics.entities.player.IPlayer;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class AttachedCamera extends Camera {
    private AbstractSceneEntity abstractSceneEntity;

    public AttachedCamera(@NotNull AbstractSceneEntity abstractSceneEntity) {
        this.attachCameraOnItem(abstractSceneEntity);
    }

    public Vector3f getCamPosition() {
        return super.getCamPosition();
    }

    @Override
    public void updateCamera(float partialTicks) {
        AbstractSceneEntity abstractSceneEntity = this.getPhysXObject();
        if (abstractSceneEntity != null) {
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

    public void attachCameraOnItem(AbstractSceneEntity abstractSceneEntity) {
        SystemLogging.get().getLogManager().log("Attached camera to: " + abstractSceneEntity.getWorldItem().getItemName());
        this.abstractSceneEntity = abstractSceneEntity;
        this.setCameraPos(abstractSceneEntity.getRenderPosition());
        this.setCameraRot(abstractSceneEntity.getRenderRotation());
    }

    public AbstractSceneEntity getPhysXObject() {
        return this.abstractSceneEntity;
    }
}
