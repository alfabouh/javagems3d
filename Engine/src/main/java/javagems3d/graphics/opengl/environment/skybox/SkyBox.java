package javagems3d.graphics.opengl.environment.skybox;

import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.camera.FreeCamera;
import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.rendering.items.IModeledSceneObject;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneData;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.system.resources.assets.material.samples.CubeMapSample;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class SkyBox implements ISkyBox {
    private final Background background;
    private CubeMapSample sky2DTexture;
    private final Sun sun;
    private boolean isSkyCoveredByFog;

    public SkyBox(CubeMapSample sky2DTexture) {
        this.sky2DTexture = sky2DTexture;
        this.sun = new Sun(new Vector3f(1.0f), null, 1.0f);
        this.isSkyCoveredByFog = false;
        this.background = new Background();
    }

    public void setSkyCoveredByFog(boolean skyCoveredByFog) {
        this.isSkyCoveredByFog = skyCoveredByFog;
    }

    public void setSky2DTexture(CubeMapSample sky2DTexture) {
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

    public CubeMapSample getSky2DTexture() {
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
        private final FreeCamera scaledCameraBackground;
        private final Set<IModeledSceneObject> toRenderSet;
        private float viewScaling;

        public Background() {
            this.scaledCameraBackground = new FreeCamera(new Vector3f(), new Vector3f());
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
            this.getToRenderSet().forEach(e -> e.renderFabric().onPostRender(e));
            this.getToRenderSet().clear();
        }

        public void addObjectInBackGround(IModeledSceneObject modeledSceneObject) {
            this.getToRenderSet().add(modeledSceneObject);
            modeledSceneObject.renderFabric().onPreRender(modeledSceneObject);
        }

        public FreeCamera getScaledCameraBackground() {
            return this.scaledCameraBackground;
        }

        public float getViewScaling() {
            return this.viewScaling;
        }

        public Set<IModeledSceneObject> getToRenderSet() {
            return this.toRenderSet;
        }
    }
}
