package javagems3d.graphics.rendering.programs.textures;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class CubeMapProgram implements ICubeMapProgram, ITextureBindless {
    private int textureId;
    private int samplerId;
    private long bindlessHandler;
    private final boolean bindless;
    private final Vector2i[] size;

    public CubeMapProgram(boolean bindless) {
        this.bindless = bindless;
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
        this.size = new Vector2i[6];
    }

    public void createTexture(Vector2i size6x, @NotNull CubeMapProgram.Properties properties, FloatBuffer pixels) {
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 1);
        for (int i = 0; i < 6; i++) {
            this.size[i] = size6x;
            GL46.glTexImage2D(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, properties.internalFormat(), size6x.x, size6x.y, 0, properties.textureFormat(), properties.getTextureTypeByFormat(properties.internalFormat), (ByteBuffer) null);
        }
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_BASE_LEVEL, 0);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAX_LEVEL, 0);
        this.unBindTexture();
        this.createSampler(properties);
    }

    protected void createSampler(@NotNull CubeMapProgram.Properties properties) {
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
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_R, properties.clampR());
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
        this.removeARB64Handling();
        GL46.glDeleteTextures(this.getTextureId());
        GL46.glDeleteSamplers(this.getSamplerId());
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
    }

    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL46.GL_TEXTURE_CUBE_MAP;
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

    @Override
    public Vector2i[] getSize() {
        return this.size;
    }

    public record Properties(int textureFormat, int internalFormat, int filteringMag, int filteringMin, int compareMode,
                             int compareFunc, int clampS, int clampT, int clampR,
                             float[] borderColor) implements IProperties {
    }
}