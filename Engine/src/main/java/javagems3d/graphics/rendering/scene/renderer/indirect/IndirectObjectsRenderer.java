package javagems3d.graphics.rendering.scene.renderer.indirect;

import javagems3d.graphics.objects.rendering.pipeline.enums.Redirections;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.IndirectCommandsProgram;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
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
import java.util.*;

public abstract class IndirectObjectsRenderer {
    protected static final int SSBO_DATASETS_MATRICES_SIZE = JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16;
    protected static final int SSBO_DATASETS_ENT_IDS_SIZE = JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    protected static final int SSBO_DATASETS_MATERIAL_IDS_SIZE = JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    protected static final int SSBO_DATASETS_PROPERTIES_SIZE = JGemsConfig.SYSTEM.INDIRECT_RENDERING_PROPERTIES_PACK_SIZE * JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_PROPERTIES;

    private final Pipeline pipeline;

    private final Set<SceneObject> rejected;
    protected Collection<SceneObject> indirectMeshObjects;
    private final OpenGLRenderer openGLRenderer;

    protected final boolean usePropertiesSSBO;
    protected final boolean useMaterialsSSBO;

    private final ShaderStorageBufferObject indirectSSBO;
    private final ShaderStorageBufferObject propertiesSSBO;

    public IndirectObjectsRenderer(@NotNull OpenGLRenderer openGLRenderer, @NotNull ShaderStorageBufferObject indirectSSBO, @NotNull ShaderStorageBufferObject propertiesSSBO, @NotNull Pipeline pipeline, boolean usePropertiesSSBO, boolean useMaterialsSSBO) {
        this.pipeline = pipeline;
        this.openGLRenderer = openGLRenderer;
        this.usePropertiesSSBO = usePropertiesSSBO;
        this.useMaterialsSSBO = useMaterialsSSBO;

        this.indirectSSBO = indirectSSBO;
        this.propertiesSSBO = propertiesSSBO;

        this.rejected = new HashSet<>();
    }

    protected abstract void processAndRender(@Nullable ArbitraryArguments metaData);
    protected abstract IndirectCommandsProgram createCommands(Mode mode, @NotNull IntBuffer indexes, @Nullable IntBuffer materialIds, IndirectBufferProgram renderBuffer, Collection<SceneObject> sceneObjects);

    protected void render(Operator operator, IndirectCommandsProgram indirectCommandsProgram, IndirectBufferProgram renderBuffer, @Nullable ArbitraryArguments metaData) {
        operator.renderingFunction().func(operator.indirectShader(), indirectCommandsProgram, renderBuffer, metaData == null ? ArbitraryArguments.empty() : metaData);
    }

