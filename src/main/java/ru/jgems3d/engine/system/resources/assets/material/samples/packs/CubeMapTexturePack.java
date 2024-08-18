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

package ru.jgems3d.engine.system.resources.assets.material.samples.packs;

import com.google.common.io.ByteStreams;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.service.exceptions.JGemsIOException;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.material.samples.TextureSample;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class CubeMapTexturePack {
    private final Data[] textures;
    private final String id;

    @SuppressWarnings("all")
    public CubeMapTexturePack(JGemsPath pathToCubeMap, String textureType) {
        this.textures = new Data[6];
        this.id = new JGemsPath(pathToCubeMap).getFullPath();
        for (int i = 0; i < 6; i++) {
            StringBuilder builder = new StringBuilder();
            builder.append(pathToCubeMap);
            builder.append(i + 1);
            builder.append(textureType);
            try (InputStream inputStream = JGems3D.loadFileFromJar(new JGemsPath(builder.toString()))) {
                this.textures[i] = this.readTextureFromMemory(inputStream);
                if (this.textures[i] == null) {
                    throw new JGemsIOException("Couldn't create texture " + pathToCubeMap + ". \n" + STBImage.stbi_failure_reason());
                }
            } catch (IOException e) {
                throw new JGemsIOException(e);
            }
        }
    }

    private Data readTextureFromMemory(InputStream inputStream) throws JGemsIOException {
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
            return new Data(new Vector2i(width.get(), height.get()), buffer1);
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public void freeBuffers() {
        for (Data buffer : this.textures) {
            STBImage.stbi_image_free(buffer.buffer);
        }
    }

    public Data[] getTextureArray() {
        return this.textures;
    }

    public String getId() {
        return this.id;
    }

    public static class Data {
        public final Vector2i size;
        public final ByteBuffer buffer;

        public Data(Vector2i size, ByteBuffer buffer) {
            this.size = size;
            this.buffer = buffer;
        }
    }
}
