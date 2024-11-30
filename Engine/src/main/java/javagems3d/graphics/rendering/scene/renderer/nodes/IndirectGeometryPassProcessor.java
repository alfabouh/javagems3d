package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.AbstractSceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.indirect.IndirectBufferCommandsBuilder;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.AbstractRenderProcessor;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IndirectGeometryPassProcessor extends AbstractRenderProcessor {
    private FBOTexture2DProgram gBuffer;
    private Set<AbstractSceneObject> indirectMeshObjects;

    public IndirectGeometryPassProcessor(int renderOrder, @NotNull OpenGLRenderer openGLRenderer) {
        super(renderOrder, openGLRenderer);
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
        this.getGBuffer().clearFBO();
    }

    @Override
    public void onRenderNode(@Nullable FBOTexture2DProgram fboIn) {
        this.getGBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

        Map<JGemsShaderManager, Set<AbstractSceneObject>> map = this.splitObjectsByShaderGroups(this.getIndirectMeshObjects());
        for (Set<AbstractSceneObject> abstractSceneObjects : map.values()) {
            IndirectBufferCommandsBuilder indirectBufferCommandsBuilder1 = new IndirectBufferCommandsBuilder(this.getOpenGLRenderer().getSceneData().getSceneIndirectRenderBuffer());
            indirectBufferCommandsBuilder1.createBuffer();
            indirectBufferCommandsBuilder1.buildCommands(this.splitMeshes(abstractSceneObjects));
            for (AbstractSceneObject abstractSceneObject : abstractSceneObjects) {
                abstractSceneObject.getRenderFabric().onRender(this.getOpenGLRenderer(), abstractSceneObject, indirectBufferCommandsBuilder1);
            }
            indirectBufferCommandsBuilder1.destroyBuffer();
        }

        this.getGBuffer().unBindFBO();
    }

    private Map<JGemsShaderManager, Set<AbstractSceneObject>> splitObjectsByShaderGroups(Set<AbstractSceneObject> sceneObjects) {
        Map<JGemsShaderManager, Set<AbstractSceneObject>> map = new HashMap<>();
        for (AbstractSceneObject abstractSceneObject : sceneObjects) {
            JGemsShaderManager shaderManager = abstractSceneObject.getObjectRenderConfiguration().getModelRenderShader();
            JGemsHelper.UTILS.putObjectInMapOrUpdate(map, shaderManager, new HashSet<>(), (ex, nw) ->
            {
                ex.add(nw);
                return ex;
            }, abstractSceneObject);
        }
        return map;
    }

    private Map<MeshBuffer, Integer> splitMeshes(Set<AbstractSceneObject> abstractSceneObjects) {
        Map<MeshBuffer, Integer> splitOnGroups = new HashMap<>();
        for (AbstractSceneObject abstractSceneObject : abstractSceneObjects) {
            MeshBuffer meshBuffer = abstractSceneObject.getModel().getMeshStructureWithUnSafeCast();
            JGemsHelper.UTILS.putObjectInMapOrUpdate(splitOnGroups, meshBuffer, 0, Integer::sum, 1);
        }
        return splitOnGroups;
    }

    public Set<AbstractSceneObject> getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }

    public FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    @Override
    public @Nullable FBOTexture2DProgram outFrameBuffer() {
        return this.getGBuffer();
    }
}
