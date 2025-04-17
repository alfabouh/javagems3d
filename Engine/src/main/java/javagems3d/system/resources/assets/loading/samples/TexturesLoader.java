package javagems3d.system.resources.assets.loading.samples;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TexturesLoader implements ILoadingHelper {
    private String hashId;
    private final SystemResources systemResources;

    public TexturesLoader(@Nullable SystemResources systemResources, @Nullable String hashId) {
        this.hashId = hashId == null ? ILoadingHelper.DEFAULT_NAME : hashId;
        this.systemResources = systemResources;
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull ImageTexture.Data data) {
        return this.createImageTexture(textureProperties, data, this.getHashId());
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull ImageTexture.Data data, @NotNull String name) {
        if (this.isCacheValid() && !name.equals(ILoadingHelper.DEFAULT_NAME)) {
            if (this.getResourceCache().checkObjectInCache(name)) {
                Log.get().info("Texture " + this.getHashId() + " picked from cache");
                return this.getResourceCache().getCachedObjectUnSafeCast(name);
            }
        }
        ImageTexture imageTexture = new ImageTexture(textureProperties, data);
        Log.get().info("Texture " + this.getHashId() + " successfully created");
        if (this.isCacheValid()) {
            if (name.equals(ILoadingHelper.DEFAULT_NAME)) {
                this.getResourceCache().addObjectInBuffer(imageTexture.toString(), imageTexture);
            } else {
                this.getResourceCache().addObjectInBuffer(name, imageTexture);
            }
            this.getGameResources().getResourceArrays().getBindlessTexturesArray().add(imageTexture);
        }
        return imageTexture;
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull JGemsPath pathToTexture) {
        this.hashId = pathToTexture.toString();
        try (InputStream inputStream = JGems3D.loadFileFromJar(pathToTexture)) {
            return this.createImageTexture(textureProperties, inputStream);
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull InputStream inputStream) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer buffer = JGemsFilesHelper.toByteBuffer(inputStream);
            ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, width, height, channels, STBImage.STBI_rgb_alpha);
            MemoryUtil.memFree(buffer);
            if (imageBuffer == null) {
                throw new JGemsIOException("Couldn't create texture " + this.getHashId() + ". \n" + STBImage.stbi_failure_reason());
            }
            Vector2i size = new Vector2i(width.get(), height.get());
            return this.createImageTexture(textureProperties, new ImageTexture.Data(imageBuffer, size), this.getHashId());
        } catch (Exception e) {
            throw new JGemsIOException(e);
        }
    }

    public SystemResources getGameResources() {
        return this.systemResources;
    }

    public ResourceCache getResourceCache() {
        return this.getGameResources() == null ? null : this.getGameResources().getResourceCache();
    }

    public String getHashId() {
        return this.hashId;
    }
}
