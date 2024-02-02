package ru.BouH.engine.render.environment.sky.skybox;

import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;

public class SkyBox2D implements ISkyBox {
    private final CubeMapProgram cubeMapTexture;

    public SkyBox2D(CubeMapProgram cubeMapTexture) {
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
