package javagems3d.system.resources.assets.loading.samples;

import com.google.common.io.ByteStreams;
import javagems3d.JGems3D;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.base.IImageTexture;
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

public class TexturesLoader {
    private final String name;

    public TexturesLoader(@Nullable String name) {
        this.name = name == null ? "unknowns" : name;
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull IImageTexture.Data data) {
        return new ImageTexture(textureProperties, data, this.getName());
    }

    public ImageTexture createImageTexture(@Nullable ImageTexture.Properties textureProperties, @NotNull JGemsPath pathToTexture) {
        return this.createImageTextureFromStream(textureProperties, JGems3D.loadFileFromJar(pathToTexture));
    }

    public ImageTexture createImageTextureFromStream(@Nullable ImageTexture.Properties textureProperties, @NotNull InputStream inputStream) {
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
            return this.createImageTexture(textureProperties, new IImageTexture.Data(imageBuffer, size));
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public String getName() {
        return this.name;
    }
}
