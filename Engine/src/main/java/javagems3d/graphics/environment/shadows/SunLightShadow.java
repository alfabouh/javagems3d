package javagems3d.graphics.environment.shadows;

import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

public class SunLightShadow extends Shadow {
    private final FBOTexture2DProgram sunShadowFBO;
    private List<Cascade> cascades;

    public SunLightShadow(IEnvironment environment, Vector2i shadowMapResolution) {
        super(environment, shadowMapResolution);
        this.sunShadowFBO = new FBOTexture2DProgram(true);
        this.initCascades();
    }

    private void initCascades() {
        this.cascades = new ArrayList<>();
        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            this.cascades.add(new Cascade());
        }
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

        Vector4f sunPos = new Vector4f(this.getEnvironment().getSkyBox().getSun().getSunPosition(), 0.0f);

        float[] cascadeSplitLambda = new float[]{0.6f, 0.6f, 0.6f};
        float[] cascadeSplits = new float[JGemsRenderingGlobalConstants.CASCADE_SPLITS];

        float nearClip = JGemsRenderingGlobalConstants.Z_NEAR;
        float farClip = JGemsRenderingGlobalConstants.Z_FAR;
        float clipRange = farClip - nearClip;

        float minZ = nearClip;
        float maxZ = nearClip + clipRange;

        float range = maxZ - minZ;
        float ratio = maxZ / minZ;

        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            float p = (i + 1) / (float) JGemsRenderingGlobalConstants.CASCADE_SPLITS;
            float log = (float) (minZ * Math.pow(ratio, p));
            float uniform = minZ + range * p;
            float d = cascadeSplitLambda[i] * (log - uniform) + uniform;
            cascadeSplits[i] = (d - nearClip) / clipRange;
        }

        float lastSplitDist = 0.0f;
        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
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
            Matrix4f lightViewMatrix = TransformUtils.getLookAtMatrix(eye, up, frustumCenter);

            Matrix4f lightOrthoMatrix = TransformUtils.getOrthographic3DMatrix(minExtents.x, maxExtents.x, minExtents.y, maxExtents.y, 0.0f, maxExtents.z - minExtents.z, true);

            Cascade cascade = this.getCascades().get(i);
            cascade.setSplitDistance((nearClip + splitDist * clipRange) * -1.0f);

            Matrix4f shadowMatrix = new Matrix4f(lightOrthoMatrix.mul(lightViewMatrix));
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

    @Override
    public void createResources() {
        T2DAttachmentContainer shadow = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA32F, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA32F, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA32F, GL46.GL_RGBA);
        }};
        this.getSunShadowFBO().createFrameBuffer2DTexture(this.getShadowMapResolution(), shadow, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        this.getSunShadowFBO().clearFBO();
    }

    public List<Cascade> getCascades() {
        return this.cascades;
    }

    public FBOTexture2DProgram getSunShadowFBO() {
        return this.sunShadowFBO;
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
