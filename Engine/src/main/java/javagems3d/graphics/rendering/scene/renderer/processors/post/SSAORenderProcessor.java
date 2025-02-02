package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.graphics.rendering.programs.textures.Texture2DProgram;
import javagems3d.graphics.rendering.programs.textures.TextureSimple2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class SSAORenderProcessor extends IRenderProcessor.Template {
    private final FBOTexture2DProgram gBuffer;
    private final JGemsShaderManager ssaoComputing;

    private ITextureProgram ssaoNoiseTexture;
    private ITextureProgram ssaoKernelTexture;
    private ITextureProgram ssaoBufferTexture;

    public SSAORenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull FBOTexture2DProgram gBuffer, @NotNull JGemsShaderManager ssaoComputing) {
        super(openGLRenderer);
        this.gBuffer = gBuffer;
        this.ssaoComputing = ssaoComputing;
    }

    @Override
    public void createResources() {
        this.createSSAOResources(this.getSSAOParams(this.getRenderingResolution()));
    }

    @Override
    public void destroyResources() {
        this.destroySsaoTextures();
    }

    protected void createSSAOResources(@Nullable Vector3i ssaoParams) {
        if (ssaoParams != null) {
            this.ssaoKernelTexture = this.calcSSAOKernel(ssaoParams.z * ssaoParams.z);
            this.ssaoNoiseTexture = this.calcSSAONoise(JGemsRenderingGlobalConstants.SSAO_NOISE_SIZE * JGemsRenderingGlobalConstants.SSAO_NOISE_SIZE);
            this.ssaoBufferTexture = this.createSSAOBuffer(new Vector2i(ssaoParams.x, ssaoParams.y));
        }
    }

    protected void destroySsaoTextures() {
        if (this.getSsaoNoiseTexture() != null) {
            this.getSsaoNoiseTexture().clear();
            this.ssaoNoiseTexture = null;
        }
        if (this.getSsaoKernelTexture() != null) {
            this.getSsaoKernelTexture().clear();
            this.ssaoKernelTexture = null;
        }
        if (this.getSsaoBufferTexture() != null) {
            this.getSsaoBufferTexture().clear();
            this.ssaoBufferTexture = null;
        }
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        if (this.getSsaoBufferTexture() == null || !JGemsRenderingGlobalConstants.USE_SSAO) {
            GL46.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            return;
        }
        FBOTexture2DProgram gBuffer = this.getGBuffer();
        Vector2i windowSize = this.getRenderingResolution();
        JGemsShaderManager ssaoComputeShader = this.getSsaoComputing();
        ssaoComputeShader.beginComputing();

        ssaoComputeShader.performUniform(new UniformString("ssao_bias"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.SSAO_BIAS));
        ssaoComputeShader.performUniform(new UniformString("ssao_radius"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.SSAO_RADIUS));
        ssaoComputeShader.performUniform(new UniformString("ssao_range"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.SSAO_RANGE));

        ssaoComputeShader.performUniform(new UniformString("noiseScale"), UniformFunctions.VEC2I(new Vector2i(windowSize).div(JGemsRenderingGlobalConstants.SSAO_NOISE_SIZE)));
        ssaoComputeShader.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getPerspectiveMatrix()));
        ssaoComputeShader.performUniformTexture(new UniformString("gPositions"), gBuffer.getTextureByIndex(0));
        ssaoComputeShader.performUniformTexture(new UniformString("gNormals"), gBuffer.getTextureByIndex(1));
        ssaoComputeShader.performUniformTexture(new UniformString("ssaoNoise"), this.getSsaoNoiseTexture());
        ssaoComputeShader.performUniformTexture(new UniformString("ssaoKernel"), this.getSsaoKernelTexture());
        GL46.glBindImageTexture(4, this.getSsaoBufferTexture().getTextureId(), 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_RGBA16F);
        ssaoComputeShader.dispatchComputeShader(windowSize.x / 8, windowSize.y / 8, 1, GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        ssaoComputeShader.endComputing();

        JGemsShaderManager ssaoBlur = JGemsResourceManager.globalShaderAssets.blur_ssao;
        ssaoBlur.beginShading();
        ssaoBlur.performUniformTexture(new UniformString("texture_sampler"), this.getSsaoBufferTexture());
        ssaoBlur.getUtils().performOrthographicMatrix(this.getOpenGLRenderer().getScreenModel());
        JGemsHelper.RENDERING.renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        ssaoBlur.endShading();
    }

    protected Texture2DProgram calcSSAOKernel(int size) {
        Texture2DProgram texture2DProgram = new Texture2DProgram();
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(size * 3);
        for (int i = 0; i < size; ++i) {
            float x = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            float y = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            float z = JGems3D.random.nextFloat();

            Vector3f sample = new Vector3f(x, y, z);
            sample.normalize();
            sample.mul(JGems3D.random.nextFloat());

            float scale = (float) i / ((float) size);
            scale = JGemsHelper.MATH.lerp(0.1f, 1.0f, scale * scale);
            sample.mul(scale);

            floatBuffer.put(sample.x);
            floatBuffer.put(sample.y);
            floatBuffer.put(sample.z);
        }
        floatBuffer.flip();
        int s = (int) Math.sqrt(size);
        texture2DProgram.createTexture(new Vector2i(s), new Texture2DProgram.Properties(GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_REPEAT, GL46.GL_REPEAT, null), floatBuffer);
        MemoryUtil.memFree(floatBuffer);
        return texture2DProgram;
    }

    protected Texture2DProgram calcSSAONoise(int size) {
        Texture2DProgram texture2DProgram = new Texture2DProgram();
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(size * 3);
        for (int i = 0; i < size; ++i) {
            float x = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            float y = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            floatBuffer.put(x);
            floatBuffer.put(y);
            floatBuffer.put(0.0f);
        }
        floatBuffer.flip();
        int s = (int) Math.sqrt(size);
        texture2DProgram.createTexture(new Vector2i(s), new Texture2DProgram.Properties(GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_REPEAT, GL46.GL_REPEAT, null), floatBuffer);
        MemoryUtil.memFree(floatBuffer);
        return texture2DProgram;
    }

    protected ITextureProgram createSSAOBuffer(Vector2i size) {
        TextureSimple2DProgram texture2DProgram = new TextureSimple2DProgram();
        texture2DProgram.createTexture(size, new Texture2DProgram.Properties(GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_LINEAR, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, null), null);
        return texture2DProgram;
    }

    protected Vector3i getSSAOParams(Vector2i windowSize) {
        switch (JGems3D.get().getGameSettings().ssao.getValue()) {
            case 1: {
                return new Vector3i((int) (windowSize.x * 1.0f), (int) (windowSize.y * 1.0f), 3);
            }
            case 2: {
                return new Vector3i((int) (windowSize.x * 1.0f), (int) (windowSize.y * 1.0f), 4);
            }
            case 3: {
                return new Vector3i((int) (windowSize.x * 1.0f), (int) (windowSize.y * 1.0f), 6);
            }
            case 0:
            default: {
                return null;
            }
        }
    }

    public JGemsShaderManager getSsaoComputing() {
        return this.ssaoComputing;
    }

    public FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    public ITextureProgram getSsaoNoiseTexture() {
        return this.ssaoNoiseTexture;
    }

    public ITextureProgram getSsaoKernelTexture() {
        return this.ssaoKernelTexture;
    }

    public ITextureProgram getSsaoBufferTexture() {
        return this.ssaoBufferTexture;
    }

    public boolean isValid() {
        return this.getSsaoBufferTexture() != null;
    }
}
