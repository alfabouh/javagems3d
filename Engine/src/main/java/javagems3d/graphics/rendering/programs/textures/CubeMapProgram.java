package javagems3d.graphics.rendering.programs.textures;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.texturing.base.ISample;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class CubeMapProgram implements ICubeMapProgram, ITextureBindless {
    private int textureId;
    private int samplerId;
    private long bindlessHandler;
    private Vector2i[] size;

    public CubeMapProgram() {
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
        this.size = new Vector2i[6];
    }

    public void createTexture(Vector2i size6x, @NotNull CubeMapProgram.Properties properties, FloatBuffer pixels) {
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        for (int i = 0; i < 6; i++) {
            this.size[i] = size6x;
            GL46.glTexImage2D(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, properties.getInternalFormat(), size6x.x, size6x.y, 0, properties.getTextureFormat(), GL46.GL_FLOAT, (ByteBuffer) null);
        }
        this.createSampler(properties);
        this.unBindTexture();

        this.createBindlessHandling();
    }

    protected void createSampler(@NotNull CubeMapProgram.Properties properties) {
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
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_R, properties.getClampR());
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
    public Vector2i[] getSize() {
        return this.size;
    }

    public static class Properties implements ISample.IProperties {
        private final int textureFormat;
        private final int internalFormat;
        private final int filteringMag;
        private final int filteringMin;
        private final int compareMode;
        private final int compareFunc;
        private final int clampS;
        private final int clampT;
        private final int clampR;
        private final float[] borderColor;

        public Properties(int textureFormat, int internalFormat, int filteringMag, int filteringMin, int compareMode, int compareFunc, int clampS, int clampT, int clampR, float[] borderColor) {
            this.textureFormat = textureFormat;
            this.internalFormat = internalFormat;
            this.filteringMag = filteringMag;
            this.filteringMin = filteringMin;
            this.compareMode = compareMode;
            this.compareFunc = compareFunc;
            this.clampS = clampS;
            this.clampT = clampT;
            this.clampR = clampR;
            this.borderColor = borderColor;
        }

        public int getTextureFormat() {
            return this.textureFormat;
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

        public int getClampR() {
            return this.clampR;
        }

        public float[] getBorderColor() {
            return this.borderColor;
        }
    }
}