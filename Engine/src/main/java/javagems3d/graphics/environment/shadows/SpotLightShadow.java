package javagems3d.graphics.environment.shadows;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.SpotLight;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.transformation.TransformUtils;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

public class SpotLightShadow extends Shadow {
    private final FBOTexture2DProgram pointLightCubeMap;
    private final int id;
    private SpotLight spotLight;
    private Matrix4f shadowDirection;
   // private float farPlane;

    public SpotLightShadow(IEnvironment environment, Vector2i shadowMapResolution, int id) {
        super(environment, shadowMapResolution);
        this.id = id;
        this.shadowDirection = null;
        this.spotLight = null;
        this.pointLightCubeMap = new FBOTexture2DProgram(true, true);
    }

    public void configureMatrix() {
       // this.farPlane = Math.min(this.getPointLight().getClipRadius() / 2.0f, 64.0f);
        this.shadowDirection = TransformUtils.getDirectionViewSpace(this.getSpotLight().getLightPosition(), this.nearPlane(), this.farPlane(), this.getSpotLight().getFOV(), this.getSpotLight().getLightDirection());
    }

    public void setSpotLight(SpotLight spotLight) {
        this.spotLight = spotLight;
    }

    @Override
    public void createResources() {
        T2DAttachmentContainer shadow = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RG32F, GL46.GL_RG);
        }};
        this.getSpotLightFBO().createFrameBuffer2DTexture(this.getShadowMapResolution(), shadow, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        this.getSpotLightFBO().clearFBO();
    }

    public int getId() {
        return this.id;
    }

    public float farPlane() {
        return 256.0f;
    }

    public float nearPlane() {
        return 0.1f;
    }

    public boolean isAttachedToLight() {
        return this.getSpotLight() != null;
    }

    public SpotLight getSpotLight() {
        return this.spotLight;
    }

    public Matrix4f getShadowProjectionView() {
        return this.shadowDirection;
    }

    public FBOTexture2DProgram getSpotLightFBO() {
        return this.pointLightCubeMap;
    }
}
