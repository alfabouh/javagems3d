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

package javagems3d.graphics.opengl.environment;

import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneData;
import javagems3d.graphics.opengl.world.SceneWorld;

public interface IEnvironment {
    void createEnvironment(SceneWorld sceneWorld);
    void updateEnvironment(SceneWorld sceneWorld, ICamera camera);
    void destroyEnvironment(SceneWorld sceneWorld);
}
