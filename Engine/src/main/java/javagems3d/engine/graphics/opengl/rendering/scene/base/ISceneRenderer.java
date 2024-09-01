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

package javagems3d.engine.graphics.opengl.rendering.scene.base;

import org.joml.Vector2i;
import javagems3d.engine.graphics.opengl.rendering.scene.render_base.SceneData;
import javagems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;

public interface ISceneRenderer {
    void onStartRender();

    void onRender(FrameTicking frameTicking, Vector2i windowSize);

    void onStopRender();

    void createResources(Vector2i windowSize);

    void destroyResources();

    void onWindowResize(Vector2i windowSize);

    SceneData getSceneData();
}
