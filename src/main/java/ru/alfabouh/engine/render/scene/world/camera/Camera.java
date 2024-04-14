package ru.alfabouh.engine.render.scene.world.camera;

import org.joml.Vector3d;

public class Camera implements ICamera {
    private final Vector3d camPosition;
    private final Vector3d camRotation;

    public Camera(Vector3d pos, Vector3d rot) {
        this.camPosition = new Vector3d(pos);
        this.camRotation = new Vector3d(rot);
    }

    public Camera() {
        this(new Vector3d(0.0d), new Vector3d(0.0d));
    }

    protected void setCameraPos(Vector3d vector3d) {
        this.camPosition.set(vector3d);
    }

    protected void setCameraRot(Vector3d vector3d) {
        this.camRotation.set(vector3d);
    }

    public Vector3d getCamPosition() {
        return new Vector3d(this.camPosition);
    }

    public Vector3d getCamRotation() {
        return new Vector3d(this.camRotation);
    }

    @Override
    public void updateCamera(double partialTicks) {
    }
}
