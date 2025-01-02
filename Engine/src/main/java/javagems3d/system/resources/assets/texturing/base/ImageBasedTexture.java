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
import javagems3d.system.resources.cache.ICached;
import org.lwjgl.opengl.GL46;

public abstract class ImageBasedTexture implements ISample, ICached {
    public ImageBasedTexture() {
    }

    public void bindTexture() {
        GL46.glBindTexture(this.getTextureAttachment(), this.getTextureId());
    }

    public void unBindTexture() {
        GL46.glBindTexture(this.getTextureAttachment(), 0);
    }

    public abstract int getTextureId();
    public abstract int getTextureAttachment();

    public boolean isValid() {
        return this.getTextureId() > 0;
    }

    protected abstract void init(@Nullable IProperties properties, @NotNull IData data);
    public abstract void reload(@Nullable IProperties properties);
}
