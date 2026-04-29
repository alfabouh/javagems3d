package javagems3d.graphics.camera;

import javagems3d.graphics.camera.base.CameraBase;
import javagems3d.graphics.objects.entities.SceneEntity;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.physics.entities.kinematic.player.IPlayer;

public class AttachedCamera extends CameraBase {
    private SceneEntity abstractSceneEntity;

    public AttachedCamera(@NotNull SceneEntity abstractSceneEntity) {
        this.attachCameraOnItem(abstractSceneEntity);
    }

    public Vector3f getCamPosition() {
        return super.getCamPosition();
    }

    @Override
    public void updateCamera(float frameDeltaTicks) {
        SceneEntity abstractSceneEntity = this.getAttachedObject();
        if (abstractSceneEntity != null) {
            Vector3f pos = new Vector3f(this.getAttachedObject().getRenderPosition()).add(this.cameraOffset());
            Vector3f rot = new Vector3f(this.getAttachedObject().getRenderRotation());
            this.setCameraPosition(pos);
            this.setCameraRotation(rot);
        }
    }

    private Vector3f cameraOffset() {
        Vector3f vector3f = new Vector3f(0.0f);
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

    public SceneEntity getAttachedObject() {
        return this.abstractSceneEntity;
    }
}