    protected void fillSSBOWithInformation(@NotNull IntBuffer indexes, @NotNull IntBuffer materialIds, Collection<SceneObject> sceneObjects) {
        ByteBuffer properties = this.isUsePropertiesSSBO() ? MemoryUtil.memAlloc(Float.BYTES * IndirectObjectsRenderer.SSBO_DATASETS_PROPERTIES_SIZE) : null;
        FloatBuffer modelMatrices = MemoryUtil.memAllocFloat(IndirectObjectsRenderer.SSBO_DATASETS_MATRICES_SIZE);
        FloatBuffer deltaFrames = MemoryUtil.memAllocFloat(IndirectObjectsRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
        IntBuffer animationMatricesOffsets = MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
        IntBuffer animationMatricesOffsetsPrev = MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_ENT_IDS_SIZE);

        this.getRejected().clear();
        for (SceneObject sceneObject : sceneObjects) {
            if (pipeline.equals(Pipeline.SCENE) && !sceneObject.getRenderTable().isRedirected(Redirections.TRANSPARENCY__IN__SCENE)) {
                if (sceneObject.getModel().getMeshStructure().hasTransparency()) {
                    this.getRejected().add(sceneObject);
                }
            }
            this.passMatricesInBuffer(this.getPipeline(), sceneObject, modelMatrices);

            int animToPass = sceneObject.isAnimated() ? sceneObject.getAnimationData().getCurrentAnimationFrame().getOffset() : -1;
            int animToPassPrev = sceneObject.isAnimated() ? sceneObject.getAnimationData().getPreviousAnimationFrame().getOffset() : -1;
            double animationFrameDelta = sceneObject.isAnimated() ? sceneObject.getAnimationData().getAnimationFrameDelta() : -1.0f;
            animationMatricesOffsets.put(animToPass);
            animationMatricesOffsetsPrev.put(animToPassPrev);
            deltaFrames.put((float) animationFrameDelta);

            if (properties != null) {
                this.passPropertiesInBuffer(this.getPipeline(), sceneObject, properties);
            }
        }

        this.passBuffersInSSBO(this.getIndirectSSBO(), indexes, materialIds, modelMatrices, animationMatricesOffsets, animationMatricesOffsetsPrev, deltaFrames);

        if (properties != null) {
            properties.flip();
            ShaderStorageBufferProgram.updateSubDataSSBO(this.getPropertiesSSBO(), 0L, properties);
            MemoryUtil.memFree(properties);
        }
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

    protected void passMatricesInBuffer(Pipeline pipeline, SceneObject sceneObject, FloatBuffer matrices) {
        Matrix4f matrix = TransformUtils.getModelMatrix(sceneObject.getModel().getPose());
        IndirectRenderFabric renderFabric = (IndirectRenderFabric) sceneObject.getRenderFabric(pipeline);
        renderFabric.onFillBufferWithMatrices(pipeline, sceneObject, matrix, matrices, null);
    }

    protected void passPropertiesInBuffer(Pipeline pipeline, SceneObject sceneObject, ByteBuffer properties) {
        IndirectRenderFabric renderFabric = (IndirectRenderFabric) sceneObject.getRenderFabric(pipeline);
        RenderAttributes attributes = sceneObject.getRenderAttributes();
        renderFabric.onFillBufferWithProperties(pipeline, sceneObject, attributes, properties, null);
    }

    public Set<SceneObject> getRejected() {
        return this.rejected;
    }

    public boolean isUsePropertiesSSBO() {
        return this.usePropertiesSSBO;
    }

    public boolean isUseMaterialsSSBO() {
        return this.useMaterialsSSBO;
    }

    public void setIndirectMeshObjects(@NotNull Collection<SceneObject> sceneObjects) {
        this.indirectMeshObjects = sceneObjects;
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public Collection<SceneObject> getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }

    public OpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    @SuppressWarnings("all")
    public Mode getMode() {
        switch (this.getPipeline()) {
            case POINT_LIGHT_SHADOW_MAP:
            case SUN_LIGHT_SHADOW_MAP: {
                return JGemsConfig.SYSTEM.CAST_SHADOWS_FROM_TRANSPARENT_MESHES ? Mode.ALL : Mode.ONLY_SOLID;
            }
            case TRANSPARENCY: {
                return Mode.ONLY_TRANSPARENT;
            }
            default: {
                return Mode.ONLY_SOLID;
            }
        }
    }

    public ShaderStorageBufferObject getIndirectSSBO() {
        return this.indirectSSBO;
    }

    public ShaderStorageBufferObject getPropertiesSSBO() {
        return this.propertiesSSBO;
    }

    public interface IRenderingFunction {
        void func(JGemsShaderManager shaderManager, IndirectCommandsProgram indirectCommandsProgram, IndirectBufferProgram renderBuffer, @NotNull ArbitraryArguments metaData);
        int uniqueFunctionID();
    }

    public record Operator(IRenderingFunction renderingFunction, JGemsShaderManager indirectShader) {
            public Operator(@NotNull IndirectObjectsRenderer.IRenderingFunction renderingFunction, @NotNull JGemsShaderManager indirectShader) {
                this.renderingFunction = renderingFunction;
                this.indirectShader = indirectShader;
            }

        @Override
            public int hashCode() {
                return Objects.hash(this.renderingFunction.uniqueFunctionID(), this.indirectShader);
            }
        }

    public enum Mode {
        ALL,
        ONLY_SOLID,
        ONLY_BLENDED_TRANSPARENT,
        ONLY_TRANSPARENT
    }
}