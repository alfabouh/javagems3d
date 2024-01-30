package ru.BouH.engine.render;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public class TransformationUtils {
    private final Matrix4d viewMatrix;

    public TransformationUtils() {
        this.viewMatrix = new Matrix4d();
    }

    public final Matrix4d getModelMatrix(Model<Format3D> model, boolean invertRotations) {
        Vector3d rotation = new Vector3d(model.getFormat().getRotation());
        Vector3d position = new Vector3d(model.getFormat().getPosition());
        if (invertRotations) {
            rotation.mul(-1);
        }
        Matrix4d m1 = new Matrix4d();
        Quaterniond quaterniond = new Quaterniond();
        quaterniond.rotateXYZ(Math.toRadians(-rotation.x), Math.toRadians(-rotation.y), Math.toRadians(-rotation.z));
        return m1.identity().translationRotateScale(position, quaterniond, model.getFormat().getScale());
    }

    public final Matrix4d getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().perspective(fov, width / height, zNear, zFar);
    }

    public final Matrix4d getOrthographicMatrix(float left, float right, float bottom, float top) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().setOrtho2D(left, right, bottom, top);
    }

    public final Matrix4d getLookAtMatrix(Vector3d eye, Vector3d up, Vector3d destination) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().setLookAt(eye, destination, up);
    }

    public final Matrix4d getOrthographicMatrix(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().setOrtho(left, right, bottom, top, zNear, zFar, zZeroToOne);
    }

    public Matrix4d getOrthoModelMatrix(Model<Format2D> model, Matrix4d orthoMatrix) {
        Vector2d rotation = model.getFormat().getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(new Vector3d(model.getFormat().getPosition(), 0.0d)).rotateX(Math.toRadians(-rotation.x)).rotateY(Math.toRadians(-rotation.y)).scaleXY(model.getFormat().getScale().x, model.getFormat().getScale().y);
        Matrix4d viewCurr = new Matrix4d(orthoMatrix);
        return viewCurr.mul(m1);
    }

    public Matrix4d getModelViewMatrix(Model<Format3D> model, Matrix4d viewMatrix) {
        Vector3d rotation = model.getFormat().getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(model.getFormat().getPosition()).rotateXYZ(Math.toRadians(-rotation.x), Math.toRadians(-rotation.y), Math.toRadians(-rotation.z)).scale(model.getFormat().getScale());
        Matrix4d viewCurr = new Matrix4d(viewMatrix);
        return viewCurr.mul(m1);
    }

    public void updateViewMatrix(ICamera camera) {
        this.viewMatrix.set(this.buildViewMatrix(camera, null));
    }

    public Matrix4d buildViewMatrix(ICamera camera, Vector3d offset) {
        Vector3d cameraPos = camera.getCamPosition();
        Vector3d cameraRot = camera.getCamRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().rotateXYZ(Math.toRadians(cameraRot.x), Math.toRadians(cameraRot.y), Math.toRadians(cameraRot.z)).translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        if (offset != null) {
            m1.translate(MathHelper.calcLookVector(cameraRot).mul(offset));
        }
        return m1;
    }

    public Matrix4d getViewMatrix() {
        return new Matrix4d(this.viewMatrix);
    }
}
