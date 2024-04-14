package ru.alfabouh.engine.render.scene.world.camera;

import org.joml.Vector3d;

public interface ICamera {
    Vector3d getCamPosition();

    Vector3d getCamRotation();

    void updateCamera(double partialTicks);
}
