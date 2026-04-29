package javagems3d.graphics.camera.base;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class CameraBase implements ICamera {
    protected final Vector3f camPosition;
    protected final Vector3f camRotation;
    protected Vector3f lookAt;

    public CameraBase(ICamera camera) {
        this.camPosition = new Vector3f(camera.getCamPosition());
        this.camRotation = new Vector3f(camera.getCamRotation());
        this.lookAt = null;
    }

    public CameraBase(Vector3f pos, Vector3f rot) {
        this.camPosition = new Vector3f(pos);
        this.camRotation = new Vector3f(rot);
    }

    public CameraBase() {
        this(new Vector3f(0.0f), new Vector3f(0.0f));
    }

    public void setCameraPosition(Vector3f vector3f) {
        this.camPosition.set(vector3f);
    }

    public void setCameraRotation(Vector3f vector3f) {
        this.camRotation.set(vector3f);
    }

    public void setLookAt(@Nullable Vector3f lookAt) {
        this.lookAt = lookAt;
    }

    @Override
    public @Nullable Vector3f getLookAtPosition() {
        return this.lookAt;
    }

    public Vector3f getCamPosition() {
        return new Vector3f(this.camPosition);
    }

    public Vector3f getCamRotation() {
        return new Vector3f(this.camRotation);
    }

    @Override
    public void updateCamera(float frameDeltaTicks) {
    }
}
