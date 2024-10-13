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
import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.screen.window.IWindow;

public class TransformationUtils {
    private final Vector3f projectionData;

    private final CameraTransformation cameraTransformation;
    private final Matrix4f perspectiveMatrix;
    private final Matrix4f orthographicMatrix;
    private final IWindow window;

    public TransformationUtils(IWindow window, float fov, float zNear, float zFar) {
        this.window = window;
        this.projectionData = new Vector3f(fov, zNear, zFar);
        this.cameraTransformation = new CameraTransformation();
        this.perspectiveMatrix = new Matrix4f().identity();
        this.orthographicMatrix = new Matrix4f().identity();
        this.updateMatrices();
    }

    public void updateCamera(ICamera camera) {
        this.getCameraTransformation().update(camera);
    }

    public void updateOrthographicMatrix() {
        this.orthographicMatrix.set(Transformation.getOrthographic2DMatrix(0, this.window.getWindowDimensions().x, this.window.getWindowDimensions().y, 0));
    }

    public void updatePerspectiveMatrix() {
        this.perspectiveMatrix.set(Transformation.getPerspectiveMatrix(this.window, this.projectionData.x, this.projectionData.y, this.projectionData.z));
    }

    public void updateMatrices() {
        this.updateOrthographicMatrix();
        this.updatePerspectiveMatrix();
    }

    public static Matrix4f getAbstractCameraViewMatrix(ICamera camera) {
        return Transformation.getViewMatrix(camera);
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

    public CameraTransformation getCameraTransformation() {
        return this.cameraTransformation;
    }
}
