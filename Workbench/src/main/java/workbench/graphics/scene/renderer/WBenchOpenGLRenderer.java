package workbench.graphics.scene.renderer;

import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.culling.ISceneCulling;
import javagems3d.graphics.rendering.scene.culling.SceneCulling;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.*;
import javagems3d.graphics.rendering.ui.dear_imgui.DearUIRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.IDearUIImp;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.resources.data.bindless_rendering_cache.MeshBuffersDataCache;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.controller.binding.WBenchBindingManager;
import workbench.graphics.scene.nodes.*;
import workbench.graphics.scene.nodes.templates.IUIRenderNode;
import workbench.graphics.scene.ui.game.GameEditorInterface;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.ProjectInitInterface;
import workbench.graphics.scene.ui.map.editor.SelectedScene;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.project.map.WBenchMapProject;
import workbench.resources.WBenchResourceManager;

import java.util.*;

public class WBenchOpenGLRenderer extends OpenGLRenderer implements IDearUIImp, IProjectActionsCallback {
    public static final NodeID DEFERRED_RENDER_PASS = new NodeID("d-pass", 0);
    public static final NodeID FORWARD_RENDER_PASS = new NodeID("f-pass", 1);
    public static final NodeID TRANSPARENCY_RENDER_PASS = new NodeID("transparency-pass", 2);
    public static final NodeID POST_FX_RENDER_PASS = new NodeID("post_fx-pass", 3);
    public static final NodeID GLUING_RENDER_PASS = new NodeID("gluing-pass", 4);
    public static final NodeID UI_RENDER_PASS = new NodeID("ui-pass", 5);

    private static DearUIInterface gameEditorInterface;
    private static DearUIInterface mapEditorInterface;
    private static DearUIInterface projectInterface;

    public static Model3D flatTerrain;

    protected Map<NodeID, IRenderNode> conveyorNodes;
    protected IndirectBufferProgram sceneIndirectBufferProgram;
    protected DearUIRenderer dearUIRenderer;
    protected Model2D screenModel;
    private final ISceneCulling sceneCulling;

    private final DebugLinesDrawer debugLinesDrawer;
    private final FBOTexture2DProgram editorScenePreview;
    private final FBOTexture2DProgram gameSceneObjectsPreview;

    public WBenchOpenGLRenderer(IWindow window, WBenchWorld wBenchWorld) {
        super(window, wBenchWorld);
        this.conveyorNodes = new TreeMap<>(Comparator.comparingInt(NodeID::id));

        this.editorScenePreview = new FBOTexture2DProgram(true, false);
        this.gameSceneObjectsPreview = new FBOTexture2DProgram(true, false);

        WBenchOpenGLRenderer.gameEditorInterface = new GameEditorInterface(this, this.gameSceneObjectsPreview);
        WBenchOpenGLRenderer.mapEditorInterface = new MapEditorInterface(this, this.editorScenePreview);
        WBenchOpenGLRenderer.projectInterface = new ProjectInitInterface();

        this.sceneIndirectBufferProgram = new IndirectBufferProgram(DefaultAttributePointers.ATTR_POSITIONS, DefaultAttributePointers.ATTR_NORMALS, DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, DefaultAttributePointers.ATTR_TANGENTS, DefaultAttributePointers.ATTR_BI_TANGENTS, DefaultAttributePointers.ATTR_BONES_INDEXES, DefaultAttributePointers.ATTR_BONES_WEIGHTS);
        this.screenModel = null;

        this.sceneCulling = new SceneCulling(SceneCulling.FRUSTUM_CPU | SceneCulling.DISTANCE, null);
        this.debugLinesDrawer = new DebugLinesDrawer(WBenchResourceManager.globalShaderAssets.debug);
    }

    public static boolean isRenderingBackgroundScene() {
        return (((MapEditorInterface) WBenchOpenGLRenderer.getMapEditorInterface()).getSelectedScene().equals(SelectedScene.BACKGROUND));
    }

    @Override
    public @NotNull Vector2i getRenderingResolution() {
        return this.getWindowSize().div(1.0f);
    }

    protected void setDefaultNodes() {
        IDeferredRenderNode defaultDeferredNode = new WBenchDeferredRenderNode(new FBOTexture2DProgram(true, false), this);
        IForwardRenderNode forwardRenderNode = new WBenchForwardRenderNode(defaultDeferredNode.getOutColorBuffer(), this);
        ITransparencyRenderNode transparencyRenderNode = new WBenchTransparencyRenderNode(defaultDeferredNode.getOutColorBuffer(), this);
        IGluingRenderNode gluingRenderNode = new WBenchGluingRenderNode(transparencyRenderNode.getOutColorBuffer(), forwardRenderNode.getOutColorBuffer(), this);
        IPostFXRenderNode postFXRenderNode = new WBenchPostFXRenderNode(gluingRenderNode.getOutColorBuffer(), this);

        this.setForwardRenderNode(forwardRenderNode);
        this.setDeferredRenderNode(defaultDeferredNode);
        this.setTransparencyRenderNode(transparencyRenderNode);
        this.setPostFXRenderNode(postFXRenderNode);
        this.setGluingRenderNode(gluingRenderNode);
    }

    protected void removeNodes() {
        this.getConveyorNodes().remove(WBenchOpenGLRenderer.FORWARD_RENDER_PASS);
        this.getConveyorNodes().remove(WBenchOpenGLRenderer.DEFERRED_RENDER_PASS);
        this.getConveyorNodes().remove(WBenchOpenGLRenderer.TRANSPARENCY_RENDER_PASS);
        this.getConveyorNodes().remove(WBenchOpenGLRenderer.GLUING_RENDER_PASS);
        this.getConveyorNodes().remove(WBenchOpenGLRenderer.POST_FX_RENDER_PASS);
    }

    public void setForwardRenderNode(@NotNull IForwardRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.FORWARD_RENDER_PASS, node);
    }

    public void setDeferredRenderNode(@NotNull IDeferredRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.DEFERRED_RENDER_PASS, node);
    }

    public void setTransparencyRenderNode(@NotNull ITransparencyRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.TRANSPARENCY_RENDER_PASS, node);
    }

    public void setGluingRenderNode(@NotNull IGluingRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.GLUING_RENDER_PASS, node);
    }

    public void setPostFXRenderNode(@NotNull IPostFXRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.POST_FX_RENDER_PASS, node);
    }

    public void setUIRenderNode(@NotNull IUIRenderNode node) {
        this.getConveyorNodes().put(WBenchOpenGLRenderer.UI_RENDER_PASS, node);
    }

    @SuppressWarnings("all")
    public <T extends IRenderNode> T getRenderNodeByPass(NodeID node) {
        return (T) this.getConveyorNodes().get(node);
    }

    @Override
    public void onStartRender() {
        this.editorScenePreview.createFrameBuffer2DTexture(new Vector2i(256, 256), new T2DAttachmentContainer() {{add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA, GL46.GL_RGBA);}}, true, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        this.gameSceneObjectsPreview.createFrameBuffer2DTexture(new Vector2i(1024, 1024), new T2DAttachmentContainer() {{add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA, GL46.GL_RGBA);}}, true, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        this.constructScreenModel();

        this.dearUIRenderer = new DearUIRenderer(this.getWindow(), WBenchResourceManager.globalShaderAssets.imgui, new JGemsPathSource(new JGemsPath("/assets/wbench/gamefont.ttf"), ISource.Source.INSIDE_JAR), WBenchResourceManager.GetGlobalResources());
        IUIRenderNode uiRenderNode = new WBenchUIRenderNode(this.getDearUIRenderer(), this);
        uiRenderNode.setAnInterface(WBenchOpenGLRenderer.getProjectInterface());
        this.setUIRenderNode(uiRenderNode);

        this.getConveyorNodes().keySet().forEach(e -> Log.get().trace("Registered scenes node: " + e.name()));
        this.createResources();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        IUIRenderNode uiRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.UI_RENDER_PASS);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_STENCIL_BUFFER_BIT);
        if (this.getWorld().getCamera() == null) {
            WBenchOpenGLRenderer.DebugLinesDrawer().renderAndClearRequests((color_shader) -> {
                color_shader.second().performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC4F(new Vector4f(color_shader.first(), 1.0f)));
                color_shader.second().performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
                color_shader.second().performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
            });
            OpenGLRenderer.setViewPort(this.getWindowSize());
            uiRenderNode.onRender(frameTicking);
            return;
        }
        final MapEditorInterface mapEditorInterface1 = ((MapEditorInterface) WBenchOpenGLRenderer.mapEditorInterface);

        IForwardRenderNode forwardRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.FORWARD_RENDER_PASS);
        IDeferredRenderNode deferredRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.DEFERRED_RENDER_PASS);
        ITransparencyRenderNode transparencyRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.TRANSPARENCY_RENDER_PASS);
        IGluingRenderNode gluingRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.GLUING_RENDER_PASS);
        IPostFXRenderNode postRenderNode = this.getRenderNodeByPass(WBenchOpenGLRenderer.POST_FX_RENDER_PASS);
        JGemsOpenGLRenderer.updateTimerSSBO(WBench.get().getScreen(), WBenchResourceManager.localShaderAssets.TimerData);

        this.getWorld().getEnvironment().updateEnvironment(this.getWorld().getCamera());
        OpenGLRenderer.setViewPort(this.getRenderingResolution());

        final boolean renderBackGround = WBenchOpenGLRenderer.isRenderingBackgroundScene();
        JGemsConfig.DEBUG.DISABLE_POINT_LIGHTS = renderBackGround;
        if (this.getCamera() instanceof ControlledCamera camera) {
            WBenchBindingManager wBenchBindingManager = (WBenchBindingManager) WBench.get().getControllerDispatcher().getCurrentController().getBindingManager();
            if (wBenchBindingManager.keyCtrl.isPressed()) {
                camera.setSpeed(WBench.get().getSettings().getCamSpeed() * 0.35f);
            } else if (wBenchBindingManager.keyAlt.isPressed()) {
                camera.setSpeed(WBench.get().getSettings().getCamSpeed() * 2.5f);
            } else {
                camera.setSpeed(WBench.get().getSettings().getCamSpeed());
            }
        }
        Set<SceneObject> toRender = new HashSet<>(renderBackGround ? this.getWorld().getEnvironment().getSkyBox().getBackground().getSkySceneObjects() : this.getWorld().getSceneObjects());

        JGemsOpenGLRenderer.renderScene(this, frameTicking, toRender, Collections.emptyList(), forwardRenderNode, deferredRenderNode, transparencyRenderNode, (e) -> {
            @SuppressWarnings("unchecked") Collection<? extends ICulled>[] collections = new Collection[] { toRender };
            this.getSceneCulling().cull(JGemsTransformManager.INSTANCE.getPerspectiveMatrix(), this.getCamera(), collections);
        });

        GL46.glDepthMask(false);
        gluingRenderNode.onRender(frameTicking);
        GL46.glDepthMask(true);

        forwardRenderNode.getOutColorBuffer().copyFBOtoFBODepth(gluingRenderNode.getOutColorBuffer().getFrameBufferId(), this.getRenderingResolution());
        gluingRenderNode.getOutColorBuffer().bindFBO();
        WBenchOpenGLRenderer.DebugLinesDrawer().renderAndClearRequests((color_shader) -> {
            color_shader.second().performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC4F(new Vector4f(color_shader.first(), 1.0f)));
            color_shader.second().performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
            color_shader.second().performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
        });
        gluingRenderNode.getOutColorBuffer().unBindFBO();

        GL46.glDepthMask(false);
        postRenderNode.onRender(frameTicking);
        GL46.glDepthMask(true);

        this.getWorld().setEndFrameVisibleObjects(toRender);
        OpenGLRenderer.setViewPort(this.getWindowSize());
        uiRenderNode.onRender(frameTicking);
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

    public static void reloadModelResources() {
        WBench.get().getResourceManager().writeResourcesDataCache();
        WBench.get().getResourceManager().loadModelAnimationsInTexture();
    }

    @Override
    public void onOpeningProject(WBenchResourceManager resourceManager, @NotNull WBenchMapProject wBenchProject) {
        this.setDefaultNodes();
        //this.getDebugLinesDrawer().setup();

        resourceManager.writeResourcesDataCache();
        this.initSceneIndirectRenderBuffer(resourceManager.getResourceDataCache().getMeshBuffersDataCache());
        resourceManager.loadMeshMaterialsIsSSBO(WBenchResourceManager.localShaderAssets.MaterialsData);
        resourceManager.loadBindlessHandlersInSSBO(WBenchResourceManager.localShaderAssets.BindlessTexturesData);

        this.getWorld().getEnvironment().createEnvironment(this);
        this.getConveyorNodes().values().stream().filter(e -> !(e instanceof IUIRenderNode)).forEach(IRenderNode::createResources);
        this.getSceneCulling().createResources();

        Log.get().info("Created scenes data");
    }

    @Override
    public void onClosingProject(WBenchResourceManager resourceManager, @NotNull WBenchMapProject wBenchProject) {
        this.destroySceneIndirectRenderBuffer();

        if (this.getWorld().getEnvironment() != null) {
            this.getWorld().getEnvironment().destroyEnvironment();
        }
        this.getConveyorNodes().values().stream().filter(e -> !(e instanceof IUIRenderNode)).forEach(IRenderNode::destroyResources);
        this.getSceneCulling().destroyResources();
        this.removeNodes();

        Log.get().info("Cleared scenes data");
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
        WBenchOpenGLRenderer.flatTerrain = MeshHelper.generatePlane3DModel(null, new Vector3f(-WBench.MAP_SIZE, -0.05f, -WBench.MAP_SIZE), new Vector3f(-WBench.MAP_SIZE, -0.05f, WBench.MAP_SIZE), new Vector3f(WBench.MAP_SIZE, -0.05f, -WBench.MAP_SIZE), new Vector3f(WBench.MAP_SIZE, -0.05f, WBench.MAP_SIZE));
        this.getRenderNodeByPass(WBenchOpenGLRenderer.UI_RENDER_PASS).createResources();
    }

    public void destroyResources() {
        if (WBenchOpenGLRenderer.flatTerrain != null) {
            WBenchOpenGLRenderer.flatTerrain.clear();
        }
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
        if (this.getRenderNodeByPass(WBenchOpenGLRenderer.UI_RENDER_PASS) != null) {
            ((IUIRenderNode) this.getRenderNodeByPass(WBenchOpenGLRenderer.UI_RENDER_PASS)).setAnInterface(dearUIInterface);
        }
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

    public static DearUIInterface getGameEditorInterface() {
        return WBenchOpenGLRenderer.gameEditorInterface;
    }

    public static DearUIInterface getMapEditorInterface() {
        return WBenchOpenGLRenderer.mapEditorInterface;
    }

    public static DearUIInterface getProjectInterface() {
        return WBenchOpenGLRenderer.projectInterface;
    }
}