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

package toolbox.resources.samples;

import com.google.common.io.ByteStreams;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import javagems3d.JGems3D;
import javagems3d.system.resources.assets.texturing.base.IImageTexture;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import logger.SystemLogging;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ImageTexture implements IImageTexture {
    private final String name;
    private int width;
    private int height;
    private int textureId;

    private ImageTexture(String name, int width, int height, ByteBuffer buffer) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.createTexture(buffer);
    }

    private ImageTexture(String fullPath) {
        this.name = fullPath;
        SystemLogging.get().getLogManager().log("Loading " + this.getName());
        try (InputStream inputStream = JGems3D.loadFileFromJar(new JGemsPath(fullPath))) {
            this.createTexture(this.readTextureFromMemory(this.getName(), inputStream));
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    private ImageTexture(String id, InputStream inputStream) {
        this.name = id + "_inputStream";
        this.createTexture(this.readTextureFromMemory(id, inputStream));
    }

    public static ImageTexture createTexture(ResourceCache resourceCache, String fullPath) {
        if (resourceCache.checkObjectInCache(fullPath)) {
            return (ImageTexture) resourceCache.getCachedObject(fullPath);
        }
        ImageTexture textureSample = new ImageTexture(fullPath);
        if (textureSample.isValid()) {
            resourceCache.addObjectInBuffer(fullPath, textureSample);
        } else {
            throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    public static ImageTexture createTexture(ResourceCache resourceCache, String name, int width, int height, ByteBuffer buffer) {
        if (resourceCache.checkObjectInCache(name)) {
            return (ImageTexture) resourceCache.getCachedObject(name);
        }
        ImageTexture textureSample = new ImageTexture(name, width, height, buffer);
        if (textureSample.isValid()) {
            resourceCache.addObjectInBuffer(name, textureSample);
        } else {
            throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    private ByteBuffer readTextureFromMemory(String name, InputStream inputStream) throws JGemsIOException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            byte[] stream = ByteStreams.toByteArray(inputStream);
            ByteBuffer buffer = MemoryUtil.memAlloc(stream.length);
            buffer.put(stream);
            buffer.flip();

            ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, width, height, channels, STBImage.STBI_rgb_alpha);
            MemoryUtil.memFree(buffer);
            if (imageBuffer == null) {
                throw new JGemsIOException("Couldn't create texture " + name + ". \n" + STBImage.stbi_failure_reason());
            } else {
                this.width = width.get();
                this.height = height.get();
                return imageBuffer;
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    private void createTexture(ByteBuffer buffer) {
        this.textureId = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, this.getTextureId());
        GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 1);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGBA, this.getSize().x, this.getSize().y, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, buffer);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR_MIPMAP_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_BASE_LEVEL, 0);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAX_LEVEL, 11);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_REPEAT);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_REPEAT);
        GL46.glGenerateMipmap(GL46.GL_TEXTURE_2D);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
        STBImage.stbi_image_free(buffer);
        SystemLogging.get().getLogManager().log("Texture " + this.getName() + " successfully created!");
    }

    public void clear() {
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
        GL46.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    public void bindTexture() {
        if (!this.isValid()) {
            throw new JGemsRuntimeException("Tried to bind invalid texture");
        }
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, this.getTextureId());
    }

    @Override
    public void init(@Nullable IProperties properties, javagems3d.system.resources.assets.texturing.ImageTexture.Data data) {

    }

    @Override
    public void reload(@Nullable IProperties properties) {

    }

    @Override
    public Vector2i getSize() {
        return new Vector2i(this.width, this.height);
    }

    public String getName() {
        return this.name;
    }

    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL46.GL_TEXTURE_2D;
    }

    public boolean isValid() {
        return this.getTextureId() != 0;
    }

    @Override
    public void onClearingCache(ResourceCache resourceCache) {
        this.clear();
    }
}
