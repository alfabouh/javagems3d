package javagems3d.system.resources.assets.texturing;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.texturing.base.IImageTexture;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.assets.texturing.packs.CubeMapTexturingDataPack;
import javagems3d.system.resources.cache.ResourceCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class CubeMapTexture implements IImageTexture {
    private final String name;
    private int textureId;

    public CubeMapTexture(@Nullable CubeMapTexture.Properties textureProperties, @NotNull CubeMapTexture.Data data, @NotNull String name) {
        this.name = name;
        this.init(textureProperties, data);
    }

    @Override
    public void bindTexture() {
        GL46.glBindTexture(this.getTextureAttachment(), this.getTextureId());
    }

    @Override
    public void init(@Nullable IProperties properties, IData iData) {
        Data data = (Data) iData;
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        for (int i = 0; i < 6; i++) {
            ImageTexture.Data data1 = data.getDataSet()[i];
            GL46.glTexImage2D(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL46.GL_RGB16, data1.getSize().x, data1.getSize().y, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, data1.getBuffer());
            data1.clear();
        }
        this.unBindTexture();
        this.setProperties(properties);
    }

    public void setProperties(IProperties properties) {
        if (properties == null) {
            properties = new CubeMapTexture.Properties(true);
        }
        CubeMapTexture.Properties properties1 = (CubeMapTexture.Properties) properties;
        boolean linear = properties1.isLinearFiltration();
        this.bindTexture();
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MIN_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MAG_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_R, GL46.GL_CLAMP_TO_EDGE);
        this.unBindTexture();
    }
    @Override
    public void reload(@Nullable IProperties properties) {
        this.setProperties(properties);
    }

    public void clear() {
        GL46.glBindTexture(this.getTextureAttachment(), 0);
        GL46.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    @Override
    public void onClearingCache(ResourceCache resourceCache) {
        this.clear();
    }

    public String getName() {
        return this.name;
    }

    public Vector2i getSize() {
        return this.size;
    }

    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL46.GL_TEXTURE_CUBE_MAP;
    }

    public static final class Data implements IData {
        private final ImageTexture.Data[] dataSet;

        public Data(ImageTexture.Data[] dataSet) {
            this.dataSet = dataSet;
        }

        public ImageTexture.Data[] getDataSet() {
            return this.dataSet;
        }
    }

    public static final class Properties implements ISample.IProperties {
        private final boolean linearFiltration;

        public Properties(boolean linearFilter) {
            this.linearFiltration = linearFilter;
        }

        public boolean isLinearFiltration() {
            return this.linearFiltration;
        }
    }
}
