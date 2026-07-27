/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
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

    public void addCameraPos(Vector3f vector3f) {
        super.setCameraPosition(this.getCamPosition().add(vector3f));
    }

    public void addCameraRot(Vector3f vector3f) {
        super.setCameraRotation(this.getCamRotation().add(vector3f));
    }
}
