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