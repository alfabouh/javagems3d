package javagems3d.graphics.rendering.scene.renderer.indirect;

import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
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
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public abstract class IndirectObjectsRenderer {
    protected static final int SSBO_DATASETS_MATRICES_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16;
    protected static final int SSBO_DATASETS_ENT_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    protected static final int SSBO_DATASETS_MATERIAL_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    protected static final int SSBO_DATASETS_PROPERTIES_SIZE = JGemsGlobalConfiguration.INDIRECT_RENDERING_PROPERTIES_PACK_SIZE * JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_PROPERTIES;

    private final Pipeline pipeline;

    private final Set<SceneObject> rejected;
    protected Collection<SceneObject> indirectMeshObjects;
    private final OpenGLRenderer openGLRenderer;

    protected final boolean usePropertiesSSBO;
    protected final boolean useMaterialsSSBO;

    public IndirectObjectsRenderer(@NotNull OpenGLRenderer openGLRenderer, @NotNull Pipeline pipeline, boolean usePropertiesSSBO, boolean useMaterialsSSBO) {
        this.pipeline = pipeline;
        this.openGLRenderer = openGLRenderer;
        this.usePropertiesSSBO = usePropertiesSSBO;
        this.useMaterialsSSBO = useMaterialsSSBO;

        this.rejected = new HashSet<>();
    }

    protected abstract void processAndRender(@Nullable ArbitraryArguments metaData);
    protected abstract IndirectCommandsProgram createCommands(Mode mode, IntBuffer indexes, IntBuffer materialIds, IndirectBufferProgram renderBuffer, Collection<SceneObject> sceneObjects);

    protected void render(Operator operator, IndirectCommandsProgram indirectCommandsProgram, IndirectBufferProgram renderBuffer, @Nullable ArbitraryArguments metaData) {
        operator.getRenderingFunction().func(operator.getIndirectShader(), indirectCommandsProgram, renderBuffer, metaData == null ? ArbitraryArguments.empty() : metaData);
    }

    protected void fillSSBOWithInformation(IntBuffer indexes, IntBuffer materialIds, Collection<SceneObject> sceneObjects, ShaderStorageBufferObject indirectBufferData, ShaderStorageBufferObject objectProperties) {
        ByteBuffer properties = this.isUsePropertiesSSBO() ? MemoryUtil.memAlloc(4 * IndirectObjectsRenderer.SSBO_DATASETS_PROPERTIES_SIZE) : null;
        FloatBuffer matrices = MemoryUtil.memAllocFloat(IndirectObjectsRenderer.SSBO_DATASETS_MATRICES_SIZE);

        this.getRejected().clear();
        for (SceneObject sceneObject : sceneObjects) {
            if (this.getPipeline().equals(Pipeline.SCENE) && sceneObject.getModel().getMeshStructure().hasTransparency()) {
                this.getRejected().add(sceneObject);
            }
            this.passMatricesInBuffer(this.getPipeline(), sceneObject, matrices);
            if (properties != null) {
                this.passPropertiesInBuffer(this.getPipeline(), sceneObject, properties);
            }
        }

        matrices.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(indirectBufferData, 0L, matrices);
        MemoryUtil.memFree(matrices);

        indexes.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(indirectBufferData, (long) (IndirectObjectsRenderer.SSBO_DATASETS_MATRICES_SIZE) * Float.BYTES, indexes);
        MemoryUtil.memFree(indexes);

        if (materialIds != null) {
            materialIds.flip();
            ShaderStorageBufferProgram.updateSubDataSSBO(indirectBufferData, (long) IndirectObjectsRenderer.SSBO_DATASETS_MATRICES_SIZE * Float.BYTES + (long) IndirectObjectsRenderer.SSBO_DATASETS_ENT_IDS_SIZE * Integer.BYTES, materialIds);
            MemoryUtil.memFree(materialIds);
        }

        if (properties != null) {
            properties.flip();
            ShaderStorageBufferProgram.updateSubDataSSBO(objectProperties, 0L, properties);
            MemoryUtil.memFree(properties);
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
                return JGemsRenderingGlobalConstants.CAST_SHADOWS_FROM_TRANSPARENT_MESHES ? Mode.ALL : Mode.ONLY_SOLID;
            }
            case TRANSPARENCY: {
                return Mode.ONLY_TRANSPARENT;
            }
            default: {
                return Mode.ONLY_SOLID;
            }
        }
    }

    public interface IRenderingFunction {
        void func(JGemsShaderManager shaderManager, IndirectCommandsProgram indirectCommandsProgram, IndirectBufferProgram renderBuffer, @NotNull ArbitraryArguments metaData);
        int uniqueFunctionID();
    }

    public static class Operator {
        private final IRenderingFunction renderingFunction;
        private final JGemsShaderManager indirectShader;

        public Operator(@NotNull IndirectObjectsRenderer.IRenderingFunction overRenderingFunction, @NotNull JGemsShaderManager indirectShader) {
            this.renderingFunction = overRenderingFunction;
            this.indirectShader = indirectShader;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Operator operator = (Operator) o;
            return Objects.equals(this.renderingFunction, operator.renderingFunction) && Objects.equals(this.indirectShader, operator.indirectShader);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.renderingFunction.uniqueFunctionID(), this.indirectShader);
        }

        public IRenderingFunction getRenderingFunction() {
            return this.renderingFunction;
        }

        public JGemsShaderManager getIndirectShader() {
            return this.indirectShader;
        }
    }

    public enum Mode {
        ALL,
        ONLY_SOLID,
        ONLY_TRANSPARENT
    }
}