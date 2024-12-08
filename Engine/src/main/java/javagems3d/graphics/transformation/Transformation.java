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
import org.joml.Vector3f;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.screen.window.IWindow;

public class Transformation {
    private final Vector3f projectionData;

    private final CameraMatrix cameraMatrix;
    private final Matrix4f perspectiveMatrix;
    private final Matrix4f orthographicMatrix;
    private final IWindow window;

    public Transformation(IWindow window, float fov, float zNear, float zFar) {
        this.window = window;
        this.projectionData = new Vector3f(fov, zNear, zFar);
        this.cameraMatrix = new CameraMatrix();
        this.perspectiveMatrix = new Matrix4f().identity();
        this.orthographicMatrix = new Matrix4f().identity();
        this.updateMatrices();
    }

    public void updateCamera(ICamera camera) {
        this.getCameraTransformation().update(camera);
    }

    public void updateOrthographicMatrix() {
        this.orthographicMatrix.set(TransformationUtils.getOrthographic2DMatrix(0, this.window.getWindowSize().x, this.window.getWindowSize().y, 0));
    }

    public void updatePerspectiveMatrix() {
        this.perspectiveMatrix.set(TransformationUtils.getPerspectiveMatrix(this.window, this.projectionData.x, this.projectionData.y, this.projectionData.z));
    }

    public void updateMatrices() {
        this.updateOrthographicMatrix();
        this.updatePerspectiveMatrix();
    }

    public static Matrix4f getAbstractCameraViewMatrix(ICamera camera) {
        return TransformationUtils.getViewMatrix(camera);
    }

    public Matrix4f getMainCameraViewMatrix() {
        return this.getCameraTransformation().getViewMatrix();
    }

    public Matrix4f getOrthographicMatrix() {
        return new Matrix4f(this.orthographicMatrix);
    }

    public Matrix4f getPerspectiveMatrix() {
        return new Matrix4f(this.perspectiveMatrix);
    }

    public CameraMatrix getCameraTransformation() {
        return this.cameraMatrix;
    }
}
