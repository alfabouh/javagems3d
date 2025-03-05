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
        GL46.glTexImage2D(this.getTextureAttachment(), 0, properties.getTextureFormat(), size.x, size.y, 0, properties.getInternalFormat(), GL46.GL_FLOAT, pixels);
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_MAG_FILTER, properties.getFilteringMag());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_MIN_FILTER, properties.getFilteringMin());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_COMPARE_MODE, properties.getCompareMode());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_COMPARE_FUNC, properties.getCompareFunc());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_WRAP_S, properties.getClampS());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_WRAP_T, properties.getClampT());
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
