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

package javagems3d.system.resources.assets.texturing.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import javagems3d.system.resources.cache.ICached;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

public interface IImageTexture extends ISample, ICached {
    int getTextureId();
    int getTextureAttachment();
    void bindTexture();
    void init(@Nullable IProperties properties, Data data);
    void reload(@Nullable IProperties properties);
    Vector2i getSize();

    default boolean isValid() {
        return this.getTextureId() > 0;
    }

    interface IProperties {

    }

    class Data {
        private final ByteBuffer buffer;
        private final Vector2i size;

        public Data(@NotNull ByteBuffer buffer, @NotNull Vector2i size) {
            this.buffer = buffer;
            this.size = size;
        }

        public void clear() {
            STBImage.stbi_image_free(this.getBuffer());
        }

        public ByteBuffer getBuffer() {
            return this.buffer;
        }

        public Vector2i getSize() {
            return this.size;
        }
    }
}
