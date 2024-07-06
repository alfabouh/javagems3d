package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.camera;

import org.joml.Vector3f;

public interface ICamera {
    Vector3f getCamPosition();

    Vector3f getCamRotation();

    void updateCamera(float partialTicks);
}
