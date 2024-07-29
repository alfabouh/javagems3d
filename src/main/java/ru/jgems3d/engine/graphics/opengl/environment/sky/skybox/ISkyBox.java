package ru.jgems3d.engine.graphics.opengl.environment.sky.skybox;

import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.CubeMapProgram;

public interface ISkyBox {
    CubeMapProgram cubeMapTexture();

    SkyboxType skyboxType();

    enum SkyboxType {
        D3D,
        D2D
    }
}
