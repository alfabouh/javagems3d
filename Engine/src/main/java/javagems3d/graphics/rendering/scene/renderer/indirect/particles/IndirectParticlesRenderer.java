package javagems3d.graphics.rendering.scene.renderer.indirect.particles;

import javagems3d.graphics.environment.particles.data.ParticleFXRenderData;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.IndirectCommandsProgram;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.scene_objects.IndirectSceneObjectsRenderer;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class IndirectParticlesRenderer {
    protected static final int SSBO_DATASETS_MATRICES_SIZE = JGemsConfig.SYSTEM.MAX_INDIRECT_PARTICLES_RENDERING_MESH_DATASETS * 16;
    protected static final int SSBO_DATASETS_ENT_IDS_SIZE = JGemsConfig.SYSTEM.MAX_INDIRECT_PARTICLES_RENDERING_MESH_DATASETS;
    protected static final int SSBO_DATASETS_PROPERTIES_SIZE = JGemsConfig.SYSTEM.INDIRECT_PARTICLES_RENDERING_PROPERTIES_PACK_SIZE * JGemsConfig.SYSTEM.MAX_INDIRECT_PARTICLES_RENDERING_MESH_PROPERTIES;

    private final Pipeline pipeline;

    private final Set<ParticleFX> rejected;
    protected Collection<ParticleFX> indirectMeshObjects;
    private final OpenGLRenderer openGLRenderer;

    private final ShaderStorageBufferObject indirectSSBO;
    private final ShaderStorageBufferObject propertiesSSBO;
    private final IndirectSceneObjectsRenderer.IRenderingFunction renderingFunction;

    public IndirectParticlesRenderer(@NotNull OpenGLRenderer openGLRenderer, @NotNull IndirectSceneObjectsRenderer.IRenderingFunction renderingFunction, @NotNull ShaderStorageBufferObject indirectSSBO, @NotNull ShaderStorageBufferObject propertiesSSBO, @NotNull Pipeline pipeline) {
        this.pipeline = pipeline;
        this.openGLRenderer = openGLRenderer;

        this.renderingFunction = renderingFunction;
        this.indirectSSBO = indirectSSBO;
        this.propertiesSSBO = propertiesSSBO;

        this.rejected = new HashSet<>();
    }

    protected abstract void processAndRender(@Nullable ArbitraryArguments metaData);
    protected abstract IndirectCommandsProgram createCommands(IndirectBufferProgram renderBuffer, Collection<ParticleFX> particles);

    protected void render(ShaderManager shaderManager, IndirectCommandsProgram indirectCommandsProgram, IndirectBufferProgram renderBuffer, @Nullable ArbitraryArguments metaData) {
        this.getRenderingFunction().func((JGemsShaderManager) shaderManager, indirectCommandsProgram, renderBuffer, metaData == null ? ArbitraryArguments.empty() : metaData);
    }

    protected void fillSSBOWithParticleInformation(Collection<ParticleFX> particles) {
        ByteBuffer properties = MemoryUtil.memAlloc(Float.BYTES * IndirectParticlesRenderer.SSBO_DATASETS_PROPERTIES_SIZE);
        FloatBuffer modelMatrices = MemoryUtil.memAllocFloat(IndirectParticlesRenderer.SSBO_DATASETS_MATRICES_SIZE);
        this.getRejected().clear();
        Iterator<ParticleFX> iterator = particles.iterator();
        while (iterator.hasNext()) {
            ParticleFX particleFX = iterator.next();
            if (pipeline.equals(Pipeline.SCENE)) {
                if (particleFX.getParticleFXRenderData().isTransparent()) {
                    this.getRejected().add(particleFX);
                    iterator.remove();
                    continue;
                }
            }
            this.passMatricesInBuffer(particleFX, modelMatrices);
            this.passPropertiesInBuffer(particleFX, properties);
        }
        this.passBuffersInSSBO(this.getIndirectSSBO(), modelMatrices);
        properties.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(this.getPropertiesSSBO(), 0L, properties);
        MemoryUtil.memFree(properties);
        GL46.glMemoryBarrier(GL46.GL_SHADER_STORAGE_BARRIER_BIT | GL46.GL_COMMAND_BARRIER_BIT);
    }

    protected void passBuffersInSSBO(ShaderStorageBufferObject shaderStorageBufferObject, Buffer... buffers) {
        long offset = 0L;
        for (Buffer buffer : buffers) {
            int elementSize = 0;
            if (buffer instanceof FloatBuffer) {
                elementSize = Float.BYTES;
            } else if (buffer instanceof IntBuffer) {
                elementSize = Integer.BYTES;
            } else if (buffer instanceof ByteBuffer) {
                elementSize = Byte.BYTES;
            }
            long bufferSize = (long) buffer.limit() * elementSize;
            buffer.flip();
            ShaderStorageBufferProgram.updateSubDataSSBO(shaderStorageBufferObject, offset, buffer);
            MemoryUtil.memFree(buffer);
            offset += bufferSize;
        }
    }

    protected void passMatricesInBuffer(ParticleFX particleFX, FloatBuffer matrices) {
        Matrix4f matrix = particleFX.getMatrix();
        matrices.put(matrix.get(new float[16]));
    }

    protected void passPropertiesInBuffer(ParticleFX particleFX, ByteBuffer properties) {
        ParticleFXRenderData renderData = particleFX.getParticleFXRenderData();
        {
            properties.putFloat(renderData.particleFXMaterial().getDiffuseColor().color().x);
            properties.putFloat(renderData.particleFXMaterial().getDiffuseColor().color().y);
            properties.putFloat(renderData.particleFXMaterial().getDiffuseColor().color().z);
            properties.putFloat(renderData.particleFXMaterial().getDiffuseColor().color().w);
        }
        {
            properties.putFloat(renderData.particleFXMaterial().getEmissionColor().color().x);
            properties.putFloat(renderData.particleFXMaterial().getEmissionColor().color().y);
            properties.putFloat(renderData.particleFXMaterial().getEmissionColor().color().z);
        }
        properties.putFloat(0.0f);
        {
            properties.putFloat(renderData.particleFXProperties().getEmissionStrength());
            properties.putFloat(renderData.particleFXProperties().getAlphaDiscard());
        }
        {
            final long descriptorImg = renderData.particleFXMaterial().getTextureMap().getBindingHandler();
            properties.putInt((int)(descriptorImg & 0xFFFFFFFFL)); // low
            properties.putInt((int)((descriptorImg >>> 32) & 0xFFFFFFFFL)); // high
        }
    }

    public IndirectSceneObjectsRenderer.IRenderingFunction getRenderingFunction() {
        return this.renderingFunction;
    }

    public ShaderStorageBufferObject getIndirectSSBO() {
        return this.indirectSSBO;
    }

    public ShaderStorageBufferObject getPropertiesSSBO() {
        return this.propertiesSSBO;
    }

    public Set<ParticleFX> getRejected() {
        return this.rejected;
    }

    public void setIndirectMeshObjects(@NotNull Collection<ParticleFX> sceneObjects) {
        this.indirectMeshObjects = sceneObjects;
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public Collection<ParticleFX> getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }

    public OpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }
}