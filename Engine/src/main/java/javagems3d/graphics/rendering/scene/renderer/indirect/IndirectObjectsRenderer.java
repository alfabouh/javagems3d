package javagems3d.graphics.rendering.scene.renderer.indirect;

import javagems3d.JGemsHelper;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.rendering.programs.indirect.IndirectBufferCommandsBuilder;
import javagems3d.graphics.rendering.programs.indirect.IndirectRenderBufferProgram;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class IndirectObjectsRenderer {
    private static final int SSBO_DATASETS_MATRICES_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16;
    private static final int SSBO_DATASETS_ENT_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    private static final int SSBO_DATASETS_MATERIAL_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    private static final int SSBO_DATASETS_PROPERTIES_SIZE = JGemsGlobalConfiguration.INDIRECT_RENDERING_PROPERTIES_PACK_SIZE * JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_PROPERTIES;

    private Collection<SceneObject> indirectMeshObjects;
    private final OpenGLRenderer openGLRenderer;

    private final boolean usePropertiesSSBO;
    private final boolean useMaterialsSSBO;
    private final Operator overlappingOperator;

    public IndirectObjectsRenderer(@NotNull OpenGLRenderer openGLRenderer, @Nullable Operator overlappingOperator, boolean usePropertiesSSBO, boolean useMaterialsSSBO) {
        this.openGLRenderer = openGLRenderer;
        this.usePropertiesSSBO = usePropertiesSSBO;
        this.useMaterialsSSBO = useMaterialsSSBO;
        this.overlappingOperator = overlappingOperator;
    }

    public void processAndRender(@NotNull Pipeline pipeline, @Nullable ArbitraryArguments metaData) {
        IndirectRenderBufferProgram renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        if (this.getOverlappingOperator() != null) {
            IndirectBufferCommandsBuilder indirectBufferCommandsBuilder1 = new IndirectBufferCommandsBuilder(renderBuffer);
            indirectBufferCommandsBuilder1.createBuffer();

            IntBuffer indexes = MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
            IntBuffer materialIds = MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_MATERIAL_IDS_SIZE);
            indirectBufferCommandsBuilder1.buildCommands(indexes, materialIds, this.getIndirectMeshObjects());

            this.fillSSBOWithInformation(indexes, materialIds, this.getIndirectMeshObjects(), JGemsResourceManager.globalShaderAssets.IndirectBufferData, JGemsResourceManager.globalShaderAssets.PropertiesData);
            this.render(this.getOverlappingOperator(), indirectBufferCommandsBuilder1, renderBuffer, metaData);

            indirectBufferCommandsBuilder1.destroyBuffer();
        } else {
            Map<Operator, Set<SceneObject>> map = this.groupObjects(this.getIndirectMeshObjects(), pipeline);
            for (Map.Entry<Operator, Set<SceneObject>> sceneObjects : map.entrySet()) {
                Operator operator = sceneObjects.getKey();

                IndirectBufferCommandsBuilder indirectBufferCommandsBuilder1 = new IndirectBufferCommandsBuilder(renderBuffer);
                indirectBufferCommandsBuilder1.createBuffer();

                IntBuffer indexes = MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
                IntBuffer materialIds = this.isUseMaterialsSSBO() ? MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_MATERIAL_IDS_SIZE) : null;
                indirectBufferCommandsBuilder1.buildCommands(indexes, materialIds, sceneObjects.getValue());

                this.fillSSBOWithInformation(indexes, materialIds, sceneObjects.getValue(), JGemsResourceManager.globalShaderAssets.IndirectBufferData, JGemsResourceManager.globalShaderAssets.PropertiesData);
                this.render(operator, indirectBufferCommandsBuilder1, renderBuffer, metaData);

                indirectBufferCommandsBuilder1.destroyBuffer();
            }
        }
    }

    protected void render(Operator operator, IndirectBufferCommandsBuilder indirectBufferCommandsBuilder, IndirectRenderBufferProgram renderBuffer, @Nullable ArbitraryArguments arbitraryArguments) {
        if (arbitraryArguments == null) {
            arbitraryArguments = ArbitraryArguments.empty();
        }
        RenderingFunction renderingFunction = this.getOverlappingOperator() != null ? this.getOverlappingOperator().getRenderingFunction() : operator.getRenderingFunction();
        renderingFunction.func(operator.getIndirectShader(), indirectBufferCommandsBuilder, renderBuffer, arbitraryArguments);
    }

    protected void fillSSBOWithInformation(IntBuffer indexes, IntBuffer materialIds, Collection<SceneObject> sceneObjects, ShaderStorageBufferObject indirectBufferData, ShaderStorageBufferObject objectProperties) {
        ByteBuffer properties = this.isUsePropertiesSSBO() ? MemoryUtil.memAlloc(4 * IndirectObjectsRenderer.SSBO_DATASETS_PROPERTIES_SIZE) : null;
        FloatBuffer matrices = MemoryUtil.memAllocFloat(IndirectObjectsRenderer.SSBO_DATASETS_MATRICES_SIZE);

        for (SceneObject sceneObject : sceneObjects) {
            Matrix4f matrix = TransformationUtils.getModelMatrix(sceneObject.getModel().getFormat());
            IndirectRenderFabric renderFabric = (IndirectRenderFabric) sceneObject.getRenderFabric(Pipeline.SCENE);
            renderFabric.onFillBufferWithMatrices(Pipeline.SCENE, sceneObject, matrix, matrices, null);
            if (properties != null) {
                RenderAttributes attributes = sceneObject.getRenderAttributes();
                renderFabric.onFillBufferWithProperties(Pipeline.SCENE, sceneObject, attributes, properties, null);
            }
        }

        matrices.flip();
        ShaderStorageBufferProgram.fillSSBOWithData(indirectBufferData, 0L, matrices);
        MemoryUtil.memFree(matrices);

        indexes.flip();
        ShaderStorageBufferProgram.fillSSBOWithData(indirectBufferData, (long) (IndirectObjectsRenderer.SSBO_DATASETS_MATRICES_SIZE) * Float.BYTES, indexes);
        MemoryUtil.memFree(indexes);

        if (materialIds != null) {
            materialIds.flip();
            ShaderStorageBufferProgram.fillSSBOWithData(indirectBufferData, (long) IndirectObjectsRenderer.SSBO_DATASETS_MATRICES_SIZE * Float.BYTES + (long) IndirectObjectsRenderer.SSBO_DATASETS_ENT_IDS_SIZE * Integer.BYTES, materialIds);
            MemoryUtil.memFree(materialIds);
        }

        if (properties != null) {
            properties.flip();
            ShaderStorageBufferProgram.fillSSBOWithData(objectProperties, 0L, properties);
            MemoryUtil.memFree(properties);
        }
    }

    protected Map<Operator, Set<SceneObject>> groupObjects(Collection<SceneObject> sceneObjects, Pipeline pipeline) {
        return sceneObjects.stream().collect(Collectors.groupingBy(e -> {
            RenderTable.Data renderingData = e.getRenderingTable().getRenderingData(pipeline);
            if (renderingData.getRenderFabric() == null) {
                throw new JGemsNullException("RenderFabric should not be NULL!");
            }
            if (!renderingData.getRenderFabric().getRenderingType().equals(Type.INDIRECT)) {
                throw new JGemsRuntimeException("RenderFabric-type should be INDIRECT!");
            }
            IndirectRenderFabric renderFabric = (IndirectRenderFabric) renderingData.getRenderFabric();
            return new Operator(renderFabric.getRenderingFunction(), renderingData.getShaderManager());
        }, HashMap::new, Collectors.toSet()));
    }

    public Operator getOverlappingOperator() {
        return this.overlappingOperator;
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

    public Collection<SceneObject> getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }

    public OpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    public interface RenderingFunction {
        void func(JGemsShaderManager shaderManager, IndirectBufferCommandsBuilder indirectBufferCommandsBuilder, IndirectRenderBufferProgram renderBuffer, @NotNull ArbitraryArguments metaData);
        int uniqueFunctionID();
    }

    public static class Operator {
        private final RenderingFunction renderingFunction;
        private final JGemsShaderManager indirectShader;

        public Operator(@NotNull RenderingFunction overRenderingFunction, @NotNull JGemsShaderManager indirectShader) {
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
            return Objects.hash(this.renderingFunction, this.indirectShader);
        }

        public RenderingFunction getRenderingFunction() {
            return this.renderingFunction;
        }

        public JGemsShaderManager getIndirectShader() {
            return this.indirectShader;
        }
    }
}