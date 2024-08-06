package ru.jgems3d.engine.graphics.opengl.camera;

import org.joml.Vector3f;

public interface ICamera {
    Vector3f getCamPosition();

    Vector3f getCamRotation();

    void updateCamera(float frameDeltaTicks);
}
