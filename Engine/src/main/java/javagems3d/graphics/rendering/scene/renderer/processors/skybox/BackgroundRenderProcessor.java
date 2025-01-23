package javagems3d.graphics.rendering.scene.renderer.processors.skybox;

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.background.SceneBackgroundProp;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IDeferredRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.IndirectGeometryRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BackgroundRenderProcessor extends IRenderProcessor.Template {
    private final IDeferredRenderNode deferredRenderNode;
    private final SkyBox skyBox;
    private DirectGeometryRenderProcessor directGeometryRenderProcessor;
    private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
    private FBOTexture2DProgram background;

    public BackgroundRenderProcessor(@NotNull SkyBox skyBox, @NotNull IDeferredRenderNode deferredRenderNode, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.skyBox = skyBox;
        this.deferredRenderNode = deferredRenderNode;
    }

    @Override
    public void createResources() {
        this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(Pipeline.SCENE, this.getOpenGLRenderer());
        this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(Pipeline.SCENE, this.getOpenGLRenderer());

        this.getDirectGeometryRenderProcessor().createResources();
        this.getIndirectGeometryRenderProcessor().createResources();

        this.background = new FBOTexture2DProgram(true);
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
        }};
        this.background.createFrameBuffer2DTexture(this.getRenderingResolution(), clr, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        this.getDirectGeometryRenderProcessor().destroyResources();
        this.getIndirectGeometryRenderProcessor().destroyResources();

        if (this.getBackground() != null) {
            this.getBackground().clearFBO();
        }
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.renderBackground(frameTicking);
    }

    protected void renderBackground(FrameTicking frameTicking) {
        Set<SceneBackgroundProp> toRender = this.getSkyBox().getBackground().getToRenderSet();
        Pair<List<SceneObject>, List<SceneObject>> groups = this.divideSet2Groups(toRender);
        List<SceneObject> directRenderObjects = groups.getFirst();
        List<SceneObject> indirectRenderObjects = groups.getSecond();

        this.getBackground().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.renderIndirectObjects(frameTicking, indirectRenderObjects);
        this.renderDirectObjects(frameTicking, directRenderObjects);
        this.getBackground().unBindFBO();
    }

    protected void renderDirectObjects(FrameTicking frameTicking, List<SceneObject> objects) {
        this.getDirectGeometryRenderProcessor().setDirectMeshObjects(objects);
        this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
    }

    protected void renderIndirectObjects(FrameTicking frameTicking, List<SceneObject> objects) {
        this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(objects);
        this.getIndirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
    }

    protected Pair<List<SceneObject>, List<SceneObject>> divideSet2Groups(Set<SceneBackgroundProp> filteredObjectsSet) {
        Map<Boolean, List<SceneObject>> partitionedModels = filteredObjectsSet.stream().collect(Collectors.partitioningBy(e -> e.getModel().getMeshStructure().canBeUsedInIndirectRendering()));
        return new Pair<>(partitionedModels.get(false), partitionedModels.get(true));
    }

    public FBOTexture2DProgram getBackground() {
        return this.background;
    }

    public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
        return this.directGeometryRenderProcessor;
    }

    public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
        return this.indirectGeometryRenderProcessor;
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }

    public IDeferredRenderNode getDeferredRenderNode() {
        return this.deferredRenderNode;
    }
}
