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

package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base;

import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;

public final class SceneData {
    private ICamera camera;
    private SceneWorld sceneWorld;

    public SceneData(SceneWorld sceneWorld, ICamera camera) {
        this.sceneWorld = sceneWorld;
        this.camera = camera;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public void setSceneWorld(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
    }

    public ICamera getCamera() {
        return this.camera;
    }

    public void setCamera(ICamera camera) {
        this.camera = camera;
    }
}
