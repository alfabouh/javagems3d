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

package javagems3d.engine.graphics.opengl.environment.sky.skybox;

import javagems3d.engine.system.resources.assets.material.samples.CubeMapSample;

public class SkyBox2D implements ISkyBox {
    private CubeMapSample cubeMapTexture;

    public SkyBox2D(CubeMapSample cubeMapTexture) {
        this.cubeMapTexture = cubeMapTexture;
    }

    public void setCubeMapTexture(CubeMapSample cubeMapTexture) {
        this.cubeMapTexture = cubeMapTexture;
    }

    @Override
    public CubeMapSample cubeMapTexture() {
        return this.cubeMapTexture;
    }

    @Override
    public SkyboxType skyboxType() {
        return SkyboxType.D2D;
    }
}
