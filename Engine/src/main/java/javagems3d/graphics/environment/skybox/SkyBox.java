package javagems3d.graphics.environment.skybox;

import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.objects.entities.background.SceneBackgroundProp;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public abstract class SkyBox implements ISkyBox {
    private ICubeMapProgram sky2DTexture;
    private final Background background;
    private final SunLight sunLight;
    private boolean isSkyCoveredByFog;

    public SkyBox(float backGroundViewScaling, IWorld world, @Nullable ICubeMapProgram sky2DTexture) {
        this.sky2DTexture = sky2DTexture;
        this.sunLight = new SunLight(new Vector3f(1.0f), new Vector3f(1.0f), 1.0f);
        this.isSkyCoveredByFog = true;
        this.background = new Background(world, backGroundViewScaling);
    }

    @Override
    public void setSkyCoveredByFog(boolean skyCoveredByFog) {
        this.isSkyCoveredByFog = skyCoveredByFog;
    }

    @Override
    public void setSky2DTexture(@Nullable ICubeMapProgram sky2DTexture) {
        this.sky2DTexture = sky2DTexture;
    }

    public boolean isSkyCoveredByFog() {
        return this.isSkyCoveredByFog;
    }

    public Background getBackground() {
        return this.background;
    }

    public SunLight getSun() {
        return this.sunLight;
    }

    public ICubeMapProgram getTexture() {
        return this.sky2DTexture;
    }

    @Override
    public void updateSkyBox(IWorld world, ICamera camera) {
        this.getBackground().updateMeta(camera);
    }

    @Override
    public void destroySkyBox(IWorld world) {
        this.getBackground().clearBackGround();
    }

    public static class Background {
        private final FixedCamera scaledCameraBackground;
        private final Set<SceneBackgroundProp> toRenderSet;
        private final IWorld world;
        private float viewScaling;

        public Background(IWorld world, float viewScaling) {
            this.scaledCameraBackground = new FixedCamera(new Vector3f(), new Vector3f());
            this.toRenderSet = new HashSet<>();
            this.viewScaling = viewScaling;
            this.world = world;
        }

        public void updateMeta(ICamera camera) {
            this.getScaledCameraBackground().setCameraPosition(camera.getCamPosition().mul(1.0f / this.getViewScaling()));
            this.getScaledCameraBackground().setCameraRotation(camera.getCamRotation());
        }

        public void setViewScaling(float viewScaling) {
            this.viewScaling = viewScaling;
        }

        public void clearBackGround() {
            this.getToRenderSet().forEach(e -> e.onDestroy(this.getWorld()));
            this.getToRenderSet().clear();
        }

        public void addObjectInBackGround(SceneBackgroundProp sceneBackgroundProp) {
            sceneBackgroundProp.onSpawn(this.getWorld());
            this.getToRenderSet().add(sceneBackgroundProp);
        }

        public IWorld getWorld() {
            return this.world;
        }

        public FixedCamera getScaledCameraBackground() {
            return this.scaledCameraBackground;
        }

        public float getViewScaling() {
            return this.viewScaling;
        }

        public Set<SceneBackgroundProp> getToRenderSet() {
            return this.toRenderSet;
        }
    }
}
