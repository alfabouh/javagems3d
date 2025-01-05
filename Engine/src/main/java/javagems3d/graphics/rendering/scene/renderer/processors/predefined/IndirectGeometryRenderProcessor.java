package javagems3d.graphics.rendering.scene.renderer.processors.predefined;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.ObjectRenderConfiguration;
import javagems3d.graphics.rendering.programs.indirect.IndirectBufferCommandsBuilder;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.scene.buffers.IndirectRenderBuffer;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class IndirectGeometryRenderProcessor extends IRenderProcessor.Template {
    private Set<SceneObject> indirectMeshObjects;

    private static final int SSBO_DATASETS_MATRICES_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16;
    private static final int SSBO_DATASETS_ENT_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    private static final int SSBO_DATASETS_MATERIAL_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;

    public IndirectGeometryRenderProcessor(@NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.indirectMeshObjects = new HashSet<>();
    }

    @Override
    public void createResources() {
        //JGems3D.get().getResourceManager().getGlobalResources().getResourceCache().clearGroupInCache(MeshBuffer.class);
        //JGemsResourceManager.globalModelAssets.load(JGems3D.get().getResourceManager().getGlobalResources());
        ((JGemsOpenGLRenderer) JGemsHelper.getScreen().getScene().getSceneRenderer()).initSceneIndirectRenderBuffer(JGems3D.get().getResourceManager().getResourceDataCache().getMeshBuffersDataCache()); //DEBUG
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
       ((JGemsOpenGLRenderer) JGemsHelper.getScreen().getScene().getSceneRenderer()).loadMeshMaterialsIsSSBO(JGems3D.get().getResourceManager().getResourceDataCache().getBindlessTexturesCache(), JGems3D.get().getResourceManager().getResourceDataCache().getMeshBuffersDataCache());
        IndirectRenderBuffer renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        Map<JGemsShaderManager, Set<SceneObject>> map = this.splitObjectsByShaderGroups(this.getIndirectMeshObjects());

        for (Map.Entry<JGemsShaderManager, Set<SceneObject>> sceneObjects : map.entrySet()) {
            IndirectBufferCommandsBuilder indirectBufferCommandsBuilder1 = new IndirectBufferCommandsBuilder(renderBuffer);
            indirectBufferCommandsBuilder1.createBuffer();

            IntBuffer indexes = MemoryUtil.memAllocInt(IndirectGeometryRenderProcessor.SSBO_DATASETS_ENT_IDS_SIZE);
            IntBuffer materialIds = MemoryUtil.memAllocInt(IndirectGeometryRenderProcessor.SSBO_DATASETS_MATERIAL_IDS_SIZE);

            indirectBufferCommandsBuilder1.buildCommands(indexes, materialIds, sceneObjects.getValue());
            this.fillSSBOWithInformation(indexes, materialIds, sceneObjects.getValue(), JGemsResourceManager.globalShaderAssets.IndirectBufferData, JGemsResourceManager.globalShaderAssets.PropertiesData);

            this.render(sceneObjects.getKey(), indirectBufferCommandsBuilder1, renderBuffer);
            indirectBufferCommandsBuilder1.destroyBuffer();
        }
    }

    private void render(JGemsShaderManager shaderManager, IndirectBufferCommandsBuilder indirectBufferCommandsBuilder, IndirectRenderBuffer renderBuffer) {
        shaderManager.beginShading();
        shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(this.getOpenGLRenderer().getTransformationManager().getPerspectiveMatrix()));
        shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(this.getOpenGLRenderer().getTransformationManager().getMainCameraViewMatrix()));
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, indirectBufferCommandsBuilder.getRenderBufferHandle());
        GL46.glBindVertexArray(renderBuffer.getStaticVao());
        GL46.glMultiDrawElementsIndirect(GL46.GL_TRIANGLES, GL46.GL_UNSIGNED_INT, 0, indirectBufferCommandsBuilder.getDrawCount(), 0);
        GL46.glBindVertexArray(0);
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, 0);
        shaderManager.endShading();
    }

    private void fillSSBOWithInformation(IntBuffer indexes, IntBuffer materialIds, Set<SceneObject> sceneObjects, ShaderStorageBufferObject indirectBufferData, ShaderStorageBufferObject objectProperties) {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(Float.BYTES * JGemsGlobalConfiguration.INDIRECT_RENDERING_PROPERTIES_PACK_SIZE * JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS);
        FloatBuffer matrices = MemoryUtil.memAllocFloat(IndirectGeometryRenderProcessor.SSBO_DATASETS_MATRICES_SIZE);
        for (SceneObject sceneObject : sceneObjects) {
            Matrix4f matrix = TransformationUtils.getModelMatrix(sceneObject.getModel().getFormat());
            matrices.put(matrix.get(new float[16]));

            ObjectRenderConfiguration configuration = sceneObject.getObjectRenderConfiguration();
           byteBuffer.putFloat(configuration.getAlphaDiscardValue());
           byteBuffer.putInt(1);
           byteBuffer.putInt(1);
        }
        matrices.flip();
        byteBuffer.flip();

        ShaderStorageBufferProgram.fillSSBOWithData(indirectBufferData, 0L, matrices);
        ShaderStorageBufferProgram.fillSSBOWithData(indirectBufferData, (long) (IndirectGeometryRenderProcessor.SSBO_DATASETS_MATRICES_SIZE) * Float.BYTES, indexes);
        ShaderStorageBufferProgram.fillSSBOWithData(indirectBufferData, (long) IndirectGeometryRenderProcessor.SSBO_DATASETS_MATRICES_SIZE * Float.BYTES + (long) IndirectGeometryRenderProcessor.SSBO_DATASETS_ENT_IDS_SIZE * Integer.BYTES, materialIds);

        ShaderStorageBufferProgram.fillSSBOWithData(objectProperties, 0L, byteBuffer);

        MemoryUtil.memFree(matrices);
        MemoryUtil.memFree(indexes);
        MemoryUtil.memFree(byteBuffer);
        MemoryUtil.memFree(materialIds);
    }

    private Map<JGemsShaderManager, Set<SceneObject>> splitObjectsByShaderGroups(Set<SceneObject> sceneObjects) {
        Map<JGemsShaderManager, Set<SceneObject>> map = new HashMap<>();
        for (SceneObject sceneObject : sceneObjects) {
            JGemsShaderManager shaderManager = sceneObject.getObjectRenderConfiguration().getModelRenderShader();
            JGemsHelper.UTILS.putObjectInMapOrUpdate(map, shaderManager, new HashSet<SceneObject>() {{ add(sceneObject); }}, (ex, nw) ->
            {
                ex.add(nw);
                return ex;
            }, sceneObject);
        }
        return map;
    }

    public void setIndirectMeshObjects(@NotNull Set<SceneObject> sceneObjects) {
        this.indirectMeshObjects = sceneObjects;
    }

    public Set<SceneObject> getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }
}
