package javagems3d.graphics.rendering.scene.renderer.indirect.particles;

import javagems3d.graphics.environment.particles.data.ParticleFXRenderConfig;
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
import org.joml.Vector3f;
import org.joml.Vector4f;
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
        Iterator<ParticleFX> iterator = particles.iterator();
        while (iterator.hasNext()) {
            ParticleFX particleFX = iterator.next();
            if (pipeline.equals(Pipeline.SOLID_SCENE)) {
                if (particleFX.getParticleFXRenderConfig().getDiffuseColor().color().w < 1.0f) {
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
       // final Vector3f sunFactor = new Vector3f();
       // final Vector3f pointLightFactor = new Vector3f();
       // final Vector3f spotLightFactor = new Vector3f();

        ParticleFXRenderConfig config = particleFX.getParticleFXRenderConfig();
        {
            Vector4f c = config.getDiffuseColor().color();
            properties.putFloat(c.x);
            properties.putFloat(c.y);
            properties.putFloat(c.z);
            properties.putFloat(c.w);
        }

        {
            Vector3f e = config.getEmissionColor().color();
            properties.putFloat(e.x);
            properties.putFloat(e.y);
            properties.putFloat(e.z);
        }

        {
            properties.putInt(particleFX.getCurrentTextureID());
        }

        {
            properties.putFloat(config.getEmissionStrength());
            properties.putFloat(config.getAlphaDiscard());
        }

        {
            long descriptor = config.getTexture().getBindingHandler();
            properties.putInt((int) (descriptor & 0xFFFFFFFFL));
            properties.putInt((int) ((descriptor >>> 32) & 0xFFFFFFFFL));
        }

        {
            properties.putInt(config.getCellsXY().x);
            properties.putInt(config.getCellsXY().y);
        }

        {
            properties.putFloat(particleFX.interpolationPoint());
        }

        {
            properties.putInt(particleFX.getInterpolateWithTextureID());
        }

      //  {
      //      properties.putFloat(sunFactor.x);
      //      properties.putFloat(sunFactor.y);
      //      properties.putFloat(sunFactor.z);
      //  }
      //  properties.putInt(0); //PADDING
//
      //  {
      //      properties.putFloat(pointLightFactor.x);
      //      properties.putFloat(pointLightFactor.y);
      //      properties.putFloat(pointLightFactor.z);
      //  }
      //  properties.putInt(0); //PADDING
//
      //  {
      //      properties.putFloat(spotLightFactor.x);
      //      properties.putFloat(spotLightFactor.y);
      //      properties.putFloat(spotLightFactor.z);
      //  }
      //  properties.putInt(0); //PADDING
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