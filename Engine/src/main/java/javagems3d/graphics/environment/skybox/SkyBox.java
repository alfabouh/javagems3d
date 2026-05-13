package javagems3d.graphics.environment.skybox;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.skybox.background.JGemsSkyBackground;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class SkyBox implements ISkyBox {
    private ICubeMapProgram sky2DTexture;
    private ISkyBackground background;
    private boolean isSkyCoveredByFog;
    private boolean drawSunOnSkyBox;

    public SkyBox(@NotNull ISkyBackground skyBackground, @Nullable ICubeMapProgram sky2DTexture) {
        this.sky2DTexture = sky2DTexture;
        this.isSkyCoveredByFog = true;
        this.background = skyBackground;
        this.drawSunOnSkyBox = true;
    }

    @Override
    public void setSkyCoveredByFog(boolean skyCoveredByFog) {
        this.isSkyCoveredByFog = skyCoveredByFog;
    }

    @Override
    public void setSky2DTexture(@Nullable ICubeMapProgram sky2DTexture) {
        this.sky2DTexture = sky2DTexture;
    }

    public SkyBox setBackground(@NotNull ISkyBackground background) {
        this.background = background;
        return this;
    }

    public boolean isSkyCoveredByFog() {
        return this.isSkyCoveredByFog;
    }

    public ISkyBackground getBackground() {
        return this.background;
    }

    public ICubeMapProgram getTexture() {
        return this.sky2DTexture;
    }

    @Override
    public void updateSkyBox(IWorld world, ICamera camera) {
        this.getBackground().update(camera);
    }

    @Override
    public void createSkyBox(IWorld world) {
        this.getBackground().create(world);
    }

    @Override
    public void destroySkyBox(IWorld world) {
        this.getBackground().destroy(world);
    }

    public boolean isDrawSunOnSkyBox() {
        return this.drawSunOnSkyBox;
    }

    public SkyBox setDrawSunOnSkyBox(boolean drawSunOnSkyBox) {
        this.drawSunOnSkyBox = drawSunOnSkyBox;
        return this;
    }
}
