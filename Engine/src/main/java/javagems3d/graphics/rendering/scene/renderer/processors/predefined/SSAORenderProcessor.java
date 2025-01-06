package javagems3d.graphics.rendering.scene.renderer.processors.predefined;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.TextureProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class SSAORenderProcessor extends IRenderProcessor.Template {
    private final IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
    private final JGemsShaderManager ssaoComputing;
    private FBOTexture2DProgram ssaoBuffer;

    private TextureProgram ssaoNoiseTexture;
    private TextureProgram ssaoKernelTexture;
    private TextureProgram ssaoBufferTexture;

    public SSAORenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull IndirectGeometryRenderProcessor indirectGeometryRenderProcessor, @NotNull JGemsShaderManager ssaoComputing) {
        super(openGLRenderer);
        this.indirectGeometryRenderProcessor = indirectGeometryRenderProcessor;
        this.ssaoComputing = ssaoComputing;
    }

    @Override
    public void createResources() {
        this.createSSAOResources(this.getSSAOParams(this.getWindowSize()));
        this.ssaoBuffer = new FBOTexture2DProgram(true);
        T2DAttachmentContainer ssao = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_R16F, GL46.GL_RED);
        }};
        this.ssaoBuffer.createFrameBuffer2DTexture(this.getWindowSize(), ssao, false, GL46.GL_LINEAR, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        this.destroySsaoTextures();
        if (this.getSSAOBuffer() != null) {
            this.getSSAOBuffer().clearFBO();
        }
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
    public void onRender(FrameTicking frameTicking) {
        if (this.getSsaoBufferTexture() == null || !JGemsRenderingGlobalConstants.USE_SSAO) {
            this.getSSAOBuffer().bindFBO();
            GL46.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            this.getSSAOBuffer().unBindFBO();
            return;
        }
        FBOTexture2DProgram gBuffer = this.getIndirectGeometryRenderProcessor().getGBuffer();
        Vector2i windowSize = this.getWindowSize();
        JGemsShaderManager ssaoComputeShader = this.getSsaoComputing();
        ssaoComputeShader.beginComputing();

        ssaoComputeShader.performUniform(new UniformString("ssao_bias"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.SSAO_BIAS));
        ssaoComputeShader.performUniform(new UniformString("ssao_radius"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.SSAO_RADIUS));
        ssaoComputeShader.performUniform(new UniformString("ssao_range"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.SSAO_RANGE));

        ssaoComputeShader.performUniform(new UniformString("noiseScale"), UniformFunctions.VEC2I(new Vector2i(windowSize).div(JGemsRenderingGlobalConstants.SSAO_NOISE_SIZE)));
        ssaoComputeShader.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(JGemsHelper.RENDERING.getMainPerspectiveMatrix()));
        ssaoComputeShader.performUniformTexture(new UniformString("gPositions"), gBuffer.getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture(new UniformString("gNormals"), gBuffer.getTextureIDByIndex(1), GL46.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture(new UniformString("ssaoNoise"), this.getSsaoNoiseTexture().getTextureId(), GL46.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture(new UniformString("ssaoKernel"), this.getSsaoKernelTexture().getTextureId(), GL46.GL_TEXTURE_2D);
        GL46.glBindImageTexture(4, this.getSsaoBufferTexture().getTextureId(), 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_RGBA16F);
        ssaoComputeShader.dispatchComputeShader(windowSize.x / 8, windowSize.y / 8, 1, GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        ssaoComputeShader.endComputing();

        JGemsShaderManager ssaoBlur = JGemsResourceManager.globalShaderAssets.blur_ssao;
        this.getSSAOBuffer().bindFBO();
        ssaoBlur.beginShading();
        ssaoBlur.performUniformTexture(new UniformString("texture_sampler"), this.getSsaoBufferTexture().getTextureId(), GL46.GL_TEXTURE_2D);
        ssaoBlur.getUtils().performOrthographicMatrix(this.getOpenGLRenderer().getScreenModel());
        JGemsHelper.RENDERING.renderModel(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        ssaoBlur.endShading();
        this.getSSAOBuffer().unBindFBO();
    }

    protected TextureProgram calcSSAOKernel(int size) {
        TextureProgram textureProgram = new TextureProgram();
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
        textureProgram.createTexture(new Vector2i(s), GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_REPEAT, GL46.GL_REPEAT, null, floatBuffer);
        MemoryUtil.memFree(floatBuffer);
        return textureProgram;
    }

    protected TextureProgram calcSSAONoise(int size) {
        TextureProgram textureProgram = new TextureProgram();
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
        textureProgram.createTexture(new Vector2i(s), GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_REPEAT, GL46.GL_REPEAT, null, floatBuffer);
        MemoryUtil.memFree(floatBuffer);
        return textureProgram;
    }

    protected TextureProgram createSSAOBuffer(Vector2i windowSize) {
        TextureProgram textureProgram = new TextureProgram();
        textureProgram.createTexture(windowSize, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_LINEAR, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, null);
        return textureProgram;
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

    public FBOTexture2DProgram getSSAOBuffer() {
        return this.ssaoBuffer;
    }

    protected IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
        return this.indirectGeometryRenderProcessor;
    }

    public TextureProgram getSsaoNoiseTexture() {
        return this.ssaoNoiseTexture;
    }

    public TextureProgram getSsaoKernelTexture() {
        return this.ssaoKernelTexture;
    }

    public TextureProgram getSsaoBufferTexture() {
        return this.ssaoBufferTexture;
    }
}
