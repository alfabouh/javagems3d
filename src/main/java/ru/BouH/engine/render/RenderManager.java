package ru.BouH.engine.render;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.render.scene.world.camera.ICamera;

import java.util.ArrayList;
import java.util.List;

public class RenderManager {
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 500.0f;
    public static RenderManager instance = new RenderManager();
    private final TransformationUtils transformationUtils;

    public RenderManager() {
        this.transformationUtils = new TransformationUtils();
    }

    public TransformationUtils getTransform() {
        return this.transformationUtils;
    }

    public Matrix4d getProjectionMatrix() {
        return RenderManager.instance.getTransform().getProjectionMatrix(RenderManager.FOV, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), RenderManager.Z_NEAR, RenderManager.Z_FAR);
    }

    public Matrix4d buildViewMatrix(ICamera camera, Vector3d offset) {
        return RenderManager.instance.getTransform().buildViewMatrix(camera, offset);
    }

    public Matrix4d getViewMatrix() {
        return RenderManager.instance.getTransform().getViewMatrix();
    }

    public Matrix4d getModelViewMatrix(Model<Format3D> model) {
        return RenderManager.instance.getTransform().getModelViewMatrix(model, this.getViewMatrix());
    }

    public Matrix4d getModelViewMatrix(Model<Format3D> model, Matrix4d viewMatrix) {
        return RenderManager.instance.getTransform().getModelViewMatrix(model, viewMatrix);
    }

    public Matrix4d getModelMatrix(Model<Format3D> model) {
        return this.getModelMatrix(model, false);
    }

    public Matrix4d getModelMatrix(Model<Format3D> model, boolean invertRotations) {
        return RenderManager.instance.getTransform().getModelMatrix(model, invertRotations);
    }

    public void updateViewMatrix(ICamera camera) {
        this.transformationUtils.updateViewMatrix(camera);
    }

    public Matrix4d getScreenMatrix2D() {
        return RenderManager.instance.getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0);
    }

    public Matrix4d getOrthographicScreenModelMatrix(Model<Format2D> model) {
        Matrix4d orthographicMatrix = RenderManager.instance.getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0);
        return RenderManager.instance.getTransform().getOrthoModelMatrix(model, orthographicMatrix);
    }

    public final Matrix4d getOrthographicMatrix(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne) {
        return RenderManager.instance.getTransform().getOrthographicMatrix(left, right, bottom, top, zNear, zFar, zZeroToOne);
    }

    public final Matrix4d getLookAtMatrix(Vector3d eye, Vector3d up, Vector3d destination) {
        return RenderManager.instance.getTransform().getLookAtMatrix(eye, up, destination);
    }

    public List<Matrix4d> getAllDirectionViewSpaces(Vector3d pos, float near, float far) {
        List<Matrix4d> directions = new ArrayList<>();
        Matrix4d perspective = new Matrix4d().perspective((float) Math.toRadians(90.0f), 1.0f, near, far);

        Matrix4d projectionViewMatrix1 = new Matrix4d(perspective).mul(RenderManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), pos.add(1.0d, 0.0d, 0.0d)));
        Matrix4d projectionViewMatrix2 = new Matrix4d(perspective).mul(RenderManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), pos.add(-1.0d, 0.0d, 0.0d)));
        Matrix4d projectionViewMatrix3 = new Matrix4d(perspective).mul(RenderManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, 0.0d, 1.0d), pos.add(0.0d, 1.0d, 0.0d)));
        Matrix4d projectionViewMatrix4 = new Matrix4d(perspective).mul(RenderManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, 0.0d, -1.0d), pos.add(0.0d, -1.0d, 0.0d)));
        Matrix4d projectionViewMatrix5 = new Matrix4d(perspective).mul(RenderManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), pos.add(0.0d, 0.0d, 1.0d)));
        Matrix4d projectionViewMatrix6 = new Matrix4d(perspective).mul(RenderManager.instance.getLookAtMatrix(pos, new Vector3d(0.0d, -1.0d, 0.0d), pos.add(0.0d, 0.0d, -1.0d)));

        directions.add(projectionViewMatrix1);
        directions.add(projectionViewMatrix2);
        directions.add(projectionViewMatrix3);
        directions.add(projectionViewMatrix4);
        directions.add(projectionViewMatrix5);
        directions.add(projectionViewMatrix6);
        return directions;
    }
}
