package javagems3d.system.resources.assets.texturing.maps;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import javagems3d.system.resources.assets.texturing.IPropertiesSample;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

public class CubeMapTexture implements ICached, IPropertiesSample, ICubeMapProgram, ITextureBindless {
    protected Vector2i[] size6x;
    protected int textureId;
    protected int samplerId;
    protected long bindlessHandler;
    protected IProperties properties;

    public CubeMapTexture(@Nullable CubeMapTexture.Properties textureProperties, @NotNull CubeMapTexture.Data data) {
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
        this.properties = new CubeMapTexture.Properties(true);
        this.init(textureProperties, data);
    }

    private void init(@Nullable IProperties properties, @NotNull Data data) {
        this.size6x = new Vector2i[6];
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        for (int i = 0; i < 6; i++) {
            this.size6x[i] = data.getDataSet()[i].getSize();
            GL46.glTexImage2D(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL46.GL_RGB16, this.getSize()[i].x, this.getSize()[i].y, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, data.getDataSet()[i].getBuffer());
        }
        data.clear();
        this.unBindTexture();
        this.setProperties(properties, true);

        this.createBindlessHandling();
    }

    public void setProperties(IProperties properties, boolean update) {
        if (properties != null && update) {
            this.properties = properties;
        }
        CubeMapTexture.Properties properties1 = (CubeMapTexture.Properties) this.getProperties();
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
    public void reload(@Nullable IProperties properties, boolean update) {
        this.setProperties(properties, update);
    }

    @Override
    public IProperties getProperties() {
        return this.properties;
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
        return this.size6x;
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

    public static final class Data {
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

    public static final class Properties implements IProperties {
        private final boolean linearFiltration;

        public Properties(boolean linearFilter) {
            this.linearFiltration = linearFilter;
        }

        public boolean isLinearFiltration() {
            return this.linearFiltration;
        }
    }
}
