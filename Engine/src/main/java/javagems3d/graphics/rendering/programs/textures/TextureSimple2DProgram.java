package javagems3d.graphics.rendering.programs.textures;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

public class TextureSimple2DProgram implements ITexture2DProgram {
    private int textureId;
    private Vector2i size;

    public TextureSimple2DProgram() {
        this.textureId = 0;
        this.size = null;
    }

    public void createTexture(Vector2i size, @NotNull Texture2DProgram.Properties properties, FloatBuffer pixels) {
        this.size = size;
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        GL46.glTexImage2D(this.getTextureAttachment(), 0, properties.textureFormat(), size.x, size.y, 0, properties.internalFormat(), properties.getTextureTypeByFormat(properties.internalFormat()), pixels);
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_MAG_FILTER, properties.filteringMag());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_MIN_FILTER, properties.filteringMin());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_COMPARE_MODE, properties.compareMode());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_COMPARE_FUNC, properties.compareFunc());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_WRAP_S, properties.clampS());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_WRAP_T, properties.clampT());
        this.unBindTexture();
    }

    @Override
    public void clear() {
        GL46.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL46.GL_TEXTURE_2D;
    }

    @Override
    public int getSamplerId() {
        return 0;
    }

    @Override
    public Vector2i getSize() {
        return this.size;
    }
}
