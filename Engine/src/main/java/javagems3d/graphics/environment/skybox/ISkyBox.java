package javagems3d.graphics.environment.skybox;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.Nullable;

public interface ISkyBox {
    void updateSkyBox(IWorld world, ICamera camera);
    void createSkyBox(IWorld world);
    void destroySkyBox(IWorld world);
    boolean isDrawSunOnSkyBox();

    ISkyBackground getBackground();
    ICubeMapProgram getTexture();

    void setSkyCoveredByFog(boolean skyCoveredByFog);
    void setSky2DTexture(@Nullable ICubeMapProgram sky2DTexture);

    boolean isSkyCoveredByFog();
}
