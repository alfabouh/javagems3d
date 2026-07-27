/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.JGems3D;
import javagems3d.JGemsRandom;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.Texture2DProgram;
import javagems3d.graphics.rendering.programs.textures.TextureSimple2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
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
    private final JGemsShaderManager ssaoBlurring;

    private ITexture2DProgram ssaoNoiseTexture;
    private ITexture2DProgram ssaoKernelTexture;
    private ITexture2DProgram ssaoBufferTexture;

    private int quality;
    private float ssaoRange;
    private float ssaoBias;
    private float ssaoRadius;

    private boolean enabled;

    public SSAORenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull FBOTexture2DProgram gBuffer, @NotNull JGemsShaderManager ssaoComputing, @NotNull JGemsShaderManager ssaoBlurring) {
        super(openGLRenderer);
        this.gBuffer = gBuffer;
        this.ssaoComputing = ssaoComputing;
        this.ssaoBlurring = ssaoBlurring;
        this.quality = 3;

        this.enabled = true;

        this.ssaoRange = JGemsConfig.SYSTEM.SSAO_RANGE;
        this.ssaoBias =  JGemsConfig.SYSTEM.SSAO_BIAS;
        this.ssaoRadius = JGemsConfig.SYSTEM.SSAO_RADIUS;
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
            this.ssaoNoiseTexture = this.calcSSAONoise(JGemsConfig.SYSTEM.SSAO_NOISE_SIZE * JGemsConfig.SYSTEM.SSAO_NOISE_SIZE);
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
        if (this.getSsaoBufferTexture() == null || !JGemsConfig.SYSTEM.USE_SSAO || !this.isEnabled()) {
            GL46.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            return;
        }
        FBOTexture2DProgram gBuffer = this.getGBuffer();
        Vector2i windowSize = this.getRenderingResolution();
        JGemsShaderManager ssaoComputeShader = this.getSsaoComputing();
        ssaoComputeShader.beginComputing();

        float multiplier = this.quality == 3 ? 1.0f : this.quality == 2 ? 0.8f : 0.6f;

        ssaoComputeShader.performUniform(new UniformString(DefaultUniformDefinitions.SSAO_BIAS), UniformFunctions.FLOAT(this.getSsaoBias() * (1.0f + (1.0f - multiplier))));
        ssaoComputeShader.performUniform(new UniformString(DefaultUniformDefinitions.SSAO_RADIUS), UniformFunctions.FLOAT(this.getSsaoRadius() * multiplier));
        ssaoComputeShader.performUniform(new UniformString(DefaultUniformDefinitions.SSAO_RANGE), UniformFunctions.FLOAT(this.getSsaoRange()));

        ssaoComputeShader.performUniform(new UniformString(DefaultUniformDefinitions.NOISE_SCALE), UniformFunctions.VEC2I(new Vector2i(windowSize).div(JGemsConfig.SYSTEM.SSAO_NOISE_SIZE)));
        ssaoComputeShader.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getPerspectiveMatrix()));
        ssaoComputeShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_POSITIONS), gBuffer.getTextureByIndex(0));
        ssaoComputeShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_NORMALS), gBuffer.getTextureByIndex(1));
        ssaoComputeShader.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.SSAO_NOISE), this.getSsaoNoiseTexture());
        ssaoComputeShader.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.SSAO_KERNEL), this.getSsaoKernelTexture());
        GL46.glBindImageTexture(4, this.getSsaoBufferTexture().getTextureId(), 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_RGBA16F);
        int groupCountX = (windowSize.x + 16 - 1) / 16;
        int groupCountY = (windowSize.y + 16 - 1) / 16;
        ssaoComputeShader.dispatchComputeShader(groupCountX, groupCountY, 1, GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        ssaoComputeShader.endComputing();

        JGemsShaderManager ssaoBlur = this.getSsaoBlurring();
        ssaoBlur.beginShading();
        ssaoBlur.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.getSsaoBufferTexture());
        ssaoBlur.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        ssaoBlur.endShading();

        /*
                JGemsShaderManager ssaoBlur = JGemsResourceManager.globalShaderAssets.blur13;
        ssaoBlur.beginShading();
        ssaoBlur.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        ssaoBlur.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.getSsaoBufferTexture());
        ssaoBlur.performUniform(new UniformString(DefaultUniformDefinitions.DIRECTION), UniformFunctions.VEC2F(new Vector2f(1.0f, 1.0f)));
        JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        ssaoBlur.endShading();

        ssaoBlur.beginShading();
        ssaoBlur.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        ssaoBlur.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.getSsaoBufferTexture());
        ssaoBlur.performUniform(new UniformString(DefaultUniformDefinitions.DIRECTION), UniformFunctions.VEC2F(new Vector2f(-1.0f, -1.0f)));
        JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        ssaoBlur.endShading();
         */
    }

    protected Texture2DProgram calcSSAOKernel(int size) {
        Texture2DProgram texture2DProgram = new Texture2DProgram(true);
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(size * 3);
        for (int i = 0; i < size; ++i) {
            float x = JGemsRandom.getRandom().nextFloat() * 2.0f - 1.0f;
            float y = JGemsRandom.getRandom().nextFloat() * 2.0f - 1.0f;
            float z = JGemsRandom.getRandom().nextFloat();

            Vector3f sample = new Vector3f(x, y, z);
            sample.normalize();
            sample.mul(JGemsRandom.getRandom().nextFloat());

            float scale = (float) i / ((float) size);
            scale = JGemsHelper.math().interpolate(0.1f, 1.0f, scale * scale);
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
        Texture2DProgram texture2DProgram = new Texture2DProgram(true);
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(size * 3);
        for (int i = 0; i < size; ++i) {
            float x = JGemsRandom.getRandom().nextFloat() * 2.0f - 1.0f;
            float y = JGemsRandom.getRandom().nextFloat() * 2.0f - 1.0f;
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

    protected ITexture2DProgram createSSAOBuffer(Vector2i size) {
        TextureSimple2DProgram texture2DProgram = new TextureSimple2DProgram();
        texture2DProgram.createTexture(size, new Texture2DProgram.Properties(GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_LINEAR, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, null), null);
        return texture2DProgram;
    }

    protected Vector3i getSSAOParams(Vector2i windowSize) {
        return switch (this.getQuality()) {
            case 1 -> new Vector3i((int) (windowSize.x * 1.0f), (int) (windowSize.y * 1.0f), 4);
            case 2 -> new Vector3i((int) (windowSize.x * 1.0f), (int) (windowSize.y * 1.0f), 5);
            case 3 -> new Vector3i((int) (windowSize.x * 1.0f), (int) (windowSize.y * 1.0f), 6);
            default -> null;
        };
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public SSAORenderProcessor setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public float getSsaoRange() {
        return this.ssaoRange;
    }

    public SSAORenderProcessor setSsaoRange(float ssaoRange) {
        this.ssaoRange = ssaoRange;
        return this;
    }

    public float getSsaoBias() {
        return this.ssaoBias;
    }

    public SSAORenderProcessor setSsaoBias(float ssaoBias) {
        this.ssaoBias = ssaoBias;
        return this;
    }

    public float getSsaoRadius() {
        return this.ssaoRadius;
    }

    public SSAORenderProcessor setSsaoRadius(float ssaoRadius) {
        this.ssaoRadius = ssaoRadius;
        return this;
    }

    public int getQuality() {
        return this.quality;
    }

    public SSAORenderProcessor setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public JGemsShaderManager getSsaoBlurring() {
        return this.ssaoBlurring;
    }

    public JGemsShaderManager getSsaoComputing() {
        return this.ssaoComputing;
    }

    public FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    public ITexture2DProgram getSsaoNoiseTexture() {
        return this.ssaoNoiseTexture;
    }

    public ITexture2DProgram getSsaoKernelTexture() {
        return this.ssaoKernelTexture;
    }

    public ITexture2DProgram getSsaoBufferTexture() {
        return this.ssaoBufferTexture;
    }

    public boolean isValid() {
        return this.getSsaoBufferTexture() != null;
    }
}
