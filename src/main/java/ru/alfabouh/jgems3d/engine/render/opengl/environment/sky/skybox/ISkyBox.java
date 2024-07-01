package ru.alfabouh.jgems3d.engine.render.opengl.environment.sky.skybox;

import ru.alfabouh.jgems3d.engine.render.opengl.scene.programs.CubeMapProgram;

public interface ISkyBox {
    CubeMapProgram cubeMapTexture();
    SkyboxType skyboxType();

    enum SkyboxType {
        D3D,
        D2D
    }
}
