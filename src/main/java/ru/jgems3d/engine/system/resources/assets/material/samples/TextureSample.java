package ru.jgems3d.engine.system.resources.assets.material.samples;

import com.google.common.io.ByteStreams;
import org.joml.Vector2i;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.exceptions.JGemsIOException;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.engine.system.service.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureSample implements IImageSample {
    private final String name;
    private ByteBuffer imageBuffer;
    private Vector2i size;
    private int textureId;
    private final Params params;

    private TextureSample(String name, Vector2i size, ByteBuffer buffer, Params params) {
        this.name = name;
        this.size = size;
        this.imageBuffer = buffer;

        this.params = params;

        if (this.imageBuffer != null) {
            this.createTexture(params);
        }
    }

    private TextureSample(boolean inJar, JGPath fullPath, Params params) {
        this.name = fullPath.getSPath();
        this.params = params;
        JGemsHelper.getLogger().log("Loading " + this.getName());
        if (inJar) {
            try (InputStream inputStream = JGems3D.loadFileFromJar(fullPath)) {
                this.imageBuffer = this.readTextureFromMemory(this.getName(), inputStream);
                if (this.imageBuffer != null) {
                    this.createTexture(params);
                }
            } catch (IOException e) {
                this.imageBuffer = null;
                throw new JGemsIOException(e);
            }
        } else {
            this.imageBuffer = this.readTextureOutsideJar(this.getName());
            if (this.imageBuffer != null) {
                this.createTexture(params);
            }
        }
    }

    private TextureSample(String id, InputStream inputStream, Params params) {
        this.name = id + "_inputStream";
        this.params = params;
        if (inputStream == null) {
            JGemsHelper.getLogger().warn("Error, while loading texture " + id + " InputStream is NULL");
            this.imageBuffer = null;
        } else {
            try {
                this.imageBuffer = this.readTextureFromMemory(id, inputStream);
            } catch (IOException e) {
                throw new JGemsIOException(e);
            }
            if (this.imageBuffer != null) {
                this.createTexture(params);
            }
        }
    }

    public static TextureSample createTextureOutsideJar(ResourceCache resourceCache, JGPath fullPath, Params params) {
        if (resourceCache.checkObjectInCache(fullPath)) {
            return (TextureSample) resourceCache.getCachedObject(fullPath);
        }
        TextureSample textureSample = new TextureSample(false, fullPath, params);
        if (textureSample.isValid()) {
            resourceCache.addObjectInBuffer(fullPath, textureSample);
        } else {
            throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    public static TextureSample createTexture(ResourceCache resourceCache, JGPath fullPath, Params params) {
        if (resourceCache.checkObjectInCache(fullPath)) {
            return (TextureSample) resourceCache.getCachedObject(fullPath);
        }
        TextureSample textureSample = new TextureSample(true, fullPath, params);
        if (textureSample.isValid()) {
            resourceCache.addObjectInBuffer(fullPath, textureSample);
        } else {
            throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    public static TextureSample createTexture(ResourceCache resourceCache, String name, Vector2i size, ByteBuffer buffer, Params params) {
        if (resourceCache.checkObjectInCache(name)) {
            return (TextureSample) resourceCache.getCachedObject(name);
        }
        TextureSample textureSample = new TextureSample(name, size, buffer, params);
        if (textureSample.isValid()) {
            resourceCache.addObjectInBuffer(name, textureSample);
        } else {
            throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    public static TextureSample createTexture(String id, InputStream inputStream, Params params) {
        return new TextureSample(id, inputStream, params);
    }

    private ByteBuffer readTextureFromMemory(String name, InputStream inputStream) throws IOException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            byte[] stream = ByteStreams.toByteArray(inputStream);
            ByteBuffer buffer = MemoryUtil.memAlloc(stream.length);
            buffer.put(stream);
            buffer.flip();

            ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, width, height, channels, STBImage.STBI_rgb_alpha);
            if (imageBuffer == null) {
                JGemsHelper.getLogger().warn("Couldn't create texture " + name);
                JGemsHelper.getLogger().bigWarn(STBImage.stbi_failure_reason());
            } else {
                this.size = new Vector2i(width.get(), height.get());
                return imageBuffer;
            }
        }
        return null;
    }

    private ByteBuffer readTextureOutsideJar(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer imageBuffer = STBImage.stbi_load(path, width, height, channels, STBImage.STBI_rgb_alpha);
            if (imageBuffer == null) {
                JGemsHelper.getLogger().warn("Couldn't create texture " + path);
                JGemsHelper.getLogger().bigWarn(STBImage.stbi_failure_reason());
            } else {
                this.size = new Vector2i(width.get(), height.get());
                return imageBuffer;
            }
        }
        return null;
    }

    private void createTexture(Params params) {
        int quality = params.isQualityAffected() ? (2 - JGems3D.get().getGameSettings().texturesQuality.getValue()) : 0;
        boolean linear = params.isLinearFilter() && JGems3D.get().getGameSettings().texturesFiltering.getValue() == 1;
        boolean anisotropic = params.isAnisotropicFilter() && JGems3D.get().getGameSettings().anisotropic.getValue() == 1;

        this.textureId = GL20.glGenTextures();
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
        GL20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
        GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, this.size().x, this.size().y, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, this.getImageBuffer());
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, linear ? GL30.GL_LINEAR_MIPMAP_LINEAR : GL30.GL_NEAREST_MIPMAP_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, linear ? GL30.GL_LINEAR : GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, params.isRepeat() ? GL30.GL_REPEAT : GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, params.isRepeat() ? GL30.GL_REPEAT : GL30.GL_CLAMP_TO_EDGE);
        if (anisotropic) {
            GL30.glTexParameterf(GL30.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GL30.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        }
        GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_BASE_LEVEL, quality);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 11);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        JGemsHelper.getLogger().log("Texture " + this.getName() + " successfully created!");
    }

    public void recreateTexture() {
        GL30.glDeleteTextures(this.getTextureId());
        this.createTexture(this.params);
    }

    public void clear() {
        if (this.isValid()) {
            STBImage.stbi_image_free(this.getImageBuffer());
            this.imageBuffer = null;
        }
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        GL30.glDeleteTextures(this.getTextureId());
    }

    public void bindTexture() {
        if (!this.isValid()) {
            throw new JGemsRuntimeException("Tried to bind invalid texture");
        }
        GL30.glBindTexture(this.getTextureAttachment(), this.getTextureId());
    }

    @Override
    public Vector2i size() {
        return new Vector2i(this.size);
    }

    public String getName() {
        return this.name;
    }

    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL30.GL_TEXTURE_2D;
    }

    public boolean isValid() {
        return this.getImageBuffer() != null;
    }

    public ByteBuffer getImageBuffer() {
        return this.imageBuffer;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clear();
    }

    public static class Params {
        private final boolean linearFilter;
        private final boolean repeat;
        private final boolean anisotropicFilter;
        private final boolean qualityAffected;

        public Params(boolean qualityAffected) {
            this(true, true, true, qualityAffected);
        }

        public Params() {
            this(true, true, true, false);
        }

        public Params(boolean linearFilter, boolean repeat, boolean anisotropicFilter, boolean qualityAffected) {
            this.linearFilter = linearFilter;
            this.repeat = repeat;
            this.anisotropicFilter = anisotropicFilter;
            this.qualityAffected = qualityAffected;
        }

        public boolean isLinearFilter() {
            return this.linearFilter;
        }

        public boolean isRepeat() {
            return this.repeat;
        }

        public boolean isAnisotropicFilter() {
            return this.anisotropicFilter;
        }

        public boolean isQualityAffected() {
            return this.qualityAffected;
        }
    }
}