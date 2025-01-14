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

import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.system.resources.cache.ICached;
import org.lwjgl.opengl.GL46;

public interface ImageBasedTexture extends ITextureProgram, ISample, ICached {
    void init(@Nullable IProperties properties, @NotNull IData data);
    void reload(@Nullable IProperties properties);
}
