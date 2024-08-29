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

package ru.jgems3d.engine.graphics.opengl.environment.light;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;

public interface ILightManager {
    void updateBuffers(MemoryStack stack, SceneWorld sceneWorld, Matrix4f viewMatrix);
}
