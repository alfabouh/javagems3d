package javagems3d.graphics.environment.skybox;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.physics.world.IWorld;

public class JGemsSkyBox extends SkyBox {
    public JGemsSkyBox(float backGroundViewScaling, IWorld world, ICubeMapProgram sky2DTexture) {
        super(backGroundViewScaling, world, sky2DTexture);
    }
}
