package javagems3d.system.resources.assets.texturing;

import javagems3d.graphics.rendering.programs.textures.ext.ITextureBindless;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.cache.ResourceCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

public class CubeMapTexture implements ImageBasedTexture, ITextureBindless {
    private Vector2i[] size6;
    private int textureId;
    private int samplerId;
    private long bindlessHandler;

    public CubeMapTexture(@Nullable CubeMapTexture.Properties textureProperties, @NotNull CubeMapTexture.Data data) {
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
        this.init(textureProperties, data);
    }

    @Override
    public void init(@Nullable IProperties properties, @NotNull IData iData) {
        Data data = (Data) iData;
        this.size6 = new Vector2i[6];
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        for (int i = 0; i < 6; i++) {
            this.size6[i] = data.getDataSet()[i].getSize();
            GL46.glTexImage2D(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL46.GL_RGB16, this.getSize()[i].x, this.getSize()[i].y, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, data.getDataSet()[i].getBuffer());
        }
        data.clear();
        this.unBindTexture();
        this.setProperties(properties);

        this.createBindlessHandling();
    }

    public void setProperties(IProperties properties) {
        if (properties == null) {
            properties = new CubeMapTexture.Properties(true);
        }
        CubeMapTexture.Properties properties1 = (CubeMapTexture.Properties) properties;
        if (this.getSamplerId() != 0) {
            GL46.glDeleteSamplers(this.getSamplerId());
        }
        this.samplerId = GL46.glGenSamplers();
        boolean linear = properties1.isLinearFiltration();
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MIN_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MAG_FILTER, linear ? GL46.GL_LINEAR : GL46.GL_NEAREST);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_R, GL46.GL_CLAMP_TO_EDGE);
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

    public Vector2i[] getSize() {
        return this.size6;
    }

    @Override
    public int getSamplerId() {
        return this.samplerId;
    }

    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL46.GL_TEXTURE_CUBE_MAP;
    }

    @Override
    public long getBindingHandler() {
        return this.bindlessHandler;
    }

    public static final class Data implements IData {
        private final ImageTexture.Data[] dataSet;

        public Data(ImageTexture.Data[] dataSet) {
            this.dataSet = dataSet;
        }

        public void clear() {
            for (int i = 0; i < 6; i++) {
                this.getDataSet()[i].clear();
            }
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
