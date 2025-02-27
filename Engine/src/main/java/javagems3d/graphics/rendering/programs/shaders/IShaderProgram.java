package javagems3d.graphics.rendering.programs.shaders;

import org.lwjgl.opengl.GL46;

public interface IShaderProgram {
    int getProgramId();

    default void clear() {
        this.unbind();
        if (this.getProgramId() != 0) {
            GL46.glDeleteProgram(this.getProgramId());
        }
    }

    default void bind() {
        GL46.glUseProgram(this.getProgramId());
    }

    default void unbind() {
        GL46.glUseProgram(0);
    }
}
