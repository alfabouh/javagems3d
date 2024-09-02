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

package javagems3d.graphics.opengl.rendering.programs.textures;

import org.lwjgl.opengl.GL30;

public interface ITextureProgram {
    int getTextureId();

    void cleanUp();

    default void bindTexture(int code) {
        GL30.glBindTexture(code, this.getTextureId());
    }

    default void unBindTexture() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }
}
