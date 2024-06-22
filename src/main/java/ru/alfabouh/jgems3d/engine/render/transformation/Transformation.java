package ru.alfabouh.jgems3d.engine.render.transformation;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.ICamera;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;

import java.util.ArrayList;
import java.util.List;

public class Transformation {
    public static Matrix4d getOrthographic2DMatrix(double left, double right, double bottom, double top) {
        return new Matrix4d().identity().setOrtho2D(left, right, bottom, top);
    }

    public static Matrix4d getOrthographic3DMatrix(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne) {
        return new Matrix4d().identity().setOrtho(left, right, bottom, top, zNear, zFar, zZeroToOne);
    }

    public static Matrix4d getPerspectiveMatrix(IWindow window, double fov, double zNear, double zFar) {
        return new Matrix4d().identity().perspective(fov, window.getWindowDimensions().x / (double) window.getWindowDimensions().y, zNear, zFar);
    }

    public static Matrix4d getViewMatrix(ICamera camera) {
        Vector3d cameraPos = camera.getCamPosition();
        Vector3d cameraRot = camera.getCamRotation();
        return new Matrix4d().identity().rotateXYZ(Math.toRadians(cameraRot.x), Math.toRadians(cameraRot.y), Math.toRadians(cameraRot.z)).translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }

    public static Matrix4d getModelMatrix(Format3D format) {
        Vector3d rotation = format.getRotation();
        return new Matrix4d().identity().translate(format.getPosition()).rotateXYZ(-rotation.x, -rotation.y, -rotation.z).scale(format.getScaling());
    }

    public static Matrix4d getModelViewMatrix(Format3D format3D, Matrix4d viewMatrix) {
        Vector3d rotation = format3D.getRotation();
        Matrix4d m1 = new Matrix4d().identity().translate(format3D.getPosition()).rotateXYZ(-rotation.x, -rotation.y, -rotation.z).scale(format3D.getScaling());
        if (format3D.isOrientedToViewMatrix()) {
            viewMatrix.transpose3x3(m1);
        }
        Matrix4d viewCurr = new Matrix4d(viewMatrix);
        return viewCurr.mul(m1);
    }

    public static Matrix4d getModelOrthographicMatrix(Format2D format2D, Matrix4d orthographicMatrix) {
        return new Matrix4d(orthographicMatrix).mul(new Matrix4d().identity().translate(new Vector3d(format2D.getPosition(), 0.0d)).rotateZ(-format2D.getRotation()).scaleXY(format2D.getScale().x, format2D.getScale().y));
    }

    public static Matrix4d getLookAtMatrix(Vector3d eye, Vector3d up, Vector3d destination) {
        return new Matrix4d().identity().setLookAt(eye, destination, up);
    }

    public static List<Matrix4d> getAllDirectionViewSpaces(Vector3d pos, float near, float far) {
        List<Matrix4d> directions = new ArrayList<>();
        Matrix4d perspective = new Matrix4d().perspective((float) Math.toRadians(90.0f), 1.0f, near, far);

        Matrix4d projectionViewMatrix1 = new Matrix4d(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), new Vector3d(pos).add(1.0d, 0.0d, 0.0d)));
        Matrix4d projectionViewMatrix2 = new Matrix4d(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), new Vector3d(pos).add(-1.0d, 0.0d, 0.0d)));
        Matrix4d projectionViewMatrix3 = new Matrix4d(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3d(0.0d, 0.0d, 1.0d), new Vector3d(pos).add(0.0d, 1.0d, 0.0d)));
        Matrix4d projectionViewMatrix4 = new Matrix4d(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3d(0.0d, 0.0d, -1.0d), new Vector3d(pos).add(0.0d, -1.0d, 0.0d)));
        Matrix4d projectionViewMatrix5 = new Matrix4d(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), new Vector3d(pos).add(0.0d, 0.0d, 1.0d)));
        Matrix4d projectionViewMatrix6 = new Matrix4d(perspective).mul(Transformation.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), new Vector3d(pos).add(0.0d, 0.0d, -1.0d)));

        directions.add(projectionViewMatrix1);
        directions.add(projectionViewMatrix2);
        directions.add(projectionViewMatrix3);
        directions.add(projectionViewMatrix4);
        directions.add(projectionViewMatrix5);
        directions.add(projectionViewMatrix6);
        return directions;
    }
}
