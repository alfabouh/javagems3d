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
import javagems3d.graphics.camera.base.ICamera;
import logger.Log;
import org.joml.Vector3f;

public class FixedCamera extends CameraBase {
    public FixedCamera(ICamera camera) {
        super(camera);
        Log.get().trace("Created free camera at: " + camera.getCamPosition());
    }

    public FixedCamera(Vector3f pos, Vector3f rot) {
        super(pos, rot);
        Log.get().trace("Created free camera at: " + pos);
    }

    public void setCameraPosition(Vector3f vector3f) {
        super.setCameraPosition(vector3f);
    }

    public void setCameraRotation(Vector3f vector3f) {
        super.setCameraRotation(vector3f);
    }

    @Override
    public void updateCamera(float frameDeltaTime) {
        if (this.camRotation.x > Math.toRadians(90.0f)) {
            this.camRotation.x = (float) Math.toRadians(90.0f);
        }
        if (this.camRotation.x < -Math.toRadians(90.0f)) {
            this.camRotation.x = (float) -Math.toRadians(90.0f);
        }
    }

    public void addCameraPos(Vector3f vector3f) {
        super.setCameraPosition(this.getCamPosition().add(vector3f));
    }

    public void addCameraRot(Vector3f vector3f) {
        super.setCameraRotation(this.getCamRotation().add(vector3f));
    }
}
