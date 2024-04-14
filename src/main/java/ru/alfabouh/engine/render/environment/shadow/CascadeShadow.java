package ru.alfabouh.engine.render.environment.shadow;

import org.joml.Matrix4d;

public class CascadeShadow {
    private final Matrix4d lightProjectionViewMatrix;
    private float splitDistance;

    public CascadeShadow() {
        this.lightProjectionViewMatrix = new Matrix4d();
    }

    public float getSplitDistance() {
        return this.splitDistance;
    }

    public void setSplitDistance(float splitDistance) {
        this.splitDistance = splitDistance;
    }

    public Matrix4d getLightProjectionViewMatrix() {
        return new Matrix4d(this.lightProjectionViewMatrix);
    }

    public void setLightProjectionViewMatrix(Matrix4d matrix4d) {
        this.lightProjectionViewMatrix.set(matrix4d);
    }
}
