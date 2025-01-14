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
    default void bindSampler(int unit) {
        GL46.glBindSampler(unit, this.getSamplerId());
    }

    default void unBindSampler(int unit) {
        GL46.glBindSampler(unit, 0);
    }

    default void bindTexture() {
        GL46.glBindTexture(this.getTextureAttachment(), this.getTextureId());
    }

    default void unBindTexture() {
        GL46.glBindTexture(this.getTextureAttachment(), 0);
    }

    int getSamplerId();
    int getTextureId();
    int getTextureAttachment();

    default boolean isValid() {
        return this.getTextureId() > 0;
    }

    void clear();
}
