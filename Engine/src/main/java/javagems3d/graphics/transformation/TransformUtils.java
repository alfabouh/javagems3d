package javagems3d.graphics.transformation;

import javagems3d.system.resources.assets.models.pose.Pose2D;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.resources.assets.models.pose.Pose3D;

import java.util.ArrayList;
import java.util.List;

public abstract class TransformUtils {
    public static Matrix4f getOrthographic2DMatrix(float left, float right, float bottom, float top) {
        return new Matrix4f().identity().setOrtho2D(left, right, bottom, top);
    }

    public static Matrix4f getOrthographic3DMatrix(float left, float right, float bottom, float top, float zNear, float zFar, boolean zZeroToOne) {
        return new Matrix4f().identity().setOrtho(left, right, bottom, top, zNear, zFar, zZeroToOne);
    }

    public static Matrix4f getPerspectiveMatrix(IWindow window, float fov, float zNear, float zFar) {
        return new Matrix4f().identity().perspective(fov, window.getWindowSize().x / (float) window.getWindowSize().y, zNear, zFar);
    }

    public static Matrix4f getPerspectiveMatrix(float ratio, float fov, float zNear, float zFar) {
        return new Matrix4f().identity().perspective(fov, ratio, zNear, zFar);
    }

    public static Matrix4f getViewMatrix(ICamera camera) {
        Vector3f cameraPos = camera.getCamPosition();
        Vector3f cameraRot = camera.getCamRotation();
        Matrix4f matrix4f = new Matrix4f().identity();
        if (camera.getLookAtPosition() != null) {
            Vector3f lookUp;
            float threshold = (1.0f - 1.0e-6f);
            Vector3f nPos = new Vector3f(cameraPos).normalize();
            Vector3f vY = new Vector3f(0.0f, 1.0f, 0.0f);
            Vector3f vX = new Vector3f(1.0f, 0.0f, 0.0f);
            Vector3f vZ = new Vector3f(0.0f, 0.0f, 1.0f);
            if (nPos.dot(vY) < threshold) {
                lookUp = vY;
            } else if (nPos.dot(vX) < threshold) {
                lookUp = vX;
            } else {
                lookUp = vZ;
            }
            matrix4f.lookAt(cameraPos, camera.getLookAtPosition(), lookUp);
        } else {
            matrix4f.rotateXYZ(cameraRot.x, cameraRot.y, cameraRot.z);
        }
        return matrix4f.translate(new Vector3f(cameraPos).negate());
    }

    public static Matrix4f getModelMatrix(Pose3D pose) {
        Vector3f rotation = pose.getRotation();
        return new Matrix4f().identity().translate(pose.getPosition()).rotateXYZ(-rotation.x, -rotation.y, -rotation.z).scale(pose.getScaling());
    }

    public static Matrix4f getModelViewMatrix(Pose3D pose, Matrix4f viewMatrix) {
        if (pose.isOrientedToViewMatrix()) {
            return TransformUtils.getOrientedToViewModelViewMatrix(pose, viewMatrix);
        }
        return new Matrix4f(viewMatrix).mul(TransformUtils.getModelMatrix(pose));
    }

    public static Matrix4f getOrientedToViewModelViewMatrix(Pose3D pose, Matrix4f viewMatrix) {
        Matrix4f m1 = TransformUtils.getModelMatrix(pose);
        viewMatrix.transpose3x3(m1);
        return new Matrix4f(viewMatrix).mul(m1);
    }

    public static Matrix4f getOrientedToViewModelMatrix(Pose3D pose, Matrix4f viewMatrix) {
        Matrix4f m1 = TransformUtils.getModelMatrix(pose);
        Vector3f scaling = new Vector3f();
        m1.getScale(scaling);
        return viewMatrix.transpose3x3(m1).scale(scaling);
    }

    public static Matrix4f getModelOrthographicMatrix(Pose2D pose, Matrix4f orthographicMatrix) {
        return new Matrix4f(orthographicMatrix).mul(new Matrix4f().identity().translate(new Vector3f(pose.getPosition(), 0.0f)).rotateZ(-pose.getRotation()).scaleXY(pose.getScale().x, pose.getScale().y));
    }

    public static Matrix4f getLookAtMatrix(Vector3f eye, Vector3f up, Vector3f destination) {
        return new Matrix4f().identity().setLookAt(eye, destination, up);
    }

    public static List<Matrix4f> getAllDirectionViewSpaces(Vector3f pos, float near, float far) {
        List<Matrix4f> directions = new ArrayList<>();
        Matrix4f perspective = new Matrix4f().perspective((float) Math.toRadians(90.0f), 1.0f, near, far);

        Matrix4f projectionViewMatrix1 = new Matrix4f(perspective).mul(TransformUtils.getLookAtMatrix(pos, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(pos).add(1.0f, 0.0f, 0.0f)));
        Matrix4f projectionViewMatrix2 = new Matrix4f(perspective).mul(TransformUtils.getLookAtMatrix(pos, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(pos).add(-1.0f, 0.0f, 0.0f)));
        Matrix4f projectionViewMatrix3 = new Matrix4f(perspective).mul(TransformUtils.getLookAtMatrix(pos, new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(pos).add(0.0f, 1.0f, 0.0f)));
        Matrix4f projectionViewMatrix4 = new Matrix4f(perspective).mul(TransformUtils.getLookAtMatrix(pos, new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(pos).add(0.0f, -1.0f, 0.0f)));
        Matrix4f projectionViewMatrix5 = new Matrix4f(perspective).mul(TransformUtils.getLookAtMatrix(pos, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(pos).add(0.0f, 0.0f, 1.0f)));
        Matrix4f projectionViewMatrix6 = new Matrix4f(perspective).mul(TransformUtils.getLookAtMatrix(pos, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(pos).add(0.0f, 0.0f, -1.0f)));

        directions.add(projectionViewMatrix1);
        directions.add(projectionViewMatrix2);
        directions.add(projectionViewMatrix3);
        directions.add(projectionViewMatrix4);
        directions.add(projectionViewMatrix5);
        directions.add(projectionViewMatrix6);
        return directions;
    }
}
