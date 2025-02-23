package javagems3d.system.resources.assets.loading.samples;

import com.google.common.io.ByteStreams;
import javagems3d.JGems3D;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.service.collections.Pair;
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

public class CubeMapsLoader implements ILoadingHelper {
    public static final String DEFAULT_NAME = "unknown";
    private String hashId;
    private final GameResources gameResources;

    public CubeMapsLoader(@Nullable GameResources gameResources, @Nullable String hashId) {
        this.hashId = hashId == null ? ILoadingHelper.DEFAULT_NAME : hashId;
        this.gameResources = gameResources;
    }

    public CubeMapTexture createCubeMapTexture(@Nullable CubeMapTexture.Properties textureProperties, @NotNull CubeMapTexture.Data data) {
        return this.createCubeMapTexture(textureProperties, data, this.getHashId());
    }

    public CubeMapTexture createCubeMapTexture(@Nullable CubeMapTexture.Properties textureProperties, @NotNull CubeMapTexture.Data data, @NotNull String name) {
        if (this.isCacheValid() && !name.equals(ILoadingHelper.DEFAULT_NAME)) {
            if (this.getResourceCache().checkObjectInCache(name)) {
                Log.get().info("CubeMap " + this.getHashId() + " picked from cache");
                return this.getResourceCache().getCachedObjectUnSafeCast(name);
            }
        }
        CubeMapTexture cubeMapTexture = new CubeMapTexture(textureProperties, data);
        Log.get().info("CubeMap " + this.getHashId() + " successfully created");
        if (this.isCacheValid()) {
            if (name.equals(ILoadingHelper.DEFAULT_NAME)) {
                this.getResourceCache().addObjectInBuffer(cubeMapTexture.toString(), cubeMapTexture);
            } else {
                this.getResourceCache().addObjectInBuffer(name, cubeMapTexture);
            }
        }
        return cubeMapTexture;
    }

    public CubeMapTexture createCubeMapTexture(@Nullable CubeMapTexture.Properties textureProperties, @NotNull JGemsPath pathToCubeMapFile, @NotNull String textureDescriptor) {
        ImageTexture.Data[] dataSet = new ImageTexture.Data[6];
        if (textureDescriptor.isEmpty()) {
            textureDescriptor = "png";
        }
        for (int i = 0; i < 6; i++) {
            StringBuilder builder = new StringBuilder();
            builder.append(pathToCubeMapFile);
            builder.append(i + 1);
            if (!textureDescriptor.contains(".")) {
                builder.append(".");
            }
            builder.append(textureDescriptor);
            this.hashId = builder.toString();
            try (InputStream inputStream = JGems3D.loadFileFromJar(new JGemsPath(builder.toString()))) {
                Pair<ByteBuffer, Vector2i> pair = this.readTextureFromMemory(inputStream);
                if (pair == null || pair.getFirst() == null) {
                    throw new JGemsIOException("Couldn't create texture " + this.getHashId() + ". \n" + STBImage.stbi_failure_reason());
                }
                dataSet[i] = new ImageTexture.Data(pair.getFirst(), pair.getSecond());
            } catch (IOException e) {
                throw new JGemsIOException(e);
            }
        }
        return this.createCubeMapTexture(textureProperties, new CubeMapTexture.Data(dataSet));
    }

    private Pair<ByteBuffer, Vector2i> readTextureFromMemory(InputStream inputStream) throws JGemsIOException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            byte[] stream = ByteStreams.toByteArray(inputStream);
            ByteBuffer buffer = MemoryUtil.memAlloc(stream.length);
            buffer.put(stream);
            buffer.flip();

            ByteBuffer buffer1 = STBImage.stbi_load_from_memory(buffer, width, height, channels, STBImage.STBI_rgb_alpha);
            MemoryUtil.memFree(buffer);
            if (buffer1 == null) {
                return null;
            }
            return new Pair<>(buffer1, new Vector2i(width.get(), height.get()));
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public GameResources getGameResources() {
        return this.gameResources;
    }

    public ResourceCache getResourceCache() {
        return this.getGameResources() == null ? null : this.getGameResources().getResourceCache();
    }

    public String getHashId() {
        return this.hashId;
    }
}
