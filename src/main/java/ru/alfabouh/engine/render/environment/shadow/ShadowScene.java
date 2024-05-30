package ru.alfabouh.engine.render.environment.shadow;

import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.materials.textures.IImageSample;
import ru.alfabouh.engine.system.resources.assets.models.Model;
import ru.alfabouh.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.system.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.math.MathHelper;
import ru.alfabouh.engine.render.environment.light.PointLight;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.programs.FBOTexture2DProgram;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.transformation.TransformationManager;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class ShadowScene {
    public static final int MAX_POINT_LIGHTS_SHADOWS = 3;
    public static final int CASCADE_SPLITS = 3;
    private final SceneWorld sceneWorld;
    private Vector2i shadowDimensions;
    private final FBOTexture2DProgram FBOTexture2DProgram;
    private List<CascadeShadow> cascadeShadows;
    private List<PointLightShadow> pointLightShadows;

    public ShadowScene(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.FBOTexture2DProgram = new FBOTexture2DProgram(true);
        this.initCascades();
        this.initPointLightShadows();

        this.createFBO();
    }

    public void createFBO() {
        this.shadowDimensions = new Vector2i((int) (2048 * ShadowScene.qualityMultiplier()));

        this.getPointLightShadows().forEach(e -> e.createFBO(new Vector2i(this.getShadowDim())));
        this.FBOTexture2DProgram.clearFBO();

        FBOTexture2DProgram.FBOTextureInfo[] FBOs = new FBOTexture2DProgram.FBOTextureInfo[]{new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RG32F, GL30.GL_RG),
                new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RG32F, GL30.GL_RG),
                new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RG32F, GL30.GL_RG)};
        this.FBOTexture2DProgram.createFrameBuffer2DTexture(this.getShadowDim(), FBOs, true, GL43.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
    }

    private Vector2i getShadowDim() {
        return this.shadowDimensions;
    }

    private static float qualityMultiplier() {
        int i = (int) MathHelper.clamp(JGems.get().getGameSettings().shadowQuality.getValue(), 0.0f, 2.0f);
        return i == 2 ? 1.0f : i == 1 ? 0.5f : 0.25f;
    }

    private void initPointLightShadows() {
        this.pointLightShadows = new ArrayList<>(ShadowScene.MAX_POINT_LIGHTS_SHADOWS);
        for (int i = 0; i < ShadowScene.MAX_POINT_LIGHTS_SHADOWS; i++) {
            this.pointLightShadows.add(new PointLightShadow(i, this.getSceneWorld()));
        }
    }

    private void initCascades() {
        this.cascadeShadows = new ArrayList<>();
        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            this.cascadeShadows.add(new CascadeShadow());
        }
    }

    private void updateCascadeShadows(List<CascadeShadow> cascadeShadows) {
        final int dimensions = this.getShadowDim().x;

        Matrix4d view = TransformationManager.instance.getMainCameraViewMatrix();
        Matrix4d projection = TransformationManager.instance.getProjectionMatrix();
        Vector4d sunPos = new Vector4d(this.getSceneWorld().getEnvironment().getSky().getSunAngle(), 0.0d);

        float[] cascadeSplitLambda = new float[]{0.5f, 0.5f, 0.5f};
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
            shadowOrigin.mul(dimensions).div(2.0f);

            Vector4d roundedOrigin = new Vector4d();
            shadowOrigin.round(roundedOrigin);
            Vector4d roundOffset = new Vector4d(roundedOrigin).sub(shadowOrigin);
            roundOffset.mul(2.0d).div(dimensions);
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

    public void renderAllModelsInShadowMap(List<Model<Format3D>> renderModels) {
        this.renderSceneInShadowMap(renderModels);
    }

    public void renderSceneInShadowMap(List<Model<Format3D>> modelList) {
        this.updateCascadeShadows(this.getCascadeShadows());
        this.sunScene(modelList);
        this.pointLightsScene(modelList);
    }

    private void sunScene(List<Model<Format3D>> modelList) {
        this.getSunShadowShader().bind();
        this.getFrameBufferObjectProgram().bindFBO();
        GL30.glViewport(0, 0, this.getShadowDim().x, this.getShadowDim().y);

        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            CascadeShadow cascadeShadow = this.getCascadeShadows().get(i);
            this.getFrameBufferObjectProgram().connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, i);
            GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
            this.getSunShadowShader().performUniform("projection_view_matrix", new Matrix4d(cascadeShadow.getLightProjectionViewMatrix()));
            GL30.glCullFace(GL30.GL_BACK);
            for (Model<Format3D> model : modelList) {
                if (model == null || model.getMeshDataGroup() == null) {
                    continue;
                }
                this.getSunShadowShader().performUniform("texture_sampler", 0);
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                this.getSunShadowShader().getUtils().performModelMatrix3d(model, false);
                this.renderModelForShadow(model);
            }
        }
        this.getFrameBufferObjectProgram().unBindFBO();
        this.getSunShadowShader().unBind();

        /*
        ShaderManager blurShader = ResourceManager.shaderAssets.blur13;
        Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(ShadowScene.SHADOW_SUN_MAP_SIZE), 0);
        blurShader.bind();
        blurShader.performUniform("resolution", new Vector2f(JGems.getGame().getScreen().getDimensions()));
        this.getFrameBufferObjectProgram().bindFBO();
        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            this.getFrameBufferObjectProgram().connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, i);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.getFrameBufferObjectProgram().bindTexture(0);
            blurShader.performUniform("texture_sampler", 0);
            blurShader.performUniform("direction", new Vector2f(1.0f, 1.0f));
            blurShader.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
        }
        this.getFrameBufferObjectProgram().unBindFBO();
        blurShader.unBind();
        model.clean();
         */
    }

    private void pointLightsScene(List<Model<Format3D>> modelList) {
        this.getPointLightShadowShader().bind();
        GL30.glViewport(0, 0, this.getShadowDim().x, this.getShadowDim().y);

        for (int i = 0; i < ShadowScene.MAX_POINT_LIGHTS_SHADOWS; i++) {
            PointLightShadow pointLightShadow = this.getPointLightShadows().get(i);
            if (pointLightShadow.isAttachedToLight() && pointLightShadow.getPointLight().isEnabled()) {
                pointLightShadow.getPointLightCubeMap().bindFBO();
                pointLightShadow.configureMatrices();
                for (int j = 0; j < 6; j++) {
                    pointLightShadow.getPointLightCubeMap().connectCubeMapToBuffer(GL30.GL_COLOR_ATTACHMENT0, j);
                    GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
                    this.getPointLightShadowShader().performUniform("view_matrix", pointLightShadow.getShadowDirections().get(j));
                    this.getPointLightShadowShader().performUniform("far_plane", pointLightShadow.farPlane());
                    this.getPointLightShadowShader().performUniform("lightPos", pointLightShadow.getPointLight().getLightPos());
                    GL30.glCullFace(GL30.GL_BACK);
                    for (Model<Format3D> model : modelList) {
                        if (model == null || model.getMeshDataGroup() == null) {
                            continue;
                        }
                        this.getPointLightShadowShader().getUtils().performModelMatrix3d(model, false);
                        Scene.renderModel(model, GL30.GL_TRIANGLES);
                    }
                }
                pointLightShadow.getPointLightCubeMap().unBindFBO();
            }
        }
        this.getPointLightShadowShader().unBind();
    }

    private void renderModelForShadow(Model<?> model) {
        for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
            if (modelNode.getMaterial().getDiffuse() instanceof IImageSample) {
                ((IImageSample) modelNode.getMaterial().getDiffuse()).bindTexture();
            }
            GL30.glBindVertexArray(modelNode.getMesh().getVao());
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glEnableVertexAttribArray(a);
            }
            GL30.glDrawElements(GL11.GL_TRIANGLES, modelNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glDisableVertexAttribArray(a);
            }
            GL30.glBindVertexArray(0);
        }
    }

    public void bindPointLightToShadowScene(int attachCode, PointLight pointLight) {
        if (attachCode >= ShadowScene.MAX_POINT_LIGHTS_SHADOWS) {
            JGems.get().getLogManager().warn("Couldn't attach point light with code: " + attachCode + ", because reached limit: " + ShadowScene.MAX_POINT_LIGHTS_SHADOWS);
            return;
        }
        PointLightShadow pointLightShadow = this.getPointLightShadows().get(attachCode);
        pointLightShadow.setPointLight(pointLight);
    }

    public void unBindPointLightFromShadowScene(PointLight pointLight) {
        if (pointLight.getAttachedShadowSceneId() < 0) {
            JGems.get().getLogManager().warn("Point Light " + pointLight.getAttachedShadowSceneId() + " is not attached to shadow scene!");
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

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public ShaderManager getSunShadowShader() {
        return ResourceManager.shaderAssets.depth_sun;
    }

    public ShaderManager getPointLightShadowShader() {
        return ResourceManager.shaderAssets.depth_plight;
    }
}
