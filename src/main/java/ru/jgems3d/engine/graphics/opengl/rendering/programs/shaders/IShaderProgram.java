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

package ru.jgems3d.engine.graphics.opengl.rendering.programs.shaders;

import org.lwjgl.opengl.GL20;

public interface IShaderProgram {
    int getProgramId();
    default void clean(){
        this.unbind();
        if (this.getProgramId() != 0) {
            GL20.glDeleteProgram(this.getProgramId());
        }
    }

    default void bind(){
        GL20.glUseProgram(this.getProgramId());
    }

    default void unbind(){
        GL20.glUseProgram(0);
    }
}
