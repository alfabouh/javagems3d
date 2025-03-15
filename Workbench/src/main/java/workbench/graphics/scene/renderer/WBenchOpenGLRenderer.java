package workbench.graphics.scene.renderer;

import javagems3d.JGems3D;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.scene.culling.ISceneCulling;
import javagems3d.graphics.rendering.scene.culling.SceneCulling;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.ui.dear_imgui.DearUIRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.IDearUIImp;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsRenderingHelper;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.data.cache.MeshBuffersDataCache;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.nodes.templates.*;
import workbench.graphics.scene.nodes.*;
import workbench.graphics.scene.ui.EditorInterface;
import workbench.graphics.scene.ui.ProjectInitInterface;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.project.Project;
import workbench.resources.WBenchResourceManager;

import java.util.*;
import java.util.stream.Collectors;

public class WBenchOpenGLRenderer extends OpenGLRenderer implements IDearUIImp, IProjectActionsCallback {
    public static final NodeID DEFERRED_RENDER_PASS = new NodeID("d-pass", 0);
    public static final NodeID FORWARD_RENDER_PASS = new NodeID("f-pass", 1);
    public static final NodeID TRANSPARENCY_RENDER_PASS = new NodeID("transparency-pass", 2);
    public static final NodeID GLUING_RENDER_PASS = new NodeID("gluing-pass", 3);
    public static final NodeID UI_RENDER_PASS = new NodeID("ui-pass", 4);

    private static DearUIInterface editorInterface;
    private static DearUIInterface projectInterface;

    protected Map<NodeID, IRenderNode> conveyorNodes;
    protected IndirectBufferProgram sceneIndirectBufferProgram;
    protected DearUIRenderer dearUIRenderer;
    protected Model2D screenModel;
    private final ISceneCulling sceneCulling;

    private final DebugLinesDrawer debugLinesDrawer;
    private final FBOTexture2DProgram editorScenePreview;

    public WBenchOpenGLRenderer(IWindow window, WBenchWorld wBenchWorld) {
        super(window, wBenchWorld);
        this.conveyorNodes = new TreeMap<>(Comparator.comparingInt(NodeID::getId));

        this.editorScenePreview = new FBOTexture2DProgram(true);
        WBenchOpenGLRenderer.editorInterface = new EditorInterface(this, this.editorScenePreview, WBench.get().getProjectManager());
        WBenchOpenGLRenderer.projectInterface = new ProjectInitInterface();

        this.sceneIndirectBufferProgram = new IndirectBufferProgram(DefaultAttributePointers.ATTR_POSITIONS, DefaultAttributePointers.ATTR_NORMALS, DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, DefaultAttributePointers.ATTR_TANGENTS, DefaultAttributePointers.ATTR_BI_TANGENTS, DefaultAttributePointers.ATTR_BONES_INDEXES, DefaultAttributePointers.ATTR_BONES_WEIGHTS);
        this.screenModel = null;

        this.sceneCulling = new SceneCulling(this, null);
        this.debugLinesDrawer = new DebugLinesDrawer(WBenchResourceManager.globalShaderAssets.debug);
    }

    @Override
    public @NotNull Vector2i getRenderingResolution() {
        return this.getWindowSize().div(1.0f);
    }

    protected void setNodes() {
        WIDeferredRenderNode defaultDeferredNode = new WDeferredRenderNode(new FBOTexture2DProgram(true), this);
        WIForwardRenderNode forwardRenderNode = new WForwardRenderNode(defaultDeferredNode.getOutColorBuffer(), this);
        WITransparencyRenderNode transparencyRenderNode = new WTransparencyRenderNode(defaultDeferredNode.getOutColorBuffer(), this);
        WIGluingRenderNode gluingRenderNode = new WGluingRenderNode(transparencyRenderNode.getOutColorBuffer(), forwardRenderNode.getOutColorBuffer(), this);

        this.setForwardRenderNode(forwardRenderNode);
        this.setDeferredRenderNode(defaultDeferredNode);
        this.setTransparencyRenderNode(transparencyRenderNode);
        this.setGluingRenderNode(gluingRenderNode);
    }

    protected void removeNodes() {
        this.getConveyorNodes().remove(WBenchOpenGLRenderer.FORWARD_RENDER_PASS);
        this.getConveyorNodes().remove(WBenchOpenGLRenderer.DEFERRED_RENDER_PASS);
        this.getConveyorNodes().remove(WBenchOpenGLRenderer.TRANSPARENCY_RENDER_PASS);
        this.getConveyorNodes().remove(WBenchOpenGLRenderer.GLUING_RENDER_PASS);
    }

