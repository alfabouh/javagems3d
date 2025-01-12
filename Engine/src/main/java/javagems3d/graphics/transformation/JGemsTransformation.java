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

public class JGemsTransformation {
    public static final JGemsTransformation INSTANCE = new JGemsTransformation(-1.0f, -10.0f, -1.0f);

    private final Vector3f projectionData;

    private final CameraMatrix cameraMatrix;
    private final Matrix4f perspectiveMatrix;
    private final Matrix4f orthographicMatrix;

    protected JGemsTransformation(float fov, float zNear, float zFar) {
        this.projectionData = new Vector3f(fov, zNear, zFar);
        this.cameraMatrix = new CameraMatrix();
        this.perspectiveMatrix = new Matrix4f().identity();
        this.orthographicMatrix = new Matrix4f().identity();
    }

    public void updateCamera(ICamera camera) {
        this.getCameraTransformation().update(camera);
    }

    public void updateSetOfMatrices(IWindow window) {
        this.updateOrthographicMatrix(window);
        this.updatePerspectiveMatrix(window);
    }

    public void updateOrthographicMatrix(IWindow window) {
        this.orthographicMatrix.set(TransformationUtils.getOrthographic2DMatrix(0, window.getWindowSize().x, window.getWindowSize().y, 0));
    }

    public void updatePerspectiveMatrix(IWindow window) {
        this.perspectiveMatrix.set(TransformationUtils.getPerspectiveMatrix(window, this.getProjectionData().x, this.getProjectionData().y, this.getProjectionData().z));
    }

    public void setProjectionData(IWindow window, float fov, float zNear, float zFar) {
        this.projectionData.set(fov, zNear, zFar);
    }

    public static Matrix4f getAbstractCameraViewMatrix(ICamera camera) {
        return TransformationUtils.getViewMatrix(camera);
    }

    public Vector3f getProjectionData() {
        return new Vector3f(this.projectionData);
    }

    public Matrix4f getCameraViewMatrix() {
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
