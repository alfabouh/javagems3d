package workbench.graphics.environment.components;

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.environment.skybox.background.JGemsSkyBackground;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.Nullable;

public class WBenchSkyBox extends SkyBox {
    public WBenchSkyBox(IWorld world, float backGroundViewScaling, @Nullable ICubeMapProgram sky2DTexture) {
        super(new WBenchSkyBackground(world, backGroundViewScaling), sky2DTexture);
    }
}
