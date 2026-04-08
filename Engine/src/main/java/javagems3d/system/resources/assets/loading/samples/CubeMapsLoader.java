package javagems3d.system.resources.assets.loading.samples;

import com.google.common.io.ByteStreams;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
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
    private String hashId;
    private final SystemResources systemResources;

    public CubeMapsLoader(@Nullable SystemResources systemResources, @Nullable String hashId) {
        this.hashId = hashId == null ? ILoadingHelper.DEFAULT_NAME : hashId;
        this.systemResources = systemResources;
    }

    public CubeMapTexture createCubeMapTexture(@Nullable CubeMapTexture.Properties textureProperties, @NotNull CubeMapTexture.Data data) {
        return this.createCubeMapTexture(textureProperties, data, this.getHashId());
    }

    public CubeMapTexture createCubeMapTexture(@Nullable CubeMapTexture.Properties textureProperties, @NotNull CubeMapTexture.Data data, @NotNull String name) {
        if (this.isCacheValid() && !name.equals(ILoadingHelper.DEFAULT_NAME)) {
            if (this.getResourceCache().checkObjectInCache(name, CubeMapTexture.class)) {
                Log.get().info("CubeMap " + this.getHashId() + " picked from cache");
                return this.getResourceCache().getCachedObjectUnSafeCast(name);
            }
        }
        CubeMapTexture cubeMapTexture = new CubeMapTexture(textureProperties, data);
        Log.get().info("CubeMap " + this.getHashId() + " successfully created");
        if (this.isCacheValid()) {
            if (name.equals(ILoadingHelper.DEFAULT_NAME)) {
                this.getResourceCache().registerInCache(cubeMapTexture.toString(), cubeMapTexture);
            } else {
                this.getResourceCache().registerInCache(name, cubeMapTexture);
            }
        }
        return cubeMapTexture;
    }

    public CubeMapTexture createCubeMapTexture(@Nullable CubeMapTexture.Properties textureProperties, @NotNull CubeMapTexturesContainer cubeMapTexturesContainer) {
        ImageTexture.Data[] dataSet = new ImageTexture.Data[6];
        for (int i = 0; i < 6; i++) {
            JGemsPathSource gemsPathSource = cubeMapTexturesContainer.getTextures()[i];
            this.hashId = gemsPathSource.getPath().toString();
            try (InputStream inputStream = JGems3D.getInputStream(gemsPathSource)) {
                Pair<ByteBuffer, Vector2i> pair = this.readTextureFromMemory(inputStream);
                if (pair == null || pair.first() == null) {
                    throw new JGemsIOException("Couldn't create texture " + this.getHashId() + ". \n" + STBImage.stbi_failure_reason());
                }
                dataSet[i] = new ImageTexture.Data(pair.first(), pair.second());
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

    public SystemResources getGameResources() {
        return this.systemResources;
    }

    public ResourceCache getResourceCache() {
        return this.getGameResources() == null ? null : this.getGameResources().getResourceCache();
    }

    public String getHashId() {
        return this.hashId;
    }

    public static class CubeMapTexturesContainer {
        private final JGemsPathSource[] textures;

        public CubeMapTexturesContainer(@NotNull JGemsPath parent, @NotNull ICubeMapProgram.CMTextures textures) {
            this.textures = new JGemsPathSource[] {
                    new JGemsPathSource(new JGemsPath(parent, textures.getTextureFRONTPath().getPath().toString()), textures.getTextureFRONTPath().getSource()),
                    new JGemsPathSource(new JGemsPath(parent, textures.getTextureBACKPath().getPath().toString()), textures.getTextureBACKPath().getSource()),
                    new JGemsPathSource(new JGemsPath(parent, textures.getTextureUPPath().getPath().toString()), textures.getTextureUPPath().getSource()),
                    new JGemsPathSource(new JGemsPath(parent, textures.getTextureBOTTOMPath().getPath().toString()), textures.getTextureBOTTOMPath().getSource()),
                    new JGemsPathSource(new JGemsPath(parent, textures.getTextureLEFTPath().getPath().toString()), textures.getTextureLEFTPath().getSource()),
                    new JGemsPathSource(new JGemsPath(parent, textures.getTextureRIGHTPath().getPath().toString()), textures.getTextureRIGHTPath().getSource())
            };
        }

        public CubeMapTexturesContainer(@NotNull ICubeMapProgram.CMTextures textures) {
            this.textures = new JGemsPathSource[] {textures.getTextureFRONTPath(), textures.getTextureBACKPath(), textures.getTextureUPPath(), textures.getTextureBOTTOMPath(), textures.getTextureLEFTPath(), textures.getTextureRIGHTPath()};
        }

        public JGemsPathSource[] getTextures() {
            return this.textures;
        }
    }
}
