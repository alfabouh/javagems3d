package ru.alfabouh.engine.render.transformation;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;

import java.util.ArrayList;
import java.util.List;

public class TransformationManager {
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 500.0f;
    public static TransformationManager instance = new TransformationManager();
    private final TransformationUtils transformationUtils;

    public TransformationManager() {
        this.transformationUtils = new TransformationUtils();
    }

    public TransformationUtils getTransform() {
        return this.transformationUtils;
    }

    public Matrix4d getProjectionMatrix() {
        return TransformationManager.instance.getTransform().getProjectionMatrix(TransformationManager.FOV, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), TransformationManager.Z_NEAR, TransformationManager.Z_FAR);
    }

    public Matrix4d getProjectionMatrixFpv() {
        return TransformationManager.instance.getTransform().getProjectionMatrix(TransformationManager.FOV, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0.01f, 10.0f);
    }

    public Matrix4d buildViewMatrix(ICamera camera, Vector3d offset) {
        return TransformationManager.instance.getTransform().buildViewMatrix(camera, offset);
    }

    public Matrix4d getMainCameraViewMatrix() {
        return TransformationManager.instance.getTransform().getMainCameraViewMatrix();
    }

    public Matrix4d getModelViewMatrix(Model<Format3D> model) {
        return TransformationManager.instance.getTransform().getModelViewMatrix(model, this.getMainCameraViewMatrix());
    }

    public Matrix4d getModelViewMatrix(Model<Format3D> model, Matrix4d viewMatrix) {
        return TransformationManager.instance.getTransform().getModelViewMatrix(model, viewMatrix);
    }

    public Matrix4d getModelMatrix(Model<Format3D> model) {
        return this.getModelMatrix(model, false);
    }

    public Matrix4d getModelMatrix(Model<Format3D> model, boolean invertRotations) {
        return TransformationManager.instance.getTransform().getModelMatrix(model, invertRotations);
    }

    public Matrix4d getModelMatrix(Format3D format3D, boolean invertRotations) {
        return TransformationManager.instance.getTransform().getModelMatrix(format3D, invertRotations);
    }

    public void updateViewMatrix(ICamera camera) {
        this.transformationUtils.updateViewMatrix(camera);
    }

    public Matrix4d getScreenMatrix2D() {
        return TransformationManager.instance.getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0);
    }

    public Matrix4d getOrthographicScreenModelMatrix(Model<Format2D> model) {
        Matrix4d orthographicMatrix = TransformationManager.instance.getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0);
        return TransformationManager.instance.getTransform().getOrthoModelMatrix(model, orthographicMatrix);
    }

    public final Matrix4d getOrthographicMatrix(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne) {
        return TransformationManager.instance.getTransform().getOrthographicMatrix(left, right, bottom, top, zNear, zFar, zZeroToOne);
    }

    public final Matrix4d getLookAtMatrix(Vector3d eye, Vector3d up, Vector3d destination) {
        return TransformationManager.instance.getTransform().getLookAtMatrix(eye, up, destination);
    }

    public List<Matrix4d> getAllDirectionViewSpaces(Vector3d pos, float near, float far) {
        List<Matrix4d> directions = new ArrayList<>();
        Matrix4d perspective = new Matrix4d().perspective((float) Math.toRadians(90.0f), 1.0f, near, far);

        Matrix4d projectionViewMatrix1 = new Matrix4d(perspective).mul(TransformationManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), new Vector3d(pos).add(1.0d, 0.0d, 0.0d)));
        Matrix4d projectionViewMatrix2 = new Matrix4d(perspective).mul(TransformationManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), new Vector3d(pos).add(-1.0d, 0.0d, 0.0d)));
        Matrix4d projectionViewMatrix3 = new Matrix4d(perspective).mul(TransformationManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, 0.0d, 1.0d), new Vector3d(pos).add(0.0d, 1.0d, 0.0d)));
        Matrix4d projectionViewMatrix4 = new Matrix4d(perspective).mul(TransformationManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, 0.0d, -1.0d), new Vector3d(pos).add(0.0d, -1.0d, 0.0d)));
        Matrix4d projectionViewMatrix5 = new Matrix4d(perspective).mul(TransformationManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), new Vector3d(pos).add(0.0d, 0.0d, 1.0d)));
        Matrix4d projectionViewMatrix6 = new Matrix4d(perspective).mul(TransformationManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), new Vector3d(pos).add(0.0d, 0.0d, -1.0d)));

        directions.add(projectionViewMatrix1);
        directions.add(projectionViewMatrix2);
        directions.add(projectionViewMatrix3);
        directions.add(projectionViewMatrix4);
        directions.add(projectionViewMatrix5);
        directions.add(projectionViewMatrix6);
        return directions;
    }
}
