/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.graphics.opengl.camera;

import org.joml.Vector3f;

public interface ICamera {
    Vector3f getCamPosition();

    Vector3f getCamRotation();

    void updateCamera(float frameDeltaTicks);
}