    public void setForwardRenderNode(@NotNull WIForwardRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.FORWARD_RENDER_PASS, node);
    }

    public void setDeferredRenderNode(@NotNull WIDeferredRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.DEFERRED_RENDER_PASS, node);
    }

    public void setTransparencyRenderNode(@NotNull WITransparencyRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.TRANSPARENCY_RENDER_PASS, node);
    }

    public void setGluingRenderNode(@NotNull WIGluingRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.GLUING_RENDER_PASS, node);
    }

    public void setUIRenderNode(@NotNull WIUIRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.UI_RENDER_PASS, node);
    }

    @SuppressWarnings("all")
    public @NotNull <T extends IRenderNode> T getRenderNodeByPass(NodeID node) {
        return (T) this.getConveyorNodes().get(node);
    }

    @Override
    public void onStartRender() {
        this.editorScenePreview.createFrameBuffer2DTexture(new Vector2i(256, 256), new T2DAttachmentContainer() {{add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA, GL46.GL_RGBA);}}, true, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        this.constructScreenModel();
        this.dearUIRenderer = new DearUIRenderer(this.getWindow(), WBenchResourceManager.globalShaderAssets.imgui, new JGemsPath("/assets/wbench/gamefont.ttf"), WBenchResourceManager.getGlobalGameResources());

        WIUIRenderNode uiRenderNode = new WUIRenderNode(this.getDearUIRenderer(), this);
        uiRenderNode.setAnInterface(WBenchOpenGLRenderer.getProjectInterface());
        this.setUIRenderNode(uiRenderNode);

        this.getConveyorNodes().keySet().forEach(e -> Log.get().trace("Registered scene node: " + e.getName()));
        this.createResources();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        WIUIRenderNode uiRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.UI_RENDER_PASS);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_STENCIL_BUFFER_BIT);
        if (this.getWorld().getCamera() == null) {
            OpenGLRenderer.setViewPort(this.getWindowSize());
            uiRenderNode.onRender(frameTicking);
            return;
        }
        WIForwardRenderNode forwardRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.FORWARD_RENDER_PASS);
        WIDeferredRenderNode deferredRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.DEFERRED_RENDER_PASS);
        WITransparencyRenderNode transparencyRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.TRANSPARENCY_RENDER_PASS);
        WIGluingRenderNode gluingRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.GLUING_RENDER_PASS);

        this.getWorld().getEnvironment().updateEnvironment(this.getWorld().getCamera());
        OpenGLRenderer.setViewPort(this.getRenderingResolution());

        Set<SceneObject> toRender = new HashSet<>(this.getWorld().getSceneObjects());
        this.getSceneCulling().cull(toRender);

        Map<Stage, List<SceneObject>> dividedGroups = toRender.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(e -> e.getRenderFabric(Pipeline.SCENE).getRenderingStage()));
        deferredRenderNode.setIndirectDeferredRenderingObjects(dividedGroups.getOrDefault(Stage.DEFERRED_INDIRECT, new ArrayList<>()));
        deferredRenderNode.setDirectDeferredRenderingObjects(dividedGroups.getOrDefault(Stage.DEFERRED_DIRECT, new ArrayList<>()));
        forwardRenderNode.setForwardRenderingObjects(dividedGroups.getOrDefault(Stage.FORWARD, new ArrayList<>()));

        deferredRenderNode.onRender(frameTicking);
        forwardRenderNode.onRender(frameTicking);

        Collection<SceneObject> rejectedIndirect = deferredRenderNode.getRejectedIndirectDeferredRenderingObjects();
        Collection<SceneObject> rejectedDirect = deferredRenderNode.getRejectedDirectDeferredRenderingObjects();
        rejectedDirect.addAll(forwardRenderNode.getRejectedDirectForwardRenderingObjects());

        transparencyRenderNode.setIndirectDeferredRenderingObjects(rejectedIndirect);
        transparencyRenderNode.setDirectDeferredRenderingObjects(rejectedDirect);
        transparencyRenderNode.onRender(frameTicking);
        GL46.glDepthMask(false);
        gluingRenderNode.onRender(frameTicking);
        GL46.glDepthMask(true);

        forwardRenderNode.getOutColorBuffer().copyFBOtoFBODepth(gluingRenderNode.getOutColorBuffer().getFrameBufferId(), this.getRenderingResolution());
        gluingRenderNode.getOutColorBuffer().bindFBO();
        WBenchOpenGLRenderer.DebugLinesDrawer().render();
        gluingRenderNode.getOutColorBuffer().unBindFBO();

        OpenGLRenderer.setViewPort(this.getWindowSize());
        uiRenderNode.onRender(frameTicking);

        ((EditorInterface) WBenchOpenGLRenderer.editorInterface).renderPreviewItem();
        //  WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(new Vector3f(), new Vector3f(2.0f, 12.0f, 2.0f), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));

        //JGemsShaderManager imgShader = WBenchResourceManager.localShaderAssets.gui_image;
        //imgShader.beginShading();
        //imgShader.performUniformTexture(new UniformString("texture_sampler"), deferredRenderNode.getOutColorBuffer().getTextureByIndex(0));//finalFBO.getTextureByIndex(0)
        //imgShader.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        //JGemsRenderingHelper.renderModel2D(this.getScreenModel(), GL46.GL_TRIANGLES);
        //imgShader.endShading();
    }

    @Override
    public void onStopRender() {
        if (this.editorScenePreview != null) {
            this.editorScenePreview.clearFBO();
        }
        if (this.screenModel != null) {
            this.screenModel.clear();
        }
        this.getSceneIndirectBuffer().clear();
        this.getDearUIRenderer().destroyUI();
        this.destroyResources();
        this.getConveyorNodes().clear();
    }

    @Override
    public void onOpeningProject(WBenchResourceManager resourceManager, @NotNull Project project) {
        this.setNodes();
        this.getDebugLinesDrawer().setup();

        resourceManager.writeResourcesDataCache();
        this.initSceneIndirectRenderBuffer(resourceManager.getResourceDataCache().getMeshBuffersDataCache());
        resourceManager.loadMeshMaterialsIsSSBO(WBenchResourceManager.localShaderAssets.MaterialsData);
        resourceManager.loadBindlessHandlersInSSBO(WBenchResourceManager.localShaderAssets.BindlessTexturesData);
        resourceManager.loadModelAnimationsInTexture();

        this.getWorld().getEnvironment().createEnvironment(this);
        this.getConveyorNodes().values().stream().filter(e -> !(e instanceof WIUIRenderNode)).forEach(IRenderNode::createResources);
        this.getSceneCulling().createResources();

        Log.get().info("Created scene data");

     //   this.getWorld().addObjectInWorld(new WBenchObject(this.getWorld(), new Model3D(new Pose3D(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(0.02f)), WBenchResourceManager.localModelAssets.test), RenderAttributes.get(RenderTable.getDefaultIndirect())));
    }

    @Override
    public void onClosingProject(WBenchResourceManager resourceManager, @NotNull Project project) {
        this.destroySceneIndirectRenderBuffer();
        this.getDebugLinesDrawer().clear();

        if (this.getWorld().getEnvironment() != null) {
            this.getWorld().getEnvironment().destroyEnvironment();
        }
        this.getConveyorNodes().values().stream().filter(e -> !(e instanceof WIUIRenderNode)).forEach(IRenderNode::destroyResources);
        this.getSceneCulling().destroyResources();
        this.removeNodes();

        Log.get().info("Cleared scene data");
    }

    protected void constructScreenModel() {
        if (this.screenModel != null) {
            this.screenModel.clear();
        }
        this.screenModel = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getWindowSize()), 0);
    }

    @Override
    public @NotNull Model2D getScreenModel() {
        return this.screenModel;
    }

    public void initSceneIndirectRenderBuffer(MeshBuffersDataCache meshBuffersDataCache) {
        this.getSceneIndirectBuffer().init(meshBuffersDataCache);
    }

    public void destroySceneIndirectRenderBuffer() {
        this.getSceneIndirectBuffer().clear();
    }

    @Override
    public ICamera getCamera() {
        return this.getWorld().getCamera();
    }

    public void createResources() {
        this.getRenderNodeByPass(WBenchOpenGLRenderer.UI_RENDER_PASS).createResources();
    }

    public void destroyResources() {
        this.getRenderNodeByPass(WBenchOpenGLRenderer.UI_RENDER_PASS).destroyResources();
    }

    @Override
    public void onWindowResize(IWindow window) {
        if (this.getDearUIRenderer() != null) {
            this.getDearUIRenderer().onWindowResize(window);
        }
        if (this.getSceneCulling() != null) {
            this.getSceneCulling().onWindowResize(window);
        }
        this.constructScreenModel();
        this.getConveyorNodes().values().stream().filter(Objects::nonNull).forEach(e -> e.onWindowResize(window));
    }

    public static DebugLinesDrawer DebugLinesDrawer() {
        return ((WBenchOpenGLRenderer) WBench.get().getScreen().getScene().getSceneRenderer()).getDebugLinesDrawer();
    }

    public DebugLinesDrawer getDebugLinesDrawer() {
        return this.debugLinesDrawer;
    }

    @Override
    public @NotNull WBenchWorld getWorld() {
        return (WBenchWorld) super.getWorld();
    }

    public IndirectBufferProgram getSceneIndirectBuffer() {
        return this.sceneIndirectBufferProgram;
    }

    @Override
    public void openUIInterface(@Nullable DearUIInterface dearUIInterface) {
        ((WIUIRenderNode) this.getRenderNodeByPass(WBenchOpenGLRenderer.UI_RENDER_PASS)).setAnInterface(dearUIInterface);
    }

    public DearUIRenderer getDearUIRenderer() {
        return this.dearUIRenderer;
    }

    public Map<NodeID, IRenderNode> getConveyorNodes() {
        return this.conveyorNodes;
    }

    @Override
    public ISceneCulling getSceneCulling() {
        return this.sceneCulling;
    }

    public static DearUIInterface getEditorInterface() {
        return WBenchOpenGLRenderer.editorInterface;
    }

    public static DearUIInterface getProjectInterface() {
        return WBenchOpenGLRenderer.projectInterface;
    }
}