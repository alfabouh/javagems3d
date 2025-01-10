package javagems3d.graphics.rendering.scene.renderer.indirect;

import javagems3d.JGemsHelper;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.configuration.ShadingTable;
import javagems3d.graphics.rendering.programs.indirect.IndirectBufferCommandsBuilder;
import javagems3d.graphics.rendering.programs.indirect.IndirectRenderBufferProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IndirectObjectsRenderer {
    private static final int SSBO_DATASETS_MATRICES_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16;
    private static final int SSBO_DATASETS_ENT_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    private static final int SSBO_DATASETS_MATERIAL_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    private static final int SSBO_DATASETS_PROPERTIES_SIZE = JGemsGlobalConfiguration.INDIRECT_RENDERING_PROPERTIES_PACK_SIZE * JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_PROPERTIES;

    private Set<SceneObject> indirectMeshObjects;
    private final OpenGLRenderer openGLRenderer;

    private final boolean usePropertiesSSBO;
    private final boolean useMaterialsSSBO;
    private final JGemsShaderManager overIndirectShader;
    private final ShaderSplittingPicker defaultShaderSplittingPicker;

    public IndirectObjectsRenderer(@NotNull OpenGLRenderer openGLRenderer, @Nullable IndirectObjectsRenderer.ShaderSplittingPicker defaultShaderSplittingPicker, @Nullable JGemsShaderManager overIndirectShader, boolean usePropertiesSSBO, boolean useMaterialsSSBO) {
        this.openGLRenderer = openGLRenderer;
        this.usePropertiesSSBO = usePropertiesSSBO;
        this.useMaterialsSSBO = useMaterialsSSBO;
        this.overIndirectShader = overIndirectShader;
        this.defaultShaderSplittingPicker = defaultShaderSplittingPicker == null ? (e) -> e.getRenderAttributes().getShadingTable().getShader(ShadingTable.Category.SCENE) : defaultShaderSplittingPicker;
    }

    public void processAndRender() {
        this.processAndRender(null);
    }

    public void processAndRender(@Nullable IndirectObjectsRenderer.ShaderSplittingPicker shaderSplittingPicker) {
        IndirectRenderBufferProgram renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        if (this.getOverIndirectShader() != null) {
            IndirectBufferCommandsBuilder indirectBufferCommandsBuilder1 = new IndirectBufferCommandsBuilder(renderBuffer);
            indirectBufferCommandsBuilder1.createBuffer();

            IntBuffer indexes = MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
            IntBuffer materialIds = MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_MATERIAL_IDS_SIZE);
            indirectBufferCommandsBuilder1.buildCommands(indexes, materialIds, this.getIndirectMeshObjects());

            this.fillSSBOWithInformation(indexes, materialIds, this.getIndirectMeshObjects(), JGemsResourceManager.globalShaderAssets.IndirectBufferData, JGemsResourceManager.globalShaderAssets.PropertiesData);
            this.render(this.getOverIndirectShader(), indirectBufferCommandsBuilder1, renderBuffer);

            indirectBufferCommandsBuilder1.destroyBuffer();
        } else {
            Map<JGemsShaderManager, Set<SceneObject>> map = this.splitObjectsByShaderGroups(this.getIndirectMeshObjects(), shaderSplittingPicker != null ? shaderSplittingPicker : this.getDefaultShaderSplitting());
            for (Map.Entry<JGemsShaderManager, Set<SceneObject>> sceneObjects : map.entrySet()) {
                IndirectBufferCommandsBuilder indirectBufferCommandsBuilder1 = new IndirectBufferCommandsBuilder(renderBuffer);
                indirectBufferCommandsBuilder1.createBuffer();

                IntBuffer indexes = MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
                IntBuffer materialIds = this.isUseMaterialsSSBO() ? MemoryUtil.memAllocInt(IndirectObjectsRenderer.SSBO_DATASETS_MATERIAL_IDS_SIZE) : null;
                indirectBufferCommandsBuilder1.buildCommands(indexes, materialIds, sceneObjects.getValue());

                this.fillSSBOWithInformation(indexes, materialIds, sceneObjects.getValue(), JGemsResourceManager.globalShaderAssets.IndirectBufferData, JGemsResourceManager.globalShaderAssets.PropertiesData);
                this.render(sceneObjects.getKey(), indirectBufferCommandsBuilder1, renderBuffer);

                indirectBufferCommandsBuilder1.destroyBuffer();
            }
        }
    }

    protected void render(JGemsShaderManager shaderManager, IndirectBufferCommandsBuilder indirectBufferCommandsBuilder, IndirectRenderBufferProgram renderBuffer) {
        shaderManager.beginShading();
        CubeMapTexture cubeMapProgram = JGemsHelper.ENVIRONMENT.getWorldEnvironment().getSkyBox().getSky2DTexture();
        shaderManager.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(JGemsHelper.CAMERA.getCurrentCamera().getCamPosition()));
        if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString("ambient_cube_map"))) {
            shaderManager.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram.getTextureId(), GL46.GL_TEXTURE_CUBE_MAP);
        }
        shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(this.getOpenGLRenderer().getTransformationManager().getPerspectiveMatrix()));
        shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(this.getOpenGLRenderer().getTransformationManager().getMainCameraViewMatrix()));
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, indirectBufferCommandsBuilder.getRenderBufferHandle());
        GL46.glBindVertexArray(renderBuffer.getStaticVao());
        GL46.glMultiDrawElementsIndirect(GL46.GL_TRIANGLES, GL46.GL_UNSIGNED_INT, 0, indirectBufferCommandsBuilder.getDrawCount(), 0);
        GL46.glBindVertexArray(0);
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, 0);
        shaderManager.endShading();
    }

    protected void fillSSBOWithInformation(IntBuffer indexes, IntBuffer materialIds, Set<SceneObject> sceneObjects, ShaderStorageBufferObject indirectBufferData, ShaderStorageBufferObject objectProperties) {
        ByteBuffer byteBuffer = this.isUsePropertiesSSBO() ? MemoryUtil.memAlloc(4 * IndirectObjectsRenderer.SSBO_DATASETS_PROPERTIES_SIZE) : null;
        FloatBuffer matrices = MemoryUtil.memAllocFloat(IndirectObjectsRenderer.SSBO_DATASETS_MATRICES_SIZE);

        for (SceneObject sceneObject : sceneObjects) {
            Matrix4f matrix = TransformationUtils.getModelMatrix(sceneObject.getModel().getFormat());
            matrices.put(matrix.get(new float[16]));
            if (byteBuffer != null) {
                RenderAttributes configuration = sceneObject.getRenderAttributes();
                byteBuffer.putFloat(configuration.getAlphaDiscardValue());
                byteBuffer.putInt(JGemsHelper.RENDERING.getLightingCodeForShader(configuration));
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

        if (byteBuffer != null) {
            byteBuffer.flip();
            ShaderStorageBufferProgram.fillSSBOWithData(objectProperties, 0L, byteBuffer);
            MemoryUtil.memFree(byteBuffer);
        }
    }

    protected Map<JGemsShaderManager, Set<SceneObject>> splitObjectsByShaderGroups(Set<SceneObject> sceneObjects, ShaderSplittingPicker shaderSplittingPicker) {
        return sceneObjects.stream().collect(Collectors.groupingBy(shaderSplittingPicker::pickShaderGroup, HashMap::new, Collectors.toSet()));
    }

    public ShaderSplittingPicker getDefaultShaderSplitting() {
        return this.defaultShaderSplittingPicker;
    }

    public JGemsShaderManager getOverIndirectShader() {
        return this.overIndirectShader;
    }

    public boolean isUsePropertiesSSBO() {
        return this.usePropertiesSSBO;
    }

    public boolean isUseMaterialsSSBO() {
        return this.useMaterialsSSBO;
    }

    public void setIndirectMeshObjects(@NotNull Set<SceneObject> sceneObjects) {
        this.indirectMeshObjects = sceneObjects;
    }

    public Set<SceneObject> getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }

    public OpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    @FunctionalInterface
    public interface ShaderSplittingPicker {
        @NotNull JGemsShaderManager pickShaderGroup(SceneObject sceneObject);
    }
}