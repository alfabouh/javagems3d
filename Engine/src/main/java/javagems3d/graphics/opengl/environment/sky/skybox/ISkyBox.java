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

package javagems3d.graphics.opengl.environment.sky.skybox;

import javagems3d.system.resources.assets.material.samples.CubeMapSample;

public interface ISkyBox {
    CubeMapSample cubeMapTexture();

    SkyboxType skyboxType();

    enum SkyboxType {
        D3D,
        D2D
    }
}
