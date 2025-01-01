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

import javagems3d.system.resources.assets.texturing.ImageTexture;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import javagems3d.system.resources.cache.ICached;
import org.lwjgl.opengl.GL46;

public interface IImageTexture extends ISample, ICached {
    int getTextureId();
    int getTextureAttachment();
    void bindTexture();
    void init(@Nullable IProperties properties, IData data);
    void reload(@Nullable IProperties properties);
    Vector2i getSize();

    default void unBindTexture() {
        GL46.glBindTexture(this.getTextureAttachment(), 0);
    }

    default boolean isValid() {
        return this.getTextureId() > 0;
    }
}
