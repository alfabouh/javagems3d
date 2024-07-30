package ru.jgems3d.engine.graphics.opengl.environment.shadow;

import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.environment.light.PointLight;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsScene;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.FBOTexture2DProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.graphics.transformation.Transformation;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class ShadowScene {
    public static final int MAX_POINT_LIGHTS_SHADOWS = 3;
    public static final int CASCADE_SPLITS = 3;
    private final SceneWorld sceneWorld;
    private final FBOTexture2DProgram shadowFBO;
    private final FBOTexture2DProgram shadowFBO2;
    private final FBOTexture2DProgram shadowPostFBO;
    private Vector2i shadowDimensions;
    private List<CascadeShadow> cascadeShadows;
    private List<PointLightShadow> pointLightShadows;

    public ShadowScene(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.shadowFBO = new FBOTexture2DProgram(true);
        this.shadowFBO2 = new FBOTexture2DProgram(true);
        this.shadowPostFBO = new FBOTexture2DProgram(true);
        this.initCascades();
        this.initPointLightShadows();

        this.createResources();
    }

    private static float qualityMultiplier() {
        int i = (int) JGemsHelper.clamp(JGems3D.get().getGameSettings().shadowQuality.getValue(), 0.0f, 2.0f);
        return i == 2 ? 1.0f : i == 1 ? 0.5f : 0.25f;
    }

    public void createResources() {
        this.shadowDimensions = new Vector2i((int) (2048 * ShadowScene.qualityMultiplier()));

        this.getPointLightShadows().forEach(e -> e.createFBO(new Vector2i(this.getShadowDim())));
        this.shadowFBO.clearFBO();
        this.shadowFBO2.clearFBO();
        this.shadowPostFBO.clearFBO();

        FBOTexture2DProgram.FBOTextureInfo[] FBOs = new FBOTexture2DProgram.FBOTextureInfo[]{new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RG32F, GL30.GL_RG),
                new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RG32F, GL30.GL_RG),
                new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RG32F, GL30.GL_RG)};
        this.shadowFBO.createFrameBuffer2DTexture(this.getShadowDim(), FBOs, true, GL43.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
        this.shadowFBO2.createFrameBuffer2DTexture(this.getShadowDim(), FBOs, true, GL43.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
        this.shadowPostFBO.createFrameBuffer2DTexture(this.getShadowDim(), FBOs, true, GL43.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
    }

    private Vector2i getShadowDim() {
        return this.shadowDimensions;
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
        JGemsScene scene = JGems3D.get().getScreen().getScene();

        Matrix4f view = scene.getTransformationUtils().getMainCameraViewMatrix();
        Matrix4f projection = scene.getTransformationUtils().getPerspectiveMatrix();

        Vector4f sunPos = new Vector4f(this.getSceneWorld().getEnvironment().getSky().getSunPos(), 0.0f);

        float[] cascadeSplitLambda = new float[]{0.6f, 0.6f, 0.6f};
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

            Vector3f[] frustumCorners = new Vector3f[]{
                    new Vector3f(-1.0f, 1.0f, -1.0f),
                    new Vector3f(1.0f, 1.0f, -1.0f),
                    new Vector3f(1.0f, -1.0f, -1.0f),
                    new Vector3f(-1.0f, -1.0f, -1.0f),
                    new Vector3f(-1.0f, 1.0f, 1.0f),
                    new Vector3f(1.0f, 1.0f, 1.0f),
                    new Vector3f(1.0f, -1.0f, 1.0f),
                    new Vector3f(-1.0f, -1.0f, 1.0f),
            };

            Matrix4f invCam = (new Matrix4f(projection).mul(view)).invert();
            for (int j = 0; j < 8; j++) {
                Vector4f invCorner = new Vector4f(frustumCorners[j], 1.0f).mul(invCam);
                frustumCorners[j] = new Vector3f(invCorner.x, invCorner.y, invCorner.z).div(new Vector3f(invCorner.w));
            }

            for (int j = 0; j < 4; j++) {
                Vector3f dist = new Vector3f(frustumCorners[j + 4]).sub(frustumCorners[j]);
                frustumCorners[j + 4] = new Vector3f(frustumCorners[j]).add(new Vector3f(dist).mul(splitDist));
                frustumCorners[j] = new Vector3f(frustumCorners[j]).add(new Vector3f(dist).mul(lastSplitDist));
            }

            Vector3f frustumCenter = new Vector3f(0.0f);
            for (int j = 0; j < 8; j++) {
                frustumCenter.add(frustumCorners[j]);
            }
            frustumCenter.div(8.0f);
            float radius = 0.0f;
            for (int j = 0; j < 8; j++) {
                double distance = (new Vector3f(frustumCorners[j]).sub(frustumCenter)).length();
                radius = (float) Math.max(radius, distance);
            }
            radius = (float) (Math.ceil(radius * 16.0d) / 16.0d);

            Vector3f maxExtents = new Vector3f(radius);
            Vector3f minExtents = new Vector3f(maxExtents).mul(-1.0f);

            Vector3f lightDir = (new Vector3f(sunPos.x, sunPos.y, sunPos.z).mul(-1.0f)).normalize();
            Vector3f eye = new Vector3f(frustumCenter).sub(new Vector3f(lightDir).mul(-minExtents.z));
            Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
            Matrix4f lightViewMatrix = Transformation.getLookAtMatrix(eye, up, frustumCenter);
            Matrix4f lightOrthoMatrix = Transformation.getOrthographic3DMatrix(minExtents.x, maxExtents.x, minExtents.y, maxExtents.y, 0.0f, maxExtents.z - minExtents.z, true);

            CascadeShadow cascadeShadow = cascadeShadows.get(i);
            cascadeShadow.setSplitDistance((nearClip + splitDist * clipRange) * -1.0f);

            Matrix4f shadowMatrix = new Matrix4f(lightOrthoMatrix.mul(lightViewMatrix));
            Vector4f shadowOrigin = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
            shadowOrigin.mul(shadowMatrix, shadowOrigin);
            shadowOrigin.mul(dimensions).div(2.0f);

            Vector4f roundedOrigin = new Vector4f();
            shadowOrigin.round(roundedOrigin);
            Vector4f roundOffset = new Vector4f(roundedOrigin).sub(shadowOrigin);
            roundOffset.mul(2.0f).div(dimensions);
            roundOffset.z = 0.0f;
            roundOffset.w = 0.0f;

            Matrix4f shadowProj = new Matrix4f(lightOrthoMatrix);
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
        return JGemsSceneUtils.Z_FAR;
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
        this.getShadowFBO().bindFBO();
        GL30.glViewport(0, 0, this.getShadowDim().x, this.getShadowDim().y);

        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            CascadeShadow cascadeShadow = this.getCascadeShadows().get(i);
            this.getShadowFBO().connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, i);
            GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
            this.getSunShadowShader().performUniform("projection_view_matrix", new Matrix4f(cascadeShadow.getLightProjectionViewMatrix()));
            GL30.glCullFace(GL30.GL_BACK);
            for (Model<Format3D> model : modelList) {
                if (model == null || model.getMeshDataGroup() == null) {
                    continue;
                }
                this.getSunShadowShader().getUtils().performModel3DMatrix(model);
                this.renderModelForShadow(this.getSunShadowShader(), model);
            }
        }
        this.getShadowFBO().unBindFBO();
        this.getSunShadowShader().unBind();

        Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getShadowDim()), 0);
        JGemsShaderManager shaderManager = JGemsResourceManager.globalShaderAssets.depth_sun_fix;
        this.getShadowFBO2().bindFBO();
        GL30.glViewport(0, 0, this.getShadowDim().x, this.getShadowDim().y);
        shaderManager.bind();
        shaderManager.performUniform("projection_model_matrix", Transformation.getModelOrthographicMatrix(model.getFormat(), Transformation.getOrthographic2DMatrix(0, this.getShadowDim().x, this.getShadowDim().y, 0)));
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
            this.getShadowFBO2().connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, i);
            this.getShadowFBO().getTexturePrograms().get(i).bindTexture(GL30.GL_TEXTURE_2D);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            shaderManager.performUniform("texture_sampler", 0);
            JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        }
        shaderManager.unBind();
        this.getShadowFBO2().unBindFBO();

        shaderManager = JGemsResourceManager.globalShaderAssets.blur_box;
        this.getShadowPostFBO().bindFBO();
        GL30.glViewport(0, 0, this.getShadowDim().x, this.getShadowDim().y);
        shaderManager.bind();
        shaderManager.performUniform("projection_model_matrix", Transformation.getModelOrthographicMatrix(model.getFormat(), Transformation.getOrthographic2DMatrix(0, this.getShadowDim().x, this.getShadowDim().y, 0)));
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
        for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
            GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
            this.getShadowPostFBO().connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, i);
            this.getShadowFBO2().getTexturePrograms().get(i).bindTexture(GL30.GL_TEXTURE_2D);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            shaderManager.performUniform("blur", JGems3D.get().getGameSettings().shadowQuality.getValue() + 1.0f);
            shaderManager.performUniform("texture_sampler", 0);
            JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        }
        shaderManager.unBind();
        this.getShadowPostFBO().unBindFBO();
        model.clean();
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
                        this.getPointLightShadowShader().getUtils().performModel3DMatrix(model);
                        this.renderModelForShadow(this.getPointLightShadowShader(), model);
                    }
                }
                pointLightShadow.getPointLightCubeMap().unBindFBO();
            }
        }
        this.getPointLightShadowShader().unBind();
    }

    private void renderModelForShadow(JGemsShaderManager shaderManager, Model<?> model) {
        shaderManager.performUniform("alpha_discard", 0.8f);
        for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
            if (modelNode.getMaterial().getDiffuse() instanceof IImageSample) {
                shaderManager.performUniform("texture_sampler", 0);
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                ((IImageSample) modelNode.getMaterial().getDiffuse()).bindTexture();
                shaderManager.performUniform("use_texture", true);
            } else {
                shaderManager.performUniform("use_texture", false);
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
            JGemsHelper.getLogger().warn("Couldn't attach point light with code: " + attachCode + ", because reached limit: " + ShadowScene.MAX_POINT_LIGHTS_SHADOWS);
            return;
        }
        PointLightShadow pointLightShadow = this.getPointLightShadows().get(attachCode);
        pointLightShadow.setPointLight(pointLight);
    }

    public void unBindPointLightFromShadowScene(PointLight pointLight) {
        if (pointLight.getAttachedShadowSceneId() < 0) {
            JGemsHelper.getLogger().warn("Point Light " + pointLight.getAttachedShadowSceneId() + " is not attached to shadow scene!");
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

    public FBOTexture2DProgram getShadowFBO2() {
        return this.shadowFBO2;
    }

    public FBOTexture2DProgram getShadowFBO() {
        return this.shadowFBO;
    }

    public FBOTexture2DProgram getShadowPostFBO() {
        return this.shadowPostFBO;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public JGemsShaderManager getSunShadowShader() {
        return JGemsResourceManager.globalShaderAssets.depth_sun;
    }

    public JGemsShaderManager getPointLightShadowShader() {
        return JGemsResourceManager.globalShaderAssets.depth_plight;
    }
}