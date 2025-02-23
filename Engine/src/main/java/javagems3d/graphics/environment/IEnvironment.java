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

package javagems3d.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.fog.FogManager;
import javagems3d.graphics.environment.fog.IFogManager;
import javagems3d.graphics.environment.lights.scene.ILightsScene;
import javagems3d.graphics.environment.lights.scene.LightsScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;

public interface IEnvironment {
    void createEnvironment(OpenGLRenderer openGLRenderer);
    void updateEnvironment(ICamera camera);
    void destroyEnvironment();

    IWorld getWorld();
    IShadowScene getShadowScene();
    ILightsScene getLightManager();
    IFogManager getFogManager();
    ISkyBox getSkyBox();
}