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
import javagems3d.JGemsHelper;
import javagems3d.system.service.exceptions.JGemsIOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import javagems3d.system.resources.assets.texturing.base.IImageTexture;
import javagems3d.system.resources.cache.ResourceCache;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL46;

public class ImageTexture implements IImageTexture {
    private final String name;
    private Vector2i size;
    private int textureId;

    public ImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull Data data, @NotNull String name) {
        this.name = name;
        this.init(textureProperties, data);
    }

    public void setProperties(IProperties properties) {
        if (properties == null) {
            properties = new Properties();
        }
        if (properties instanceof Properties) {
            Properties properties1 = (Properties) properties;
            int quality = properties1.isQualityAffected() ? (2 - JGems3D.get().getGameSettings().texturesQuality.getValue()) : 0;
            boolean linear = properties1.isLinearFiltration() && JGems3D.get().getGameSettings().texturesFiltering.getValue() == 1;
            boolean anisotropic = properties1.isAnisotropicFiltration() && JGems3D.get().getGameSettings().anisotropic.getValue() == 1;

            GL46.glBindTexture(GL46.GL_TEXTURE_2D, this.getTextureId());
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, linear ? GL46.GL_LINEAR_MIPMAP_LINEAR : GL46.GL_NEAREST_MIPMAP_NEAREST);
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, properties1.isShouldBeRepeated() ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, properties1.isShouldBeRepeated() ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);
            if (anisotropic) {
                GL46.glTexParameterf(GL46.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GL46.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            }
            GL46.glGenerateMipmap(GL46.GL_TEXTURE_2D);
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_BASE_LEVEL, quality);
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAX_LEVEL, 11);
            GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
        }
    }

    @Override
    public void init(IProperties properties, Data data) {
        if (data == null) {
            throw new JGemsIOException("Couldn't create texture " + this.getName());
        }
        this.size = data.getSize();
        this.textureId = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, this.getTextureId());
        GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 1);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGBA, this.getSize().x, this.getSize().y, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, data.getBuffer());
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
        data.clear();
        this.setProperties(properties);
        JGemsHelper.getLogger().log("Texture " + this.getName() + " successfully created!");
    }

    @Override
    public void reload(@Nullable IProperties properties) {
        this.setProperties(properties);
    }

    @Override
    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL46.GL_TEXTURE_2D;
    }

    @Override
    public void bindTexture() {
        GL46.glBindTexture(this.getTextureAttachment(), this.getTextureId());
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Vector2i getSize() {
        return this.size;
    }

    public void clear() {
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
        GL46.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clear();
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
}