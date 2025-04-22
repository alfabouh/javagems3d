package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;

public class DeferredColorRenderProcessor extends IRenderProcessor.Template {
    private final JGemsShaderManager lightPassShader;
    private final FBOTexture2DProgram gBuffer;
    private final FBOTexture2DProgram ssaoBuffer;

    public DeferredColorRenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @Nullable FBOTexture2DProgram gBuffer, @Nullable FBOTexture2DProgram ssaoBuffer, @NotNull JGemsShaderManager lightPassShader) {
        super(openGLRenderer);
        this.lightPassShader = lightPassShader;
        this.gBuffer = gBuffer;
        this.ssaoBuffer = ssaoBuffer;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        FBOTexture2DProgram gBuffer = this.getGBuffer();
        FBOTexture2DProgram ssaoBuffer = this.getSsaoBuffer();

        JGemsShaderManager deferredShader = this.getLightPassShader();
        deferredShader.beginShading();
        final ICubeMapProgram cubeMapProgram = this.getWorld().getEnvironment().getSkyBox().getTexture();
        deferredShader.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(this.getWorld().getCamera().getCamPosition()));
        if (cubeMapProgram != null && deferredShader.isUniformExist(new UniformString("ambient_cubemap"))) {
            deferredShader.performUniformTextureBindless(new UniformString("ambient_cubemap"), cubeMapProgram);
            if (deferredShader.isUniformExist(new UniformString("useCubeMap"))) {
                deferredShader.performUniform(new UniformString("useCubeMap"), UniformFunctions.BOOLEAN(true));
            }
        } else {
            if (deferredShader.isUniformExist(new UniformString("useCubeMap"))) {
                deferredShader.performUniform(new UniformString("useCubeMap"), UniformFunctions.BOOLEAN(false));
            }
        }
        deferredShader.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getCameraViewMatrix()));
        deferredShader.performUniformTexture(new UniformString("gPositions"), gBuffer.getTextureByIndex(0));
        deferredShader.performUniformTexture(new UniformString("gNormals"), gBuffer.getTextureByIndex(1));
        deferredShader.performUniformTexture(new UniformString("gTexture"), gBuffer.getTextureByIndex(2));
        deferredShader.performUniformTexture(new UniformString("gEmission"), gBuffer.getTextureByIndex(3));
        deferredShader.performUniformTexture(new UniformString("gMetallicRoughness"), gBuffer.getTextureByIndex(4));
        if (ssaoBuffer != null) {
            deferredShader.performUniformTexture(new UniformString("ssao_map"), ssaoBuffer.getTextureByIndex(0));
            deferredShader.performUniform(new UniformString("isSsaoValid"), UniformFunctions.BOOLEAN(true));
        } else {
            deferredShader.performUniform(new UniformString("isSsaoValid"), UniformFunctions.BOOLEAN(false));
        }
        deferredShader.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsHelper.render().performShadowsInfo(this.getOpenGLRenderer().getWorld().getEnvironment(), deferredShader);
        JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        deferredShader.endShading();
    }

    protected FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    protected @Nullable FBOTexture2DProgram getSsaoBuffer() {
        return this.ssaoBuffer;
    }

    public JGemsShaderManager getLightPassShader() {
        return this.lightPassShader;
    }
}