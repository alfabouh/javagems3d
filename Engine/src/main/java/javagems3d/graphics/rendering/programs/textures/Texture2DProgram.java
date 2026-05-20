package javagems3d.graphics.rendering.programs.textures;

import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

public class Texture2DProgram implements ITexture2DProgram, ITextureBindless {
    private final boolean bindless;
    private int textureId;
    private int samplerId;
    private long bindlessHandler;
    private Vector2i size;

    public Texture2DProgram(boolean bindless) {
        this.bindless = bindless;
        this.size = null;
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
    }

    public void createTexture(Vector2i size, @NotNull Properties properties, FloatBuffer pixels) {
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        this.size = size;
        GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 1);
        GL46.glTexImage2D(this.getTextureAttachment(), 0, properties.textureFormat(), this.getSize().x, this.getSize().y, 0, properties.internalFormat(), properties.getTextureTypeByFormat(properties.textureFormat()), pixels);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_BASE_LEVEL, 0);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAX_LEVEL, 0);
        this.unBindTexture();
        this.createSampler(properties);
    }

    protected void createSampler(@NotNull Properties properties) {
        if (this.getSamplerId() != 0) {
            GL46.glDeleteSamplers(this.getSamplerId());
        }
        this.samplerId = GL46.glGenSamplers();
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MAG_FILTER, properties.filteringMag());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MIN_FILTER, properties.filteringMin());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_COMPARE_MODE, properties.compareMode());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_COMPARE_FUNC, properties.compareFunc());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_S, properties.clampS());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_T, properties.clampT());

        if (properties.borderColor() != null) {
            GL46.glSamplerParameterfv(this.getSamplerId(), GL46.GL_TEXTURE_BORDER_COLOR, properties.borderColor());
        }
        this.removeARB64Handling();
        this.createBindlessHandling();
    }

    public void createBindlessHandling() {
        this.bindlessHandler = this.createBindlessHandler(this.getTextureId(), this.getSamplerId());
        this.createARB64Handling();
    }

    @Override
    public void clear() {
        this.size = null;
        this.removeARB64Handling();
        GL46.glDeleteTextures(this.getTextureId());
        GL46.glDeleteSamplers(this.getSamplerId());
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
    }

    public Vector2i getSize() {
        return this.size;
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
        return this.samplerId;
    }

    @Override
    public long getBindingHandler() {
        return this.bindlessHandler;
    }

    @Override
    public boolean canBeBindless() {
        return this.bindless;
    }

    public record Properties(int textureFormat, int internalFormat, int filteringMag, int filteringMin, int compareMode,
                             int compareFunc, int clampS, int clampT, float[] borderColor) implements IProperties {
    }
}
