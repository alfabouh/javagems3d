package ru.BouH.engine.render.environment.shadow;

import org.joml.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.transformation.TransformationManager;
import ru.BouH.engine.render.environment.light.PointLight;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.objects.IModeledSceneObject;
import ru.BouH.engine.render.scene.programs.FBOTexture2DProgram;
import ru.BouH.engine.render.screen.Screen;

import java.lang.Math;
import java.util.*;
import java.util.stream.Collectors;

public class ShadowScene {
    public static final int MAX_POINT_LIGHTS_SHADOWS = 3;
    public static final int SHADOW_SUN_MAP_SIZE = 4096;
    public static final int SHADOW_PLIGHT_MAP_SIZE = 1024;
    public static final int CASCADE_SPLITS = 3;
    private final Scene scene;
    private final FBOTexture2DProgram FBOTexture2DProgram;
    private List<CascadeShadow> cascadeShadows;
    private List<PointLightShadow> pointLightShadows;

    public ShadowScene(Scene scene) {
        this.scene = scene;
        this.FBOTexture2DProgram = new FBOTexture2DProgram(true, false);
        this.FBOTexture2DProgram.createFrameBuffer2DTexture(new Vector2i(ShadowScene.SHADOW_SUN_MAP_SIZE), new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT0}, true, false, GL43.GL_RG32F, GL30.GL_RGB, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
        this.initCascades();
        this.initPointLightShadows();
    }

    private void initPointLightShadows() {
        this.pointLightShadows = new ArrayList<>(ShadowScene.MAX_POINT_LIGHTS_SHADOWS);
        for (int i = 0; i < ShadowScene.MAX_POINT_LIGHTS_SHADOWS; i++) {
            this.pointLightShadows.add(new PointLightShadow(i, this.getScene()));
        }
    }

