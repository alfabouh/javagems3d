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
