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

package javagems3d.system.resources.assets.texturing;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.ext.ITextureBindless;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.resources.cache.ResourceCache;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.util.Objects;

public class ImageTexture implements ImageBasedTexture, ITextureBindless {
    private Vector2i size;
    private int textureId;
    private int samplerId;
    private long bindlessHandler;

    public ImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull Data data) {
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
        this.init(textureProperties, data);
    }

    @Override
    public void init(IProperties properties, @NotNull IData iData) {
        Data data = (Data) iData;
        this.size = data.getSize();
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 1);
        GL46.glTexImage2D(this.getTextureAttachment(), 0, GL46.GL_RGBA, this.getSize().x, this.getSize().y, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, data.getBuffer());
        GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_MAX_LEVEL, 11);
        GL46.glGenerateMipmap(this.getTextureAttachment());
        this.unBindTexture();
        data.clear();
        this.setProperties(properties);

        this.createBindlessHandling();
    }

    public void setProperties(IProperties properties) {
        if (properties == null) {
            properties = new Properties();
        }
        Properties properties1 = (Properties) properties;
        //int quality = properties1.isQualityAffected() ? (2 - JGems3D.get().getGameSettings().texturesQuality.getValue()) : 0;
        boolean linear = properties1.isLinearFiltration() && JGems3D.get().getGameSettings().texturesFiltering.getValue() == 1;
        boolean anisotropic = properties1.isAnisotropicFiltration() && JGems3D.get().getGameSettings().anisotropic.getValue() == 1;

        if (this.getSamplerId() != 0) {
            GL46.glDeleteSamplers(this.getSamplerId());
        }
        this.samplerId = GL46.glGenSamplers();
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MIN_FILTER, linear ? GL46.GL_LINEAR_MIPMAP_LINEAR : GL46.GL_NEAREST_MIPMAP_NEAREST);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MAG_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_S, properties1.isShouldBeRepeated() ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_T, properties1.isShouldBeRepeated() ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);
        if (anisotropic) {
            GL46.glSamplerParameterf(this.getSamplerId(), EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GL46.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
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
    public void reload(@Nullable IProperties properties) {
        this.setProperties(properties);
    }

    @Override
    public int getSamplerId() {
        return this.samplerId;
    }

    @Override
    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL46.GL_TEXTURE_2D;
    }

    public Vector2i getSize() {
        return this.size;
    }

    public void clear() {
        this.removeARB64Handling();
        GL46.glDeleteSamplers(this.getSamplerId());
        GL46.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
        this.samplerId = 0;
        this.bindlessHandler = 0;
    }

    @Override
    public void onClearingCache(ResourceCache resourceCache) {
        this.clear();
    }

    @Override
    public long getBindingHandler() {
        return this.bindlessHandler;
    }

    public static final class Properties implements IProperties {
        private final boolean linearFiltration;
        private final boolean shouldBeRepeated;
        private final boolean anisotropicFiltration;
        private final boolean qualityAffected;

        public Properties(boolean qualityAffected) {
            this(true, true, true, qualityAffected);
        }

        public Properties() {
            this(true, true, true, false);
        }

        public Properties(boolean linearFilter, boolean shouldBeRepeated, boolean anisotropicFiltration, boolean qualityAffected) {
            this.linearFiltration = linearFilter;
            this.shouldBeRepeated = shouldBeRepeated;
            this.anisotropicFiltration = anisotropicFiltration;
            this.qualityAffected = qualityAffected;
        }

        public boolean isLinearFiltration() {
            return this.linearFiltration;
        }

        public boolean isShouldBeRepeated() {
            return this.shouldBeRepeated;
        }

        public boolean isAnisotropicFiltration() {
            return this.anisotropicFiltration;
        }

        public boolean isQualityAffected() {
            return this.qualityAffected;
        }
    }

    public static final class Data implements IData {
        private final ByteBuffer buffer;
        private final Vector2i size;

        public Data(@NotNull ByteBuffer buffer, @NotNull Vector2i size) {
            this.buffer = buffer;
            this.size = size;
        }

        public void clear() {
            STBImage.stbi_image_free(this.getBuffer());
        }

        public ByteBuffer getBuffer() {
            return this.buffer;
        }

        public Vector2i getSize() {
            return this.size;
        }
    }
}