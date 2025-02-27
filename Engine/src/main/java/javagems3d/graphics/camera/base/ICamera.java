package javagems3d.graphics.camera.base;

import org.joml.Vector3f;

public interface ICamera {
    Vector3f getCamPosition();

    Vector3f getCamRotation();

    void updateCamera(float frameDeltaTicks);
}
