package ru.jgems3d.toolbox.resources.samples;

import com.google.common.io.ByteStreams;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.exceptions.JGemsException;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.logger.SystemLogging;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureSample implements IImageSample {
    private final String name;
    private ByteBuffer imageBuffer;
    private int width;
    private int height;
    private int textureId;

    private TextureSample(String name, int width, int height, ByteBuffer buffer) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.imageBuffer = buffer;

        if (this.imageBuffer != null) {
            this.createTexture();
        }
    }

    private TextureSample(boolean inJar, String fullPath) {
        this.name = fullPath;
        SystemLogging.get().getLogManager().log("Loading " + this.getName());
        if (inJar) {
            try (InputStream inputStream = JGems3D.loadFileJar(new JGPath(fullPath))) {
                this.imageBuffer = this.readTextureFromMemory(this.getName(), inputStream);
                if (this.imageBuffer != null) {
                    this.createTexture();
                }
            } catch (IOException e) {
                throw new JGemsException(e);
            }
        } else {
            this.imageBuffer = this.readTextureOutsideJar(this.getName());
            if (this.imageBuffer != null) {
                this.createTexture();
            }
        }
    }

    private TextureSample(String id, InputStream inputStream) {
        this.name = id + "_inputStream";
        if (inputStream == null) {
            SystemLogging.get().getLogManager().warn("Error, while loading texture " + id + " InputStream is NULL");
            this.imageBuffer = null;
        } else {
            this.imageBuffer = this.readTextureFromMemory(id, inputStream);
            if (this.imageBuffer != null) {
                this.createTexture();
            }
        }
    }

    public static TextureSample createTexture(boolean inJar, ResourceCache resourceCache, String fullPath) {
        if (resourceCache.checkObjectInCache(fullPath)) {
            return (TextureSample) resourceCache.getCachedObject(fullPath);
        }
        TextureSample textureSample = new TextureSample(inJar, fullPath);
        if (textureSample.isValid()) {
            resourceCache.addObjectInBuffer(fullPath, textureSample);
        } else {
            throw new JGemsException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    public static TextureSample createTexture(ResourceCache resourceCache, String name, int width, int height, ByteBuffer buffer) {
        if (resourceCache.checkObjectInCache(name)) {
            return (TextureSample) resourceCache.getCachedObject(name);
        }
        TextureSample textureSample = new TextureSample(name, width, height, buffer);
        if (textureSample.isValid()) {
            resourceCache.addObjectInBuffer(name, textureSample);
        } else {
            throw new JGemsException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    private ByteBuffer readTextureFromMemory(String name, InputStream inputStream) {
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
                SystemLogging.get().getLogManager().warn("Couldn't create texture " + name);
                SystemLogging.get().getLogManager().bigWarn(STBImage.stbi_failure_reason());
            } else {
                this.width = width.get();
                this.height = height.get();
                return imageBuffer;
            }
        } catch (IOException e) {
            throw new JGemsException(e);
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
                SystemLogging.get().getLogManager().warn("Couldn't create texture " + path);
                SystemLogging.get().getLogManager().bigWarn(STBImage.stbi_failure_reason());
            } else {
                this.width = width.get();
                this.height = height.get();
                return imageBuffer;
            }
        }
        return null;
    }

    private void createTexture() {
        this.textureId = GL20.glGenTextures();
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
        GL20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
        GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, this.size().x, this.size().y, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, this.getImageBuffer());
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST_MIPMAP_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_BASE_LEVEL, 0);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 6);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
        GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        SystemLogging.get().getLogManager().log("Texture " + this.getName() + " successfully created!");
    }

    public void recreateTexture() {
        GL30.glDeleteTextures(this.getTextureId());
        this.createTexture();
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
            throw new JGemsException("Tried to bind invalid texture");
        }
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.getTextureId());
    }

    @Override
    public Vector2i size() {
        return new Vector2i(this.width, this.height);
    }

    public String getName() {
        return this.name;
    }

    public int getTextureId() {
        return this.textureId;
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
}
