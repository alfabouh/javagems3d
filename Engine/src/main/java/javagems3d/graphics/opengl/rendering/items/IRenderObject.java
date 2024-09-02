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

package javagems3d.graphics.opengl.rendering.items;

import javagems3d.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;

public interface IRenderObject {
    IRenderObjectFabric renderFabric();

    default boolean hasRender() {
        return this.renderFabric() != null;
    }
}
