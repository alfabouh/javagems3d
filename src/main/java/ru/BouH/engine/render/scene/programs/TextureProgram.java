package ru.BouH.engine.render.scene.programs;

import org.joml.Vector2i;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

public class TextureProgram {
    private int textureId;

    public TextureProgram() {
    }

    public void createTexture(Vector2i size, int internalFormat, int textureFormat, int filtering_mag, int filtering_min, int compareMode, int compareFunc, int clamp_s, int clamp_t, float[] borderColor, boolean aliasing) {
        this.textureId = GL30.glGenTextures();
        this.bindTexture();
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, internalFormat, size.x, size.y, 0, textureFormat, GL30.GL_FLOAT, (ByteBuffer) null);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, filtering_mag);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, filtering_min);
        if (aliasing) {
            GL30.glTexParameterf(GL30.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GL30.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        }
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_COMPARE_MODE, compareMode);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_COMPARE_FUNC, compareFunc);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, clamp_s);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, clamp_t);
        if (borderColor != null) {
            GL30.glTexParameterfv(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_BORDER_COLOR, borderColor);
        }
        this.unBindTexture();
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void cleanUp() {
        GL30.glDeleteTextures(this.getTextureId());
        this.unBindTexture();
    }

    public void bindTexture() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.getTextureId());
    }

    public void unBindTexture() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }
}
