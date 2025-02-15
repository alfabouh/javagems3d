/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.camera;

import javagems3d.graphics.camera.base.CameraBase;
import javagems3d.graphics.objects.entities.SceneEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.JGemsHelper;
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
        if (this.getAttachedObject() != null && this.getAttachedObject().getWorldItem() instanceof IPlayer) {
            IPlayer entityPlayerSP = (IPlayer) this.getAttachedObject().getWorldItem();
            vector3f.add(0, entityPlayerSP.getEyeHeight(), 0);
        }
        return vector3f;
    }

    public void attachCameraOnItem(SceneEntity abstractSceneEntity) {
        JGemsHelper.getLogger().debug("Attached camera to: " + abstractSceneEntity.getWorldItem().getItemName());
        this.abstractSceneEntity = abstractSceneEntity;
        this.setCameraPosition(abstractSceneEntity.getRenderPosition());
        this.setCameraRotation(abstractSceneEntity.getRenderRotation());
    }

    public SceneEntity getAttachedObject() {
        return this.abstractSceneEntity;
    }
}
