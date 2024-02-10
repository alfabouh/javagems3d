package ru.BouH.engine.render.scene.programs;

import org.joml.Vector2i;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import java.nio.ByteBuffer;

public class MSAATextureProgram implements ITextureProgram {
    private final int textureId;
    private final int msaa;

    public MSAATextureProgram(int msaa) {
        this.msaa = msaa;
        this.textureId = GL30.glGenTextures();
    }

    public void createTexture(Vector2i size, int internalFormat, int filtering_mag, int filtering_min, int compareMode, int compareFunc, int clamp_s, int clamp_t, float[] borderColor, boolean aliasing) {
        this.bindTexture(GL43.GL_TEXTURE_2D_MULTISAMPLE);
        GL43.glTexImage2DMultisample(GL43.GL_TEXTURE_2D_MULTISAMPLE, this.msaa, internalFormat, size.x, size.y, true);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D_MULTISAMPLE, GL30.GL_TEXTURE_MAG_FILTER, filtering_mag);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D_MULTISAMPLE, GL30.GL_TEXTURE_MIN_FILTER, filtering_min);
        if (aliasing) {
            GL30.glTexParameterf(GL43.GL_TEXTURE_2D_MULTISAMPLE, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GL30.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        }
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D_MULTISAMPLE, GL30.GL_TEXTURE_COMPARE_MODE, compareMode);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D_MULTISAMPLE, GL30.GL_TEXTURE_COMPARE_FUNC, compareFunc);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D_MULTISAMPLE, GL30.GL_TEXTURE_WRAP_S, clamp_s);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D_MULTISAMPLE, GL30.GL_TEXTURE_WRAP_T, clamp_t);
        if (borderColor != null) {
            GL30.glTexParameterfv(GL43.GL_TEXTURE_2D_MULTISAMPLE, GL30.GL_TEXTURE_BORDER_COLOR, borderColor);
        }
        this.unBindTexture();
    }

    public int getTextureId() {
        return this.textureId;
    }
}
