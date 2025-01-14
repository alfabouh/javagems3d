/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.rendering.programs.textures;

import javagems3d.graphics.rendering.programs.textures.cache.TexturesSamplersCachingProgram;
import javagems3d.graphics.rendering.programs.textures.ext.ITextureBindless;
import javagems3d.system.resources.assets.texturing.base.ISample;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Objects;

public class Texture2DMSAAProgram implements ITextureProgram, ITextureBindless {
    private final int msaa;
    private int textureId;
    private int samplerId;
    private long bindlessHandler;

    public Texture2DMSAAProgram(int msaa) {
        this.msaa = msaa;
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
    }

    public void createTexture(Vector2i size, @NotNull Texture2DMSAAProgram.Properties properties, FloatBuffer pixels) {
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        GL46.glTexImage2DMultisample(this.getTextureAttachment(), this.msaa, properties.getInternalFormat(), size.x, size.y, true);
        this.samplerId = this.createSampler(properties);
        this.unBindTexture();

        this.bindlessHandler = this.createBindingHandler(this.getTextureId(), this.getSamplerId());
        this.createARB64Handling();
    }

    protected int createSampler(@NotNull Texture2DMSAAProgram.Properties properties) {
        int sampler = TexturesSamplersCachingProgram.createSamplerId(Texture2DMSAAProgram.class, properties.getHash());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MAG_FILTER, properties.getFilteringMag());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MIN_FILTER, properties.getFilteringMin());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_COMPARE_MODE, properties.getCompareMode());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_COMPARE_FUNC, properties.getCompareFunc());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_S, properties.getClampS());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_T, properties.getClampT());
        if (properties.getBorderColor() != null) {
            GL46.glSamplerParameterfv(this.getSamplerId(), GL46.GL_TEXTURE_BORDER_COLOR, properties.getBorderColor());
        }
        return sampler;
    }

    @Override
    public void clear() {
        this.removeARB64Handling();
        GL46.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
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

        @Override
        public int hashCode() {
            return Objects.hash(internalFormat, filteringMag, filteringMin, compareMode, compareFunc, clampS, clampT, Arrays.hashCode(borderColor));
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

        @Override
        public int getHash() {
            return this.hashCode();
        }
    }
}