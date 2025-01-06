package javagems3d.graphics.rendering.scene.renderer.processors.predefined;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.ObjectRenderConfiguration;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
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
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
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
    private FBOTexture2DProgram gBuffer;

    private static final int SSBO_DATASETS_MATRICES_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16;
    private static final int SSBO_DATASETS_ENT_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    private static final int SSBO_DATASETS_MATERIAL_IDS_SIZE = JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS;
    private static final int SSBO_DATASETS_PROPERTIES_SIZE = JGemsGlobalConfiguration.INDIRECT_RENDERING_PROPERTIES_PACK_SIZE * JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_PROPERTIES;

    public IndirectGeometryRenderProcessor(@NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.indirectMeshObjects = new HashSet<>();
    }

    @Override
    public void createResources() {
        this.gBuffer = new FBOTexture2DProgram(true);
        T2DAttachmentContainer gBuffer = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT3, GL46.GL_RGB, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT4, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.gBuffer.createFrameBuffer2DTexture(this.getOpenGLRenderer().getRenderingResolution(), gBuffer, true, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        //JGems3D.get().getResourceManager().getGlobalResources().getResourceCache().clearGroupInCache(MeshBuffer.class);
        //JGemsResourceManager.globalModelAssets.load(JGems3D.get().getResourceManager().getGlobalResources());
        ((JGemsOpenGLRenderer) JGemsHelper.getScreen().getScene().getSceneRenderer()).initSceneIndirectRenderBuffer(JGems3D.get().getResourceManager().getResourceDataCache().getMeshBuffersDataCache()); //DEBUG
    }

    @Override
    public void destroyResources() {
        if (this.getGBuffer() != null) {
            this.getGBuffer().clearFBO();
        }
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        this.getGBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
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
        this.getGBuffer().unBindFBO();
    }

    protected void render(JGemsShaderManager shaderManager, IndirectBufferCommandsBuilder indirectBufferCommandsBuilder, IndirectRenderBuffer renderBuffer) {
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
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(4 * IndirectGeometryRenderProcessor.SSBO_DATASETS_PROPERTIES_SIZE);
        FloatBuffer matrices = MemoryUtil.memAllocFloat(IndirectGeometryRenderProcessor.SSBO_DATASETS_MATRICES_SIZE);
        for (SceneObject sceneObject : sceneObjects) {
            Matrix4f matrix = TransformationUtils.getModelMatrix(sceneObject.getModel().getFormat());
            matrices.put(matrix.get(new float[16]));

            ObjectRenderConfiguration configuration = sceneObject.getObjectRenderConfiguration();
            byteBuffer.putFloat(configuration.getAlphaDiscardValue());
            byteBuffer.putInt(JGemsHelper.RENDERING.getLightingCodeForShader(configuration));
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

    protected Map<JGemsShaderManager, Set<SceneObject>> splitObjectsByShaderGroups(Set<SceneObject> sceneObjects) {
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

    public FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    public Set<SceneObject> getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }
}
