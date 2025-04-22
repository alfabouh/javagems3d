package javagems3d.system.resources.assets.texturing.maps;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import javagems3d.system.resources.assets.texturing.IPropertiesSample;
import javagems3d.system.resources.cache.ICached;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import javagems3d.system.resources.cache.ResourceCache;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

public class ImageTexture implements ICached, IPropertiesSample, ITexture2DProgram, ITextureBindless {
    protected IProperties properties;
    protected Vector2i size;
    protected int textureId;
    protected int samplerId;
    protected long bindlessHandler;

    public ImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull Data data) {
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
        this.properties = new Properties();
        this.init(textureProperties, data);
    }

    private void init(IProperties properties, @NotNull Data data) {
        this.size = data.getSize();
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 1);
        GL46.glTexImage2D(this.getTextureAttachment(), 0, GL46.GL_RGBA, this.getSize().x, this.getSize().y, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, data.getBuffer());
        if (properties != null && ((Properties) properties).isMipMap()) {
            int maxDimension = Math.max(this.getSize().x, this.getSize().y);
            int maxLevel = (int) Math.floor(Math.log(maxDimension) / Math.log(2));
            GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_MAX_LEVEL, maxLevel);
            GL46.glGenerateMipmap(this.getTextureAttachment());
        } else {
            GL46.glTexParameteri(this.getTextureAttachment(), GL46.GL_TEXTURE_MAX_LEVEL, 0);
        }
        this.unBindTexture();
        data.clear();
        this.setProperties(properties, true);
    }

    public void setProperties(IProperties properties, boolean update) {
        if (properties != null && update) {
            this.properties = properties;
        }
        Properties properties1 = (Properties) this.getProperties();
        //int quality = properties1.isQualityAffected() ? (2 - JGems3D.get().getGameSettings().texturesQuality.getValue()) : 0;
        boolean linear = properties1.isLinearFiltration();
        boolean anisotropic = properties1.isAnisotropicFiltration();

        if (this.getSamplerId() != 0) {
            GL46.glDeleteSamplers(this.getSamplerId());
        }
        this.samplerId = GL46.glGenSamplers();
        int bitMin = properties1.isMipMap() ? (linear ? GL46.GL_LINEAR_MIPMAP_LINEAR : GL46.GL_NEAREST_MIPMAP_NEAREST) : (linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MIN_FILTER, bitMin);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MAG_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_S, properties1.isShouldBeRepeated() ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_T, properties1.isShouldBeRepeated() ? GL46.GL_REPEAT : GL46.GL_CLAMP_TO_EDGE);
        if (anisotropic) {
            GL46.glSamplerParameterf(this.getSamplerId(), EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GL46.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        }
        this.removeARB64Handling();
        this.createBindlessHandling();
    }

    public void createBindlessHandling() {
        this.bindlessHandler = this.createBindlessHandler(this.getTextureId(), this.getSamplerId());
        this.createARB64Handling();
    }

    @Override
    public void reload(@Nullable IProperties properties, boolean update) {
        this.setProperties(properties, update);
    }

    @Override
    public IProperties getProperties() {
        return this.properties;
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

    @Override
    public boolean canBeBindless() {
        return true;
    }

    public static final class Properties implements IProperties {
        private final boolean linearFiltration;
        private final boolean shouldBeRepeated;
        private final boolean anisotropicFiltration;
        private final boolean qualityAffected;
        private final boolean mipMap;

        public Properties(boolean mipMap, boolean qualityAffected) {
            this(mipMap, true, true, true, qualityAffected);
        }

        public Properties() {
            this(true, true, true, true, false);
        }

        public Properties(boolean mipMap, boolean linearFilter, boolean shouldBeRepeated, boolean anisotropicFiltration, boolean qualityAffected) {
            this.linearFiltration = linearFilter;
            this.shouldBeRepeated = shouldBeRepeated;
            this.anisotropicFiltration = anisotropicFiltration;
            this.qualityAffected = qualityAffected;
            this.mipMap = mipMap;
        }

        public boolean isMipMap() {
            return this.mipMap;
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

    public static final class Data {
        private ByteBuffer buffer;
        private final Vector2i size;

        public Data(@NotNull ByteBuffer buffer, @NotNull Vector2i size) {
            this.buffer = buffer;
            this.size = size;
        }

        public void clear() {
            STBImage.stbi_image_free(this.getBuffer());
            this.buffer = null;
        }

        public ByteBuffer getBuffer() {
            return this.buffer;
        }

        public Vector2i getSize() {
            return this.size;
        }
    }
}