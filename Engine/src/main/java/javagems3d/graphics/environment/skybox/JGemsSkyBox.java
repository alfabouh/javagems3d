package javagems3d.graphics.environment.skybox;

import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.objects.entities.background.SceneBackgroundProp;
import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.physics.world.IWorld;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class JGemsSkyBox extends SkyBox {
    public JGemsSkyBox(float backGroundViewScaling, IWorld world, ITextureProgram sky2DTexture) {
        super(backGroundViewScaling, world, sky2DTexture);
    }
}
