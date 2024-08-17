package ru.jgems3d.engine.graphics.opengl.environment.sky.skybox;

import ru.jgems3d.engine.system.resources.assets.material.samples.CubeMapSample;

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
