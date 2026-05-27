package javagems3d.graphics.camera;

import javagems3d.graphics.camera.base.CameraBase;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.physics.entities.kinematic.player.IPlayer;

public class AttachedCamera extends CameraBase {
    private final Vector3f cameraPosOffset;
    private final Vector3f cameraRotOffset;
    private SceneEntity abstractSceneEntity;

    public AttachedCamera(@NotNull SceneEntity abstractSceneEntity) {
        this.attachCameraOnItem(abstractSceneEntity);
        this.cameraPosOffset = new Vector3f();
        this.cameraRotOffset = new Vector3f();
    }

    public Vector3f getCamPosition() {
        return super.getCamPosition();
    }

    @Override
    public void updateCamera(float frameDeltaTicks) {
        SceneEntity abstractSceneEntity = this.getAttachedObject();
        if (abstractSceneEntity != null) {
            Vector3f pos = new Vector3f(this.getAttachedObject().getRenderPosition()).add(this.cameraOffsetWithEye());
            Vector3f rot = new Vector3f(this.getAttachedObject().getRenderRotation().add(this.getCameraRotOffset()));
            this.setCameraPosition(pos);
            this.setCameraRotation(rot);
        }
    }

    private Vector3f cameraOffsetWithEye() {
        Vector3f vector3f = this.getCameraPosOffset();
        if (this.getAttachedObject() != null && this.getAttachedObject().getWorldItem() instanceof IPlayer entityPlayerSP) {
            vector3f.add(0, entityPlayerSP.getEyeHeight(), 0);
        }
        return vector3f;
    }

    public void attachCameraOnItem(SceneEntity abstractSceneEntity) {
        Log.get().debug("Attached camera to: " + abstractSceneEntity.getWorldItem().getItemName());
        this.abstractSceneEntity = abstractSceneEntity;
        this.setCameraPosition(abstractSceneEntity.getRenderPosition());
        this.setCameraRotation(abstractSceneEntity.getRenderRotation());
    }

    public AttachedCamera setCameraPosOffset(Vector3f cameraPosOffset) {
        this.cameraPosOffset.set(cameraPosOffset);
        return this;
    }

    public AttachedCamera setCameraRotOffset(Vector3f cameraRotOffset) {
        this.cameraRotOffset.set(cameraRotOffset);
        return this;
    }

    public Vector3f getCameraPosOffset() {
        return new Vector3f(this.cameraPosOffset);
    }

    public Vector3f getCameraRotOffset() {
        return new Vector3f(this.cameraRotOffset);
    }

    public SceneEntity getAttachedObject() {
        return this.abstractSceneEntity;
    }
}
