package javagems3d.graphics.environment.shadows;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.SpotLight;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

public class SpotLightShadow extends Shadow {
    private final FBOTexture2DProgram spotLightShadowFBO;
    private final FBOTexture2DProgram buffer;

    private JGemsShaderManager blurShader;

    private final int id;
    private SpotLight spotLight;
    private Matrix4f shadowDirection;
   // private float farPlane;

    public SpotLightShadow(IEnvironment environment, Vector2i shadowMapResolution, int id, @Nullable JGemsShaderManager blurShader) {
        super(environment, shadowMapResolution);
        this.id = id;
        this.shadowDirection = null;
        this.spotLight = null;
        this.spotLightShadowFBO = new FBOTexture2DProgram(true, true);
        this.buffer = new FBOTexture2DProgram(true, true);
        this.blurShader = blurShader;
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
        this.buffer.createFrameBuffer2DTexture(this.getShadowMapResolution(), shadow, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        this.getSpotLightFBO().clearFBO();
        this.buffer.clearFBO();
    }

    public void blur(Model2D screenModel) {
        final JGemsShaderManager blurShader = this.blurShader;
        if (this.getSpotLight() != null && blurShader == null) {
            return;
        }
        Vector2i resolution = this.getShadowMapResolution();
        final Matrix4f orthoMatrix = TransformUtils.getModelOrthographicMatrix(screenModel.getPose(), TransformUtils.getOrthographic2DMatrix(0, resolution.x, resolution.y, 0));
        OpenGLRenderer.setViewPort(resolution);
        blurShader.beginShading();
        blurShader.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), UniformFunctions.MAT4F(orthoMatrix));

        this.buffer.bindFBO();
        GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
        blurShader.performUniform(new UniformString(DefaultUniformDefinitions.DIRECTION), UniformFunctions.VEC2F(new Vector2f(1.0f, 0.0f)));
        blurShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.getSpotLightFBO().getTextureByIndex(0));
        JGemsHelper.render().renderModel2D(screenModel, GL46.GL_TRIANGLES);
        this.buffer.unBindFBO();

        this.getSpotLightFBO().bindFBO();
        GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
        blurShader.performUniform(new UniformString(DefaultUniformDefinitions.DIRECTION), UniformFunctions.VEC2F(new Vector2f(0.0f, 1.0f)));
        blurShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.buffer.getTextureByIndex(0));
        JGemsHelper.render().renderModel2D(screenModel, GL46.GL_TRIANGLES);
        this.getSpotLightFBO().unBindFBO();

        blurShader.endShading();
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
        return this.spotLightShadowFBO;
    }
}
