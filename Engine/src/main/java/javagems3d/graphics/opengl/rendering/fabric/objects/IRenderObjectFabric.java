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

package javagems3d.graphics.opengl.rendering.fabric.objects;

import javagems3d.graphics.opengl.rendering.items.IRenderObject;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;

public interface IRenderObjectFabric {
    void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, IRenderObject renderItem);

    void onPreRender(IRenderObject renderItem);

    void onPostRender(IRenderObject renderItem);
}
