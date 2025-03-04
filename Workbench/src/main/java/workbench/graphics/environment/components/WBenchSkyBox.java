package workbench.graphics.environment.components;

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.Nullable;

public class WBenchSkyBox extends SkyBox {
    public WBenchSkyBox(float backGroundViewScaling, IWorld world, @Nullable ITextureProgram sky2DTexture) {
        super(backGroundViewScaling, world, sky2DTexture);
    }
}
