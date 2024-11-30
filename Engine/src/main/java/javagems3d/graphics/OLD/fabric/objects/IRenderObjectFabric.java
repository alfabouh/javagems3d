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

package javagems3d.graphics.OLD.fabric.objects;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.OLD.render_base.SceneRenderBase;
import javagems3d.graphics.screen.ticking.FrameTicking;

public interface IRenderObjectFabric {
    void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, IRendered renderItem);

    void onPreRender(IRendered renderItem);

    void onPostRender(IRendered renderItem);
}
