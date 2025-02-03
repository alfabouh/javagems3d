package javagems3d.graphics.rendering.scene.renderer.indirect;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.BaseIndirectCommandsProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.IndirectCommandsProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Collection;

public class SinglePassIndirectRenderer extends IndirectObjectsRenderer {
    private final Operator operator;

    public SinglePassIndirectRenderer(@NotNull OpenGLRenderer openGLRenderer, @NotNull Operator operator, @NotNull Pipeline pipeline, boolean usePropertiesSSBO, boolean useMaterialsSSBO) {
        super(openGLRenderer, pipeline, usePropertiesSSBO, useMaterialsSSBO);
        this.operator = operator;
    }

    public void processAndRender(@Nullable ArbitraryArguments metaData) {
        if (this.getIndirectMeshObjects() == null) {
            return;
        }
        IndirectBufferProgram renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        IntBuffer indexes = MemoryUtil.memAllocInt(SinglePassIndirectRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
        IntBuffer materialIds = MemoryUtil.memAllocInt(SinglePassIndirectRenderer.SSBO_DATASETS_MATERIAL_IDS_SIZE);
        IndirectCommandsProgram indirectCommandsProgram = this.createCommands(indexes, materialIds, renderBuffer, this.getIndirectMeshObjects());
        this.fillSSBOWithInformation(indexes, materialIds, this.getIndirectMeshObjects(), JGemsResourceManager.globalShaderAssets.IndirectBufferData, JGemsResourceManager.globalShaderAssets.PropertiesData);
        this.render(this.getOperator(), indirectCommandsProgram, renderBuffer, metaData);
        indirectCommandsProgram.destroyBuffer();
    }

    protected IndirectCommandsProgram createCommands(IntBuffer indexes, IntBuffer materialIds, IndirectBufferProgram renderBuffer, Collection<SceneObject> sceneObjects) {
        BaseIndirectCommandsProgram baseIndirectCommandProgram1 = new BaseIndirectCommandsProgram(renderBuffer);
        baseIndirectCommandProgram1.createBuffer();
        baseIndirectCommandProgram1.buildCommands(indexes, materialIds, sceneObjects, this.isTransparency());
        return baseIndirectCommandProgram1;
    }

    public @NotNull Operator getOperator() {
        return this.operator;
    }
}