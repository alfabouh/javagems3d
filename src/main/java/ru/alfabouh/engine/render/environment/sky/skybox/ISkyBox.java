package ru.alfabouh.engine.render.environment.sky.skybox;

import ru.alfabouh.engine.render.scene.programs.CubeMapProgram;

public interface ISkyBox {
    CubeMapProgram cubeMapTexture();

    SkyboxType skyboxType();

    enum SkyboxType {
        D3D,
        D2D
    }
}
