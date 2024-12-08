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

package javagems3d.graphics.rendering.programs.textures;

import org.lwjgl.opengl.GL46;

public interface ITextureProgram {
    int getTextureId();

    void clear();

    default void bindTexture(int code) {
        GL46.glBindTexture(code, this.getTextureId());
    }

    default void unBindTexture() {
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
    }
}
