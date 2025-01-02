package javagems3d.system.resources.assets.loading.samples;

import com.google.common.io.ByteStreams;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
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
    private String name;
    private final ResourceCache resourceCache;

    public TexturesLoader(@Nullable ResourceCache resourceCache, @Nullable String name) {
        this.name = name == null ? ILoadingHelper.DEFAULT_NAME : name;
        this.resourceCache = resourceCache;
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull ImageTexture.Data data) {
        return this.createImageTexture(textureProperties, data, this.getName());
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull ImageTexture.Data data, @NotNull String name) {
        if (this.isCacheValid() && !name.equals(ILoadingHelper.DEFAULT_NAME)) {
            if (this.getResourceCache().checkObjectInCache(name)) {
                JGemsHelper.getLogger().log("Texture " + this.getName() + " picked from cache!");
                return this.getResourceCache().getCachedObjectUnSafeCast(name);
            }
        }
        ImageTexture imageTexture = new ImageTexture(textureProperties, data);
        JGemsHelper.getLogger().log("CubeMap " + this.getName() + " successfully created!");
        if (this.isCacheValid()) {
            if (name.equals(ILoadingHelper.DEFAULT_NAME)) {
                this.getResourceCache().addObjectInBuffer(imageTexture.toString(), imageTexture);
            } else {
                this.getResourceCache().addObjectInBuffer(name, imageTexture);
            }
        }
        return imageTexture;
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull JGemsPath pathToTexture) {
        this.name = pathToTexture.toString();
        return this.createImageTexture(textureProperties, JGems3D.loadFileFromJar(pathToTexture));
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull InputStream inputStream) {
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
                throw new JGemsIOException("Couldn't create texture " + this.getName() + ". \n" + STBImage.stbi_failure_reason());
            }
            Vector2i size = new Vector2i(width.get(), height.get());
            return this.createImageTexture(textureProperties, new ImageTexture.Data(imageBuffer, size), this.getName());
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public ResourceCache getResourceCache() {
        return this.resourceCache;
    }

    public String getName() {
        return this.name;
    }
}
