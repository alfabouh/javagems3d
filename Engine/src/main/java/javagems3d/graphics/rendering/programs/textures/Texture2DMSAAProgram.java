package javagems3d.graphics.rendering.programs.textures;

import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.texturing.base.ISample;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

public class Texture2DMSAAProgram implements ITexture2DProgram, ITextureBindless {
    private final int msaa;
    private int textureId;
    private int samplerId;
    private long bindlessHandler;
    private Vector2i size;

    public Texture2DMSAAProgram(int msaa) {
        this.size = null;
        this.msaa = msaa;
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
    }

    public void createTexture(Vector2i size, @NotNull Texture2DMSAAProgram.Properties properties, FloatBuffer pixels) {
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        this.size = size;
        GL46.glTexImage2DMultisample(this.getTextureAttachment(), this.msaa, properties.getInternalFormat(), size.x, size.y, true);
        this.createSampler(properties);
        this.unBindTexture();

        this.createBindlessHandling();
    }

    protected void createSampler(@NotNull Texture2DMSAAProgram.Properties properties) {
        if (this.getSamplerId() != 0) {
            GL46.glDeleteSamplers(this.getSamplerId());
        }
        this.samplerId = GL46.glGenSamplers();
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MAG_FILTER, properties.getFilteringMag());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MIN_FILTER, properties.getFilteringMin());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_COMPARE_MODE, properties.getCompareMode());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_COMPARE_FUNC, properties.getCompareFunc());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_S, properties.getClampS());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_T, properties.getClampT());
        if (properties.getBorderColor() != null) {
            GL46.glSamplerParameterfv(this.getSamplerId(), GL46.GL_TEXTURE_BORDER_COLOR, properties.getBorderColor());
        }
        if (this.isHandlerExists()) {
            this.removeARB64Handling();
            this.createBindlessHandling();
        }
    }

    public void createBindlessHandling() {
        this.bindlessHandler = this.createBindlessHandler(this.getTextureId(), this.getSamplerId());
        this.createARB64Handling();
    }

    @Override
    public void clear() {
        this.removeARB64Handling();
        GL46.glDeleteSamplers(this.getSamplerId());
        GL46.glDeleteTextures(this.getTextureId());
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
    }

    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL46.GL_TEXTURE_2D_MULTISAMPLE;
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
    public Vector2i getSize() {
        return this.size;
    }

    public static class Properties implements ISample.IProperties {
        private final int internalFormat;
        private final int filteringMag;
        private final int filteringMin;
        private final int compareMode;
        private final int compareFunc;
        private final int clampS;
        private final int clampT;
        private final float[] borderColor;

        public Properties(int internalFormat, int filteringMag, int filteringMin, int compareMode, int compareFunc, int clampS, int clampT, float[] borderColor) {
            this.internalFormat = internalFormat;
            this.filteringMag = filteringMag;
            this.filteringMin = filteringMin;
            this.compareMode = compareMode;
            this.compareFunc = compareFunc;
            this.clampS = clampS;
            this.clampT = clampT;
            this.borderColor = borderColor;
        }

        public int getInternalFormat() {
            return this.internalFormat;
        }

        public int getFilteringMag() {
            return this.filteringMag;
        }

        public int getFilteringMin() {
            return this.filteringMin;
        }

        public int getCompareMode() {
            return this.compareMode;
        }

        public int getCompareFunc() {
            return this.compareFunc;
        }

        public int getClampS() {
            return this.clampS;
        }

        public int getClampT() {
            return this.clampT;
        }

        public float[] getBorderColor() {
            return this.borderColor;
        }
    }
}