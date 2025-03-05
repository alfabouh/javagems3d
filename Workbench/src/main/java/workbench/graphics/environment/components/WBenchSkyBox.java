package workbench.graphics.environment.components;

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.Nullable;

public class WBenchSkyBox extends SkyBox {
    public WBenchSkyBox(float backGroundViewScaling, IWorld world, @Nullable ICubeMapProgram sky2DTexture) {
        super(backGroundViewScaling, world, sky2DTexture);
    }
}
