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


import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.screen.window.IWindow;

public class JGemsTransformManager {
    public static final JGemsTransformManager INSTANCE = new JGemsTransformManager(-1.0f, -10.0f, -1.0f);

    private final Vector3f projectionData;

    private final CameraMatrix cameraMatrix;
    private final Matrix4f perspectiveMatrix;
    private final Matrix4f orthographicMatrix;

    protected JGemsTransformManager(float fov, float zNear, float zFar) {
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
        this.orthographicMatrix.set(TransformUtils.getOrthographic2DMatrix(0, window.getWindowSize().x, window.getWindowSize().y, 0));
    }

    public void updatePerspectiveMatrix(IWindow window) {
        this.perspectiveMatrix.set(TransformUtils.getPerspectiveMatrix(window, this.getProjectionData().x, this.getProjectionData().y, this.getProjectionData().z));
    }

    public void setProjectionData(IWindow window, float fov, float zNear, float zFar) {
        this.projectionData.set(fov, zNear, zFar);
    }

    public static Matrix4f getModelViewMatrix(Model3D model) {
        return TransformUtils.getModelViewMatrix(model.getPose(), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
    }

    public static Matrix4f getAbstractCameraViewMatrix(ICamera camera) {
        return TransformUtils.getViewMatrix(camera);
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
