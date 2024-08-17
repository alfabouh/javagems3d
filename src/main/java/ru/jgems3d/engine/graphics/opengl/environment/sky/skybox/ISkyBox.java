package ru.jgems3d.engine.graphics.opengl.environment.sky.skybox;

import ru.jgems3d.engine.system.resources.assets.material.samples.CubeMapSample;

public interface ISkyBox {
    CubeMapSample cubeMapTexture();

    SkyboxType skyboxType();

    enum SkyboxType {
        D3D,
        D2D
    }
}
