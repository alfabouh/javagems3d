package javagems3d.graphics.environment.shadows;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.opengl.GL46;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class SunLightShadow extends Shadow {
    private final FBOTexture2DProgram sunShadowFBO;
    private final FBOTexture2DProgram buffer;

    private List<Cascade> cascades;
    private final int totalCascades;
    private final Vector3f cascadeSplits;
    private boolean enabled;

    private JGemsShaderManager blurShader;

    public SunLightShadow(IEnvironment environment, Vector2i shadowMapResolution, int totalCascades, @Nullable JGemsShaderManager blurShader) {
        super(environment, shadowMapResolution);
        this.totalCascades = totalCascades;
        this.sunShadowFBO = new FBOTexture2DProgram(true, false);
        this.buffer = new FBOTexture2DProgram(true, false);
        this.cascadeSplits = new Vector3f();
        this.enabled = true;
        this.blurShader = blurShader;
        this.initCascades();
    }

    private void initCascades() {
        this.cascades = new ArrayList<>();
        for (int i = 0; i < this.getTotalCascades(); i++) {
            this.cascades.add(new Cascade());
        }
        this.setDefaultCascadeSplits();
    }

    /*
                Vector4f vLightCameraOrthographicMin = new Vector4f(minExtents.x, minExtents.y, 0.0f, 0.0f);
            Vector4f vLightCameraOrthographicMax = new Vector4f(maxExtents.x, maxExtents.y, 0.0f, 0.0f);
            float fCascadeBound = maxExtents.x - minExtents.x;
            float fWorldUnitsPerTexel = fCascadeBound / (float) this.getShadowMapResolution().x;
            Vector4f vWorldUnitsPerTexel = new Vector4f(fWorldUnitsPerTexel, fWorldUnitsPerTexel, 0.0f, 0.0f);
            Vector4f tempMin = new Vector4f(vLightCameraOrthographicMin).div(vWorldUnitsPerTexel);
            tempMin.x = (float) Math.floor(tempMin.x);
            tempMin.y = (float) Math.floor(tempMin.y);
            tempMin.z = (float) Math.floor(tempMin.z);
            tempMin.w = (float) Math.floor(tempMin.w);
            vLightCameraOrthographicMin.set(tempMin.mul(vWorldUnitsPerTexel));
            Vector4f tempMax = new Vector4f(vLightCameraOrthographicMax).div(vWorldUnitsPerTexel);
            tempMax.x = (float) Math.floor(tempMax.x);
            tempMax.y = (float) Math.floor(tempMax.y);
            tempMax.z = (float) Math.floor(tempMax.z);
            tempMax.w = (float) Math.floor(tempMax.w);
            vLightCameraOrthographicMax.set(tempMax.mul(vWorldUnitsPerTexel));
     */

    /*
                Vector4f roundedOrigin = new Vector4f();
            shadowOrigin.round(roundedOrigin);
            Vector4f roundOffset = new Vector4f(roundedOrigin).sub(shadowOrigin);
            roundOffset.mul(2.0f).div(this.getShadowMapResolution().x);

            Matrix4f shadowProj = new Matrix4f(lightOrthoMatrix);
            shadowProj.m30(shadowProj.m30() + roundOffset.x);
            shadowProj.m31(shadowProj.m31() + roundOffset.y);
            shadowProj.m32(shadowProj.m32() + roundOffset.z);
            shadowProj.m33(shadowProj.m33() + roundOffset.w);
     */

    public void refreshCascades() {
        Matrix4f view = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
        Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();

        Vector4f sunPos = new Vector4f(this.getEnvironment().getLightScene().getSunLight().getLightPosition(), 0.0f);

        float[] cascadeSplitLambda = new float[]{this.getCascadeSplits().x, this.getCascadeSplits().y, 0.0f};
        float[] cascadeSplits = new float[this.getTotalCascades()];

        float nearClip = JGemsConfig.SYSTEM.Z_NEAR;
        float farClip = JGemsConfig.SYSTEM.Z_FAR;
        float clipRange = farClip - nearClip;

        float minZ = nearClip;
        float maxZ = nearClip + clipRange;

        float range = maxZ - minZ;
        float ratio = maxZ / minZ;

        for (int i = 0; i < this.getTotalCascades(); i++) {
            float p = (i + 1) / (float) this.getTotalCascades();
            float log = (float) (minZ * Math.pow(ratio, p));
            float uniform = minZ + range * p;
            float d = cascadeSplitLambda[i] * (log - uniform) + uniform;
            cascadeSplits[i] = (d - nearClip) / clipRange;
        }

        float lastSplitDist = 0.0f;
        for (int i = 0; i < this.getTotalCascades(); i++) {
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
            Vector3f eye = new Vector3f(frustumCenter).sub(new Vector3f(lightDir).mul(radius));
            Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

            if (Math.abs(lightDir.y) > 0.99f) {
                up.set(0.0f, 0.0f, 1.0f);
            }

            Matrix4f lightViewMatrix = TransformUtils.getLookAtMatrix(eye, up, frustumCenter);
            Matrix4f lightOrthoMatrix = TransformUtils.getOrthographic3DMatrix(minExtents.x, maxExtents.x, minExtents.y, maxExtents.y, 0.0f, maxExtents.z - minExtents.z, true);

            Cascade cascade = this.getCascades().get(i);
            cascade.setSplitDistance((nearClip + splitDist * clipRange) * -1.0f);

            lightOrthoMatrix.mul(lightViewMatrix);
            Matrix4f shadowMatrix = new Matrix4f(lightOrthoMatrix);
            Vector4f shadowOrigin = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
            shadowOrigin.mul(shadowMatrix, shadowOrigin);
            shadowOrigin.mul(this.getShadowMapResolution().x).div(2.0f);

            Vector4f tempOrigin = new Vector4f(shadowOrigin);
            Vector4f halfTexel = new Vector4f(0.5f, 0.5f, 0.0f, 0.0f);
            tempOrigin.add(halfTexel);
            tempOrigin.x = (float) Math.floor(tempOrigin.x);
            tempOrigin.y = (float) Math.floor(tempOrigin.y);
            tempOrigin.z = (float) Math.floor(tempOrigin.z);
            tempOrigin.w = (float) Math.floor(tempOrigin.w);
            Vector4f roundedOrigin = new Vector4f(tempOrigin).sub(halfTexel);
            Vector4f roundOffset = new Vector4f(roundedOrigin).sub(shadowOrigin);
            roundOffset.mul(2.0f).div(this.getShadowMapResolution().x);
            Matrix4f shadowProj = new Matrix4f(lightOrthoMatrix);
            shadowProj.m30(shadowProj.m30() + roundOffset.x);
            shadowProj.m31(shadowProj.m31() + roundOffset.y);
            shadowProj.m32(shadowProj.m32() + roundOffset.z);
            shadowProj.m33(shadowProj.m33() + roundOffset.w);
            cascade.setLightProjectionViewMatrix(shadowProj);

            lastSplitDist = cascadeSplits[i];
        }
    }

    public void blur(Model2D screenModel, int maxCascadesToBlur) {
        final JGemsShaderManager blurShader = this.blurShader;
        if (blurShader == null) {
            return;
        }
        Vector2i resolution = this.getShadowMapResolution();
        final Matrix4f orthoMatrix = TransformUtils.getModelOrthographicMatrix(screenModel.getPose(), TransformUtils.getOrthographic2DMatrix(0, resolution.x, resolution.y, 0));
        OpenGLRenderer.setViewPort(resolution);
        blurShader.beginShading();
        blurShader.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), UniformFunctions.MAT4F(orthoMatrix));
        for (int i = 0; i < maxCascadesToBlur; i++) {

            this.buffer.bindFBO();
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
            blurShader.performUniform(new UniformString(DefaultUniformDefinitions.DIRECTION), UniformFunctions.VEC2F(new Vector2f(1.0f, 0.0f)));
            blurShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.getSunShadowFBO().getTextureByIndex(i));
            JGemsHelper.render().renderModel2D(screenModel, GL46.GL_TRIANGLES);
            this.buffer.unBindFBO();

            this.getSunShadowFBO().bindFBO();
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
            this.getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            blurShader.performUniform(new UniformString(DefaultUniformDefinitions.DIRECTION), UniformFunctions.VEC2F(new Vector2f(0.0f, 1.0f)));
            blurShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.buffer.getTextureByIndex(0));
            JGemsHelper.render().renderModel2D(screenModel, GL46.GL_TRIANGLES);
            this.getSunShadowFBO().unBindFBO();
        }

        blurShader.endShading();

        //Vector2i resolution = this.getSunLightShadow().getShadowMapResolution();
        //OpenGLRenderer.setViewPort(resolution);
        //blurring.beginShading();
        //blurring.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), UniformFunctions.MAT4F(TransformUtils.getModelOrthographicMatrix(screenModel.getPose(), TransformUtils.getOrthographic2DMatrix(0, resolution.x, resolution.y, 0))));
        //for (int i = 0; i < this.getSunLightShadow().getTotalCascades(); i++) {
        //    GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
        //    sunShadowFBO.connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
        //    blurring.performUniform(new UniformString(DefaultUniformDefinitions.BLUR), UniformFunctions.FLOAT(blurringConst));
        //    blurring.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), sunShadowFBO.getTextureByIndex(i));
        //    JGemsHelper.render().renderModel2D(screenModel, GL46.GL_TRIANGLES);
        //}
        //blurring.endShading();
        //sunShadowFBO.unBindFBO();
    }

    @Override
    public void createResources() {
        T2DAttachmentContainer shadow = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA32F, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA32F, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA32F, GL46.GL_RGBA);
        }};
        this.getSunShadowFBO().createFrameBuffer2DTexture(this.getShadowMapResolution(), shadow, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer buffer = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA32F, GL46.GL_RGBA);
        }};
        this.buffer.createFrameBuffer2DTexture(this.getShadowMapResolution(), buffer, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        this.getSunShadowFBO().clearFBO();
        this.buffer.clearFBO();
    }

    public void setDefaultCascadeSplits() {
        this.cascadeSplits.set(0.6f, 0.6f, 0.6f);
    }

    public JGemsShaderManager getBlurShader() {
        return this.blurShader;
    }

    public void setCascadeSplits(Vector3f vector3f) {
        this.cascadeSplits.set(vector3f);
    }

    public List<Cascade> getCascades() {
        return this.cascades;
    }

    public FBOTexture2DProgram getSunShadowFBO() {
        return this.sunShadowFBO;
    }

    public int getTotalCascades() {
        return this.totalCascades;
    }

    public Vector3f getCascadeSplits() {
        return cascadeSplits;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public SunLightShadow setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public static class Cascade {
        private final Matrix4f lightProjectionViewMatrix;
        private float splitDistance;

        public Cascade() {
            this.lightProjectionViewMatrix = new Matrix4f();
        }

        public float getSplitDistance() {
            return this.splitDistance;
        }

        public void setSplitDistance(float splitDistance) {
            this.splitDistance = splitDistance;
        }

        public Matrix4f getLightProjectionViewMatrix() {
            return new Matrix4f(this.lightProjectionViewMatrix);
        }

        public void setLightProjectionViewMatrix(Matrix4f Matrix4f) {
            this.lightProjectionViewMatrix.set(Matrix4f);
        }
    }
}
