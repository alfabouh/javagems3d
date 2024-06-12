package ru.alfabouh.jgems3d.engine.render.transformation;

import org.joml.Matrix4d;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.ICamera;

public class CameraTransformation {
    private final Matrix4d viewMatrix;

    public CameraTransformation() {
        this.viewMatrix = new Matrix4d().identity();
    }

    public void update(ICamera camera) {
        this.viewMatrix.set(Transformation.getViewMatrix(camera));
    }

    public Matrix4d getViewMatrix() {
        return new Matrix4d(this.viewMatrix);
    }
}