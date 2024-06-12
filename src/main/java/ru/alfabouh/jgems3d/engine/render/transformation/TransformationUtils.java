package ru.alfabouh.jgems3d.engine.render.transformation;

import org.joml.Matrix4d;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.ICamera;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;

public class TransformationUtils {
    private final Vector3f projectionData;

    private final CameraTransformation cameraTransformation;
    private final Matrix4d perspectiveMatrix;
    private final Matrix4d orthographicMatrix;
    private final IWindow window;

    public TransformationUtils(IWindow window, float fov, float zNear, float zFar) {
        this.window = window;
        this.projectionData = new Vector3f(fov, zNear, zFar);
        this.cameraTransformation = new CameraTransformation();
        this.perspectiveMatrix = new Matrix4d().identity();
        this.orthographicMatrix = new Matrix4d().identity();

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

    public Matrix4d getMainCameraViewMatrix() {
        return this.getCameraTransformation().getViewMatrix();
    }

    public Matrix4d getOrthographicMatrix() {
        return new Matrix4d(this.orthographicMatrix);
    }

    public Matrix4d getPerspectiveMatrix() {
        return new Matrix4d(this.perspectiveMatrix);
    }

    public CameraTransformation getCameraTransformation() {
        return this.cameraTransformation;
    }
}
