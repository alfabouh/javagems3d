package javagems3d.graphics.environment.skybox;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.physics.world.IWorld;

public interface ISkyBox {
    void updateSkyBox(IWorld world, ICamera camera);
    void destroySkyBox(IWorld world);

    ICubeMapProgram getTexture();
    SunLight getSun();
}
