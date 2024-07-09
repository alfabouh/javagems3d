package ru.alfabouh.jgems3d.engine.graphics.opengl.environment.sky.skybox;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.programs.textures.CubeMapProgram;

public interface ISkyBox {
    CubeMapProgram cubeMapTexture();

    SkyboxType skyboxType();

    enum SkyboxType {
        D3D,
        D2D
    }
}
