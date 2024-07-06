package ru.alfabouh.jgems3d.engine.graphics.transformation;

import org.joml.Matrix4f;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.camera.ICamera;

public class CameraTransformation {
    private final Matrix4f viewMatrix;

    public CameraTransformation() {
        this.viewMatrix = new Matrix4f().identity();
    }

    public void update(ICamera camera) {
        this.viewMatrix.set(Transformation.getViewMatrix(camera));
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f(this.viewMatrix);
    }
}