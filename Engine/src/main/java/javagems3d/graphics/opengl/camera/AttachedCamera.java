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

package javagems3d.graphics.opengl.camera;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import javagems3d.physics.entities.kinematic.player.IPlayer;

public class AttachedCamera extends Camera {
    private AbstractSceneEntity abstractSceneEntity;

    public AttachedCamera(@NotNull AbstractSceneEntity abstractSceneEntity) {
        this.attachCameraOnItem(abstractSceneEntity);
    }

    public Vector3f getCamPosition() {
        return super.getCamPosition();
    }

    @Override
    public void updateCamera(float frameDeltaTicks) {
        AbstractSceneEntity abstractSceneEntity = this.getPhysXObject();
        if (abstractSceneEntity != null) {
            Vector3f pos = new Vector3f(this.getPhysXObject().getRenderPosition()).add(this.cameraOffset());
            Vector3f rot = new Vector3f(this.getPhysXObject().getRenderRotation());
            this.setCameraPosition(pos);
            this.setCameraRotation(rot);
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
        JGemsHelper.getLogger().log("Attached camera to: " + abstractSceneEntity.getWorldItem().getItemName());
        this.abstractSceneEntity = abstractSceneEntity;
        this.setCameraPosition(abstractSceneEntity.getRenderPosition());
        this.setCameraRotation(abstractSceneEntity.getRenderRotation());
    }

    public AbstractSceneEntity getPhysXObject() {
        return this.abstractSceneEntity;
    }
}