    private void initCascades() {
        this.cascadeShadows = new ArrayList<>();
        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            this.cascadeShadows.add(new CascadeShadow());
        }
    }

    private void updateCascadeShadows(List<CascadeShadow> cascadeShadows) {
        Matrix4d view = TransformationManager.instance.getMainCameraViewMatrix();
        Matrix4d projection = TransformationManager.instance.getProjectionMatrix();
        Vector4d sunPos = new Vector4d(this.getScene().getSceneWorld().getEnvironment().getSky().getSunAngle(), 0.0d);

        float[] cascadeSplitLambda = new float[] {0.85f, 0.7f, 0.95f};
        float[] cascadeSplits = new float[ShadowScene.CASCADE_SPLITS];

        float nearClip = this.nearCascadeClip();
        float farClip = this.farCascadeClip();
        float clipRange = farClip - nearClip;

        float minZ = nearClip;
        float maxZ = nearClip + clipRange;

        float range = maxZ - minZ;
        float ratio = maxZ / minZ;

        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            float p = (i + 1) / (float) ShadowScene.CASCADE_SPLITS;
            float log = (float) (minZ * Math.pow(ratio, p));
            float uniform = minZ + range * p;
            float d = cascadeSplitLambda[i] * (log - uniform) + uniform;
            cascadeSplits[i] = (d - nearClip) / clipRange;
        }

        float lastSplitDist = 0.0f;
        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            float splitDist = cascadeSplits[i];

            Vector3d[] frustumCorners = new Vector3d[]{
                    new Vector3d(-1.0d, 1.0d, -1.0d),
                    new Vector3d(1.0d, 1.0d, -1.0d),
                    new Vector3d(1.0d, -1.0d, -1.0d),
                    new Vector3d(-1.0d, -1.0d, -1.0d),
                    new Vector3d(-1.0d, 1.0d, 1.0d),
                    new Vector3d(1.0d, 1.0d, 1.0d),
                    new Vector3d(1.0d, -1.0d, 1.0d),
                    new Vector3d(-1.0d, -1.0d, 1.0d),
            };

            Matrix4d invCam = (new Matrix4d(projection).mul(view)).invert();
            for (int j = 0; j < 8; j++) {
                Vector4d invCorner = new Vector4d(frustumCorners[j], 1.0d).mul(invCam);
                frustumCorners[j] = new Vector3d(invCorner.x, invCorner.y, invCorner.z).div(new Vector3d(invCorner.w));
            }

            for (int j = 0; j < 4; j++) {
                Vector3d dist = new Vector3d(frustumCorners[j + 4]).sub(frustumCorners[j]);
                frustumCorners[j + 4] = new Vector3d(frustumCorners[j]).add(new Vector3d(dist).mul(splitDist));
                frustumCorners[j] = new Vector3d(frustumCorners[j]).add(new Vector3d(dist).mul(lastSplitDist));
            }

            Vector3d frustumCenter = new Vector3d(0.0d);
            for (int j = 0; j < 8; j++) {
                frustumCenter.add(frustumCorners[j]);
            }
            frustumCenter.div(8.0d);
            double radius = 0.0d;
            for (int j = 0; j < 8; j++) {
                double distance = (new Vector3d(frustumCorners[j]).sub(frustumCenter)).length();
                radius = Math.max(radius, distance);
            }
            radius = Math.ceil(radius * 16.0d) / 16.0d;

            Vector3d maxExtents = new Vector3d(radius);
            Vector3d minExtents = new Vector3d(maxExtents).mul(-1.0d);

            Vector3d lightDir = (new Vector3d(sunPos.x, sunPos.y, sunPos.z).mul(-1.0d)).normalize();
            Vector3d eye = new Vector3d(frustumCenter).sub(new Vector3d(lightDir).mul(-minExtents.z));
            Vector3d up = new Vector3d(0.0d, 1.0d, 0.0d);
            Matrix4d lightViewMatrix = TransformationManager.instance.getLookAtMatrix(eye, up, frustumCenter);
            Matrix4d lightOrthoMatrix = TransformationManager.instance.getOrthographicMatrix(minExtents.x, maxExtents.x, minExtents.y, maxExtents.y, 0.0f, maxExtents.z - minExtents.z, true);

            CascadeShadow cascadeShadow = cascadeShadows.get(i);
            cascadeShadow.setSplitDistance((nearClip + splitDist * clipRange) * -1.0f);

            Matrix4d shadowMatrix = new Matrix4d(lightOrthoMatrix.mul(lightViewMatrix));
            Vector4d shadowOrigin = new Vector4d(0.0d, 0.0d, 0.0d, 1.0d);
            shadowOrigin.mul(shadowMatrix, shadowOrigin);
            shadowOrigin.mul(ShadowScene.SHADOW_SUN_MAP_SIZE).div(2.0f);

            Vector4d roundedOrigin = new Vector4d();
            shadowOrigin.round(roundedOrigin);
            Vector4d roundOffset = new Vector4d(roundedOrigin).sub(shadowOrigin);
            roundOffset.mul(2.0d).div(ShadowScene.SHADOW_SUN_MAP_SIZE);
            roundOffset.z = 0.0d;
            roundOffset.w = 0.0d;

            Matrix4d shadowProj = new Matrix4d(lightOrthoMatrix);
            shadowProj.m30(shadowProj.m30() + roundOffset.x);
            shadowProj.m31(shadowProj.m31() + roundOffset.y);
            shadowProj.m32(shadowProj.m32() + roundOffset.z);
            shadowProj.m33(shadowProj.m33() + roundOffset.w);

            cascadeShadow.setLightProjectionViewMatrix(shadowProj);

            lastSplitDist = cascadeSplits[i];
        }
    }

    public float nearCascadeClip() {
        return 1.0f;
    }

    public float farCascadeClip() {
        return TransformationManager.Z_FAR;
    }

    public void renderAllModelsInShadowMap(List<IModeledSceneObject> renderModels) {
        List<Model<Format3D>> l1 = renderModels.stream().filter(e -> e.hasRender() && e.getModelRenderParams().isShadowCaster()).map(IModeledSceneObject::getModel3D).collect(Collectors.toList());
        this.renderSceneInShadowMap(l1);
    }

    public void renderSceneInShadowMap(List<Model<Format3D>> modelList) {
        this.updateCascadeShadows(this.getCascadeShadows());
        this.sunScene(modelList);
        this.pointLightsScene(modelList);
    }

    private void sunScene(List<Model<Format3D>> modelList) {
        this.getSunShadowShader().bind();
        this.getFrameBufferObjectProgram().bindFBO();
        Screen.setViewport(new Vector2i(ShadowScene.SHADOW_SUN_MAP_SIZE));

        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            CascadeShadow cascadeShadow = this.getCascadeShadows().get(i);
            this.getFrameBufferObjectProgram().connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, i);
            GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
            this.getSunShadowShader().performUniform("projection_view_matrix", new Matrix4d(cascadeShadow.getLightProjectionViewMatrix()));
            GL30.glCullFace(GL30.GL_BACK);
            for (Model<Format3D> model : modelList) {
                this.getSunShadowShader().getUtils().performModelMatrix3d(model, false);
                Scene.renderModel(model, GL30.GL_TRIANGLES);
            }
        }

        this.getFrameBufferObjectProgram().unBindFBO();
        this.getSunShadowShader().unBind();
    }

    private void pointLightsScene(List<Model<Format3D>> modelList) {
        this.getPointLightShadowShader().bind();
        Screen.setViewport(new Vector2i(ShadowScene.SHADOW_PLIGHT_MAP_SIZE));
        for (int i = 0; i < ShadowScene.MAX_POINT_LIGHTS_SHADOWS; i++) {
            PointLightShadow pointLightShadow = this.getPointLightShadows().get(i);
            if (pointLightShadow.isAttachedToLight()) {
                pointLightShadow.getPointLightCubeMap().bindFBO();
                pointLightShadow.configureMatrices();
                GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
                for (int j = 0; j < 6; j++) {
                    this.getPointLightShadowShader().performUniform("shadow_matrices", j, pointLightShadow.getShadowDirections().get(j));
                }
                this.getPointLightShadowShader().performUniform("far_plane", pointLightShadow.farPlane());
                this.getPointLightShadowShader().performUniform("lightPos", pointLightShadow.getPointLight().getLightPos());
                for (Model<Format3D> model : modelList) {
                    this.getPointLightShadowShader().getUtils().performModelMatrix3d(model, false);
                    Scene.renderModel(model, GL30.GL_TRIANGLES);
                }
                pointLightShadow.getPointLightCubeMap().unBindFBO();
            }
        }
        this.getPointLightShadowShader().unBind();
    }

    public void bindPointLightToShadowScene(int attachCode, PointLight pointLight) {
        if (attachCode >= ShadowScene.MAX_POINT_LIGHTS_SHADOWS) {
            Game.getGame().getLogManager().warn("Couldn't attach point light with code: " + attachCode + ", because reached limit: " + ShadowScene.MAX_POINT_LIGHTS_SHADOWS);
            return;
        }
        PointLightShadow pointLightShadow = this.getPointLightShadows().get(attachCode);
        pointLightShadow.setPointLight(pointLight);
    }

    public void unBindPointLightFromShadowScene(PointLight pointLight) {
        if (pointLight.getAttachedShadowSceneId() < 0) {
            Game.getGame().getLogManager().warn("Point Light " + pointLight.getAttachedShadowSceneId() + " is not attached to shadow scene!");
            return;
        }
        this.getPointLightShadows().get(pointLight.getAttachedShadowSceneId()).setPointLight(null);
    }

    public List<PointLightShadow> getPointLightShadows() {
        return this.pointLightShadows;
    }

    public List<CascadeShadow> getCascadeShadows() {
        return this.cascadeShadows;
    }

    public FBOTexture2DProgram getFrameBufferObjectProgram() {
        return this.FBOTexture2DProgram;
    }

    public Scene getScene() {
        return this.scene;
    }

    public ShaderManager getSunShadowShader() {
        return ResourceManager.shaderAssets.depth_sun;
    }

    public ShaderManager getPointLightShadowShader() {
        return ResourceManager.shaderAssets.depth_plight;
    }
}
