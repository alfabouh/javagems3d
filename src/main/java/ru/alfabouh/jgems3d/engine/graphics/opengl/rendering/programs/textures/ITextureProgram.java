package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.programs.textures;

import org.lwjgl.opengl.GL30;

public interface ITextureProgram {
    int getTextureId();

    default void cleanUp() {
        GL30.glDeleteTextures(this.getTextureId());
        this.unBindTexture();
    }

    default void bindTexture(int code) {
        GL30.glBindTexture(code, this.getTextureId());
    }

    default void unBindTexture() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }
}
