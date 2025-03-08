package javagems3d.graphics.rendering.scene.renderer.indirect;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.rendering.programs.indirect.commands.BaseIndirectCommandsProgram;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.IndirectCommandsProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class GroupedIndirectRenderer extends IndirectObjectsRenderer {
    public GroupedIndirectRenderer(@NotNull OpenGLRenderer openGLRenderer, @NotNull ShaderStorageBufferObject indirectSSBO, @NotNull ShaderStorageBufferObject propertiesSSBO, @NotNull Pipeline pipeline, boolean usePropertiesSSBO, boolean useMaterialsSSBO) {
        super(openGLRenderer, indirectSSBO, propertiesSSBO, pipeline, usePropertiesSSBO, useMaterialsSSBO);
    }

    public void processAndRender(@Nullable ArbitraryArguments metaData) {
        if (this.getIndirectMeshObjects() == null) {
            return;
        }
        IndirectBufferProgram renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        Map<Operator, Set<SceneObject>> map = this.groupObjects(this.getIndirectMeshObjects(), this.getPipeline());
        for (Map.Entry<Operator, Set<SceneObject>> sceneObjects : map.entrySet()) {
            Operator operator = sceneObjects.getKey();
            IntBuffer indexes = MemoryUtil.memAllocInt(GroupedIndirectRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
            IntBuffer materialIds = MemoryUtil.memAllocInt(GroupedIndirectRenderer.SSBO_DATASETS_MATERIAL_IDS_SIZE);
            IndirectCommandsProgram indirectCommandsProgram = this.createCommands(this.getMode(), indexes, this.isUseMaterialsSSBO() ? materialIds : null, renderBuffer, this.getIndirectMeshObjects());
            this.fillSSBOWithInformation(indexes, materialIds, this.getIndirectMeshObjects());
            this.render(operator, indirectCommandsProgram, renderBuffer, metaData);
            indirectCommandsProgram.destroyBuffer();
        }
    }

    protected IndirectCommandsProgram createCommands(Mode mode, @NotNull IntBuffer indexes, @Nullable IntBuffer materialIds, IndirectBufferProgram renderBuffer, Collection<SceneObject> sceneObjects) {
        BaseIndirectCommandsProgram baseIndirectCommandProgram1 = new BaseIndirectCommandsProgram(renderBuffer);
        baseIndirectCommandProgram1.createBuffer();
        baseIndirectCommandProgram1.buildCommands(indexes, materialIds, sceneObjects, mode);
        return baseIndirectCommandProgram1;
    }

    protected Map<IndirectObjectsRenderer.Operator, Set<SceneObject>> groupObjects(@NotNull Collection<SceneObject> sceneObjects, Pipeline pipeline) {
        return sceneObjects.stream().collect(Collectors.groupingBy(e -> {
            RenderTable.Data renderingData = e.getRenderingTable().getRenderingData(pipeline);
            if (renderingData.getRenderFabric() == null) {
                throw new JGemsNullException("RenderFabric should not be NULL");
            }
            if (!renderingData.getRenderFabric().getRenderingType().equals(Type.INDIRECT)) {
                throw new JGemsRuntimeException("RenderFabric-type should be INDIRECT");
            }
            IndirectRenderFabric renderFabric = (IndirectRenderFabric) renderingData.getRenderFabric();
            return new IndirectObjectsRenderer.Operator(renderFabric.getRenderingFunction(), renderingData.getShaderManager());
        }, HashMap::new, Collectors.toSet()));
    }
}