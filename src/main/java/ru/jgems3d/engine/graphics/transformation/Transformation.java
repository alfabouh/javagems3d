package ru.jgems3d.engine.graphics.transformation;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.graphics.opengl.screen.window.IWindow;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;

import java.util.ArrayList;
import java.util.List;

public class Transformation {
    public static Matrix4f getOrthographic2DMatrix(float left, float right, float bottom, float top) {
        return new Matrix4f().identity().setOrtho2D(left, right, bottom, top);
    }

    public static Matrix4f getOrthographic3DMatrix(float left, float right, float bottom, float top, float zNear, float zFar, boolean zZeroToOne) {
        return new Matrix4f().identity().setOrtho(left, right, bottom, top, zNear, zFar, zZeroToOne);
    }

    public static Matrix4f getPerspectiveMatrix(IWindow window, float fov, float zNear, float zFar) {
        return new Matrix4f().identity().perspective(fov, window.getWindowDimensions().x / (float) window.getWindowDimensions().y, zNear, zFar);
    }

    public static Matrix4f getViewMatrix(ICamera camera) {
        Vector3f cameraPos = camera.getCamPosition();
        Vector3f cameraRot = camera.getCamRotation();
        return new Matrix4f().identity().rotateXYZ(cameraRot.x, cameraRot.y, cameraRot.z).translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }

    public static Matrix4f getModelMatrix(Format3D format) {
        Vector3f rotation = format.getRotation();
        return new Matrix4f().identity().translate(format.getPosition()).rotateXYZ(-rotation.x, -rotation.y, -rotation.z).scale(format.getScaling());
    }

    public static Matrix4f getModelViewMatrix(Format3D format3D, Matrix4f viewMatrix) {
        if (format3D.isOrientedToViewMatrix()) {
            return Transformation.getOrientedToViewModelViewMatrix(format3D, viewMatrix);
        }
        return new Matrix4f(viewMatrix).mul(Transformation.getModelMatrix(format3D));
    }

    public static Matrix4f getOrientedToViewModelViewMatrix(Format3D format3D, Matrix4f viewMatrix) {
        Matrix4f m1 = Transformation.getModelMatrix(format3D);
        viewMatrix.transpose3x3(m1);
        return new Matrix4f(viewMatrix).mul(m1);
    }

    public static Matrix4f getOrientedToViewModelMatrix(Format3D format3D, Matrix4f viewMatrix) {
        Matrix4f m1 = Transformation.getModelMatrix(format3D);
        Vector3f scaling = new Vector3f();
        m1.getScale(scaling);
        return viewMatrix.transpose3x3(m1).scale(scaling);
    }

    public static Matrix4f getModelOrthographicMatrix(Format2D format2D, Matrix4f orthographicMatrix) {
        return new Matrix4f(orthographicMatrix).mul(new Matrix4f().identity().translate(new Vector3f(format2D.getPosition(), 0.0f)).rotateZ(-format2D.getRotation()).scaleXY(format2D.getScale().x, format2D.getScale().y));
    }

    public static Matrix4f getLookAtMatrix(Vector3f eye, Vector3f up, Vector3f destination) {
        return new Matrix4f().identity().setLookAt(eye, destination, up);
    }

    public static List<Matrix4f> getAllDirectionViewSpaces(Vector3f pos, float near, float far) {
        List<Matrix4f> directions = new ArrayList<>();
        Matrix4f perspective = new Matrix4f().perspective((float) Math.toRadians(90.0f), 1.0f, near, far);

        Matrix4f projectionViewMatrix1 = new Matrix4f(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(pos).add(1.0f, 0.0f, 0.0f)));
        Matrix4f projectionViewMatrix2 = new Matrix4f(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(pos).add(-1.0f, 0.0f, 0.0f)));
        Matrix4f projectionViewMatrix3 = new Matrix4f(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(pos).add(0.0f, 1.0f, 0.0f)));
        Matrix4f projectionViewMatrix4 = new Matrix4f(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(pos).add(0.0f, -1.0f, 0.0f)));
        Matrix4f projectionViewMatrix5 = new Matrix4f(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(pos).add(0.0f, 0.0f, 1.0f)));
        Matrix4f projectionViewMatrix6 = new Matrix4f(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(pos).add(0.0f, 0.0f, -1.0f)));

        directions.add(projectionViewMatrix1);
        directions.add(projectionViewMatrix2);
        directions.add(projectionViewMatrix3);
        directions.add(projectionViewMatrix4);
        directions.add(projectionViewMatrix5);
        directions.add(projectionViewMatrix6);
        return directions;
    }
}
