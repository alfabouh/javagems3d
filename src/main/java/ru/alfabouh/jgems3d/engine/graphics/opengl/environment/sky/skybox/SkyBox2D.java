package ru.alfabouh.jgems3d.engine.graphics.opengl.environment.sky.skybox;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.programs.textures.CubeMapProgram;

public class SkyBox2D implements ISkyBox {
    private CubeMapProgram cubeMapTexture;

    public SkyBox2D(CubeMapProgram cubeMapTexture) {
        this.cubeMapTexture = cubeMapTexture;
    }

    public void setCubeMapTexture(CubeMapProgram cubeMapTexture) {
        this.cubeMapTexture = cubeMapTexture;
    }

    @Override
    public CubeMapProgram cubeMapTexture() {
        return this.cubeMapTexture;
    }

    @Override
    public SkyboxType skyboxType() {
        return SkyboxType.D2D;
    }
}
