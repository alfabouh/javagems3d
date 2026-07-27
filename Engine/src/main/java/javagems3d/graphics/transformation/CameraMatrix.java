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

package javagems3d.graphics.transformation;

import org.joml.Matrix4f;
import javagems3d.graphics.camera.base.ICamera;

public final class CameraMatrix {
    private final Matrix4f viewMatrix;

    public CameraMatrix() {
        this.viewMatrix = new Matrix4f().identity();
    }

    public void update(ICamera camera) {
        this.viewMatrix.set(JGemsTransformManager.getAbstractCameraViewMatrix(camera));
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f(this.viewMatrix);
    }
}