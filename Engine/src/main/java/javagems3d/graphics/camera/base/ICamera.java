package javagems3d.graphics.camera.base;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface ICamera {
    @Nullable Vector3f getLookAtPosition();
    Vector3f getCamPosition();
    Vector3f getCamRotation();

    void updateCamera(float frameDeltaTicks);
}
