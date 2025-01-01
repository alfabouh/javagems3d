package javagems3d.graphics.environment.skybox;

import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.texturing.CubeMapTextureOLD;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class SkyBox implements ISkyBox {
    private final Background background;
    private CubeMapTextureOLD sky2DTexture;
    private final Sun sun;
    private boolean isSkyCoveredByFog;

    public SkyBox(CubeMapTextureOLD sky2DTexture) {
        this.sky2DTexture = sky2DTexture;
        this.sun = new Sun(new Vector3f(1.0f), null, 1.0f);
        this.isSkyCoveredByFog = false;
        this.background = new Background();
    }

    public void setSkyCoveredByFog(boolean skyCoveredByFog) {
        this.isSkyCoveredByFog = skyCoveredByFog;
    }

    public void setSky2DTexture(CubeMapTextureOLD sky2DTexture) {
        this.sky2DTexture = sky2DTexture;
    }

    public boolean isSkyCoveredByFog() {
        return this.isSkyCoveredByFog;
    }

    public Background getBackground() {
        return this.background;
    }

    public Sun getSun() {
        return this.sun;
    }

    public CubeMapTextureOLD getSky2DTexture() {
        return this.sky2DTexture;
    }

    @Override
    public void updateSkyBox(SceneWorld sceneWorld, ICamera camera) {
        this.getBackground().updateMeta(sceneWorld, camera);
    }

    @Override
    public void destroySkyBox(SceneWorld sceneWorld) {
        this.getBackground().clearBackGround();
    }

    public static class Sun {
        private Vector3f sunPos;
        private Vector3f sunColor;
        private float sunBrightness;

        public Sun(@NotNull Vector3f sunPos, Vector3f sunColor, float sunBrightness) {
            this.sunPos = sunPos;
            this.sunColor = sunColor == null ? new Vector3f(1.0f) : sunColor;
            this.sunBrightness = sunBrightness;
        }

        public void setSunColor(Vector3f sunColor) {
            this.sunColor = sunColor;
        }

        public void setSunBrightness(float sunBrightness) {
            this.sunBrightness = sunBrightness;
        }

        public void setSunPosition(@NotNull Vector3f sunPos) {
            this.sunPos = sunPos;
        }

        public Vector3f getSunPosition() {
            return this.sunPos;
        }

        public Vector3f getSunColor() {
            return this.sunColor;
        }

        public float getSunBrightness() {
            return this.sunBrightness;
        }
    }

    public static class Background {
        private final FixedCamera scaledCameraBackground;
        private final Set<SceneObject> toRenderSet;
        private float viewScaling;

        public Background() {
            this.scaledCameraBackground = new FixedCamera(new Vector3f(), new Vector3f());
            this.toRenderSet = new HashSet<>();
            this.viewScaling = 4.0f;
        }

        public void updateMeta(SceneWorld sceneWorld, ICamera camera) {
            this.getScaledCameraBackground().setCameraPosition(camera.getCamPosition().mul(1.0f / this.getViewScaling()));
            this.getScaledCameraBackground().setCameraRotation(camera.getCamRotation());
        }

        public void setViewScaling(float viewScaling) {
            this.viewScaling = viewScaling;
        }

        public void clearBackGround() {
            //this.getToRenderSet().forEach(e -> e.getRenderFabric().onPostRender(e));
            //this.getToRenderSet().clear();
        }

        public void addObjectInBackGround(SceneObject modeledSceneObject) {
           //this.getToRenderSet().add(modeledSceneObject);
           //modeledSceneObject.getRenderFabric().onPreRender(modeledSceneObject);
        }

        public FixedCamera getScaledCameraBackground() {
            return this.scaledCameraBackground;
        }

        public float getViewScaling() {
            return this.viewScaling;
        }

        public Set<SceneObject> getToRenderSet() {
            return this.toRenderSet;
        }
    }
}
