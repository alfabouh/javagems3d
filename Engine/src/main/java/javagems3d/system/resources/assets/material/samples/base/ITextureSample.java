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

package javagems3d.system.resources.assets.material.samples.base;

import org.joml.Vector2i;
import javagems3d.system.resources.cache.ICached;

public interface ITextureSample extends ISample, ICached {
    int getTextureId();

    int getTextureAttachment();

    void bindTexture();

    Vector2i size();
}
