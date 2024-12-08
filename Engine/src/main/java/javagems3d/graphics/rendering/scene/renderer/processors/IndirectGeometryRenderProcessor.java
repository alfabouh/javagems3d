package javagems3d.graphics.rendering.scene.renderer.processors;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.fabric.args.ArbitraryArguments;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.indirect.IndirectBufferCommandsBuilder;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.buffers.IndirectRenderBuffer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IndirectGeometryRenderProcessor extends IRenderProcessor.Template {
    private FBOTexture2DProgram gBuffer;
    private Set<SceneObject> indirectMeshObjects;

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
        this.gBuffer.createFrameBuffer2DTexture(this.getWindowSize(), gBuffer, true, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
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
        IndirectRenderBuffer renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();

        Map<JGemsShaderManager, Set<SceneObject>> map = this.splitObjectsByShaderGroups(this.getIndirectMeshObjects());
        for (Map.Entry<JGemsShaderManager, Set<SceneObject>> sceneObjects : map.entrySet()) {
            IndirectBufferCommandsBuilder indirectBufferCommandsBuilder1 = new IndirectBufferCommandsBuilder(renderBuffer);
            indirectBufferCommandsBuilder1.createBuffer();
            indirectBufferCommandsBuilder1.buildCommands(this.splitMeshes(sceneObjects.getValue()));
            this.render(sceneObjects.getKey(), indirectBufferCommandsBuilder1, renderBuffer, sceneObjects.getValue());
            indirectBufferCommandsBuilder1.destroyBuffer();
        }
        this.getGBuffer().unBindFBO();
    }

    private void render(JGemsShaderManager shaderManager, IndirectBufferCommandsBuilder indirectBufferCommandsBuilder, IndirectRenderBuffer renderBuffer, Set<SceneObject> sceneObjects) {
        shaderManager.beginShading();
        shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(this.getOpenGLRenderer().getTransformationManager().getPerspectiveMatrix()));
        shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(this.getOpenGLRenderer().getTransformationManager().getMainCameraViewMatrix()));

        int entityIdx = 0;
        for (SceneObject a : sceneObjects) {
            shaderManager.performUniform(new UniformString("modelMatrices", entityIdx++), UniformFunctions.MAT4F(TransformationUtils.getModelMatrix(a.getModel().getFormat())));
        }

        int drawElement = 0;
        for (SceneObject a : sceneObjects) {
            shaderManager.performUniform(new UniformString("modelMatrixIdx", entityIdx++), UniformFunctions.INTEGER(drawElement++));
        }

        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, indirectBufferCommandsBuilder.getRenderBufferHandle());
        GL46.glBindVertexArray(renderBuffer.getStaticVao());
        GL46.glMultiDrawElementsIndirect(GL46.GL_TRIANGLES, GL46.GL_UNSIGNED_INT, 0, indirectBufferCommandsBuilder.getDrawCount(), 0);
        GL46.glBindVertexArray(0);
        shaderManager.endShading();
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

    private Map<MeshBuffer, Integer> splitMeshes(Set<SceneObject> sceneObjects) {
        Map<MeshBuffer, Integer> splitOnGroups = new HashMap<>();
        for (SceneObject sceneObject : sceneObjects) {
            MeshBuffer meshBuffer = sceneObject.getModel().getMeshStructureWithUnSafeCast();
            JGemsHelper.UTILS.putObjectInMapOrUpdate(splitOnGroups, meshBuffer, 1, Integer::sum, 1);
        }
        return splitOnGroups;
    }

    public void setIndirectMeshObjects(@NotNull Set<SceneObject> sceneObjects) {
        this.indirectMeshObjects = sceneObjects;
    }

    public Set<SceneObject> getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }

    public FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }
}
