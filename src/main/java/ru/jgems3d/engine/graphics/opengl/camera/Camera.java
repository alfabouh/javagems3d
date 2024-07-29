package ru.jgems3d.engine.graphics.opengl.camera;

import org.joml.Vector3f;

public class Camera implements ICamera {
    protected final Vector3f camPosition;
    protected final Vector3f camRotation;

    public Camera(Vector3f pos, Vector3f rot) {
        this.camPosition = new Vector3f(pos);
        this.camRotation = new Vector3f(rot);
    }

    public Camera() {
        this(new Vector3f(0.0f), new Vector3f(0.0f));
    }

    protected void setCameraPos(Vector3f vector3f) {
        this.camPosition.set(vector3f);
    }

    protected void setCameraRot(Vector3f vector3f) {
        this.camRotation.set(vector3f);
    }

    public Vector3f getCamPosition() {
        return new Vector3f(this.camPosition);
    }

    public Vector3f getCamRotation() {
        return new Vector3f(this.camRotation);
    }

    @Override
    public void updateCamera(float partialTicks) {
    }
}
