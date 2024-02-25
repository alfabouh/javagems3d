package ru.BouH.engine.render.environment.sky.skybox;

import ru.BouH.engine.render.scene.programs.CubeMapProgram;

public interface ISkyBox {
    CubeMapProgram cubeMapTexture();

    SkyboxType skyboxType();

    enum SkyboxType {
        D3D,
        D2D
    }
}
