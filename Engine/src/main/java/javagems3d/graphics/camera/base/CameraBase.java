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

package javagems3d.graphics.camera.base;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class CameraBase implements ICamera {
    protected final Vector3f camPosition;
    protected final Vector3f camRotation;
    protected Vector3f lookAt;

    public CameraBase(ICamera camera) {
        this.camPosition = new Vector3f(camera.getCamPosition());
        this.camRotation = new Vector3f(camera.getCamRotation());
        this.lookAt = null;
    }

    public CameraBase(Vector3f pos, Vector3f rot) {
        this.camPosition = new Vector3f(pos);
        this.camRotation = new Vector3f(rot);
    }

    public CameraBase() {
        this(new Vector3f(0.0f), new Vector3f(0.0f));
    }

    public void setCameraPosition(Vector3f vector3f) {
        this.camPosition.set(vector3f);
    }

    public void setCameraRotation(Vector3f vector3f) {
        this.camRotation.set(vector3f);
    }

    public void setLookAt(@Nullable Vector3f lookAt) {
        this.lookAt = lookAt;
    }

    @Override
    public @Nullable Vector3f getLookAtPosition() {
        return this.lookAt;
    }

    public Vector3f getCamPosition() {
        return new Vector3f(this.camPosition);
    }

    public Vector3f getCamRotation() {
        return new Vector3f(this.camRotation);
    }

    @Override
    public void updateCamera(float frameDeltaTicks) {
    }
}
