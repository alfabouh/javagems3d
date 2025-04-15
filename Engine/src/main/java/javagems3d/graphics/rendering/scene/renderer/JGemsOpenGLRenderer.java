package javagems3d.graphics.rendering.scene.renderer;

import com.jme3.bounding.BoundingBox;
import javagems3d.JGems3D;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Redirections;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.scene.culling.ISceneCulling;
import javagems3d.graphics.rendering.scene.culling.SceneCulling;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.rendering.scene.renderer.nodes.*;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.ForwardRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.*;
import javagems3d.graphics.rendering.ui.dear_imgui.DearUIRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.IDearUIImp;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIGameInterface;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIMenuInterface;
import javagems3d.graphics.rendering.ui.jgems_imgui.IJGemsUIImp;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.JGemsRenderingHelper;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.processing.base.IMapProcessor;
import javagems3d.mapping.processing.callbacks.IMapActionCallback;

import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.data.cache.MeshBuffersDataCache;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import java.util.*;
import java.util.function.Consumer;

public class JGemsOpenGLRenderer extends OpenGLRenderer implements IJGemsUIImp, IDearUIImp, IMapActionCallback {
    public static JGemsShaderManager UBO_SHADER = null;

    public static final NodeID DEFERRED_RENDER_PASS = new NodeID("d-pass", 0);
    public static final NodeID FORWARD_RENDER_PASS = new NodeID("f-pass", 1);
    public static final NodeID TRANSPARENCY_RENDER_PASS = new NodeID("transparency-pass", 2);
    public static final NodeID UI_RENDER_PASS = new NodeID("ui-pass", 3);
    public static final NodeID GLUING_RENDER_PASS = new NodeID("gluing-pass", 4);
    public static final NodeID POST_EFFECTS_RENDER_PASS = new NodeID("post-fx-pass", 5);

    protected Map<NodeID, IRenderNode> conveyorNodes;
    public static DearUIInterface inGameInterface;
    public static DearUIInterface inMenuInterface;
    private final ISceneCulling<SceneObject> sceneCulling;
    protected IndirectBufferProgram sceneIndirectBufferProgram;
    protected JGemsUI jGemsUI;
    protected DearUIRenderer dearUIRenderer;
    protected Model2D screenModel;

    private final DebugLinesDrawer debugLinesDrawer;

    public JGemsOpenGLRenderer(IWindow window, IRenderWorld sceneWorld) {
        super(window, sceneWorld);
        this.conveyorNodes = new TreeMap<>(Comparator.comparingInt(NodeID::getId));

        JGemsOpenGLRenderer.inGameInterface = new DearUIGameInterface();
        JGemsOpenGLRenderer.inMenuInterface = new DearUIMenuInterface();

        this.sceneIndirectBufferProgram = new IndirectBufferProgram(DefaultAttributePointers.ATTR_POSITIONS, DefaultAttributePointers.ATTR_NORMALS, DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, DefaultAttributePointers.ATTR_TANGENTS, DefaultAttributePointers.ATTR_BI_TANGENTS, DefaultAttributePointers.ATTR_BONES_INDEXES, DefaultAttributePointers.ATTR_BONES_WEIGHTS);
        this.screenModel = null;

        this.sceneCulling = new SceneCulling<>(SceneCulling.FRUSTUM_CPU | SceneCulling.DISTANCE, null);
        this.debugLinesDrawer = new DebugLinesDrawer(JGemsResourceManager.globalShaderAssets.debug);
    }

    @Override
    public @NotNull Vector2i getRenderingResolution() {
        return this.getWindowSize().div(1.0f);
    }

    protected void setDefaultNodes() {
        IDeferredRenderNode defaultDeferredNode = new JGemsDeferredRenderNode(new FBOTexture2DProgram(true), this);
        IForwardRenderNode forwardRenderNode = new JGemsForwardRenderNode(defaultDeferredNode.getOutColorBuffer(), this);
        ITransparencyRenderNode transparencyRenderNode = new JGemsTransparencyRenderNode(defaultDeferredNode.getOutColorBuffer(), this);
        IGluingRenderNode gluingRenderNode = new JGemsGluingRenderNode(transparencyRenderNode.getOutColorBuffer(), forwardRenderNode.getOutColorBuffer(), this);
        IPostFXRenderNode postFXRenderNode = new JGemsPostFXRenderNode(gluingRenderNode.getOutColorBuffer(), this);
        IUIRenderNode iuiRenderNode = new JGemsUIRenderNode(this.getDearUIRenderer(), this.getJGemsUI(), this);

        this.setDeferredRenderNode(defaultDeferredNode);
        this.setForwardRenderNode(forwardRenderNode);
        this.setTransparencyRenderNode(transparencyRenderNode);
        this.setGluingRenderNode(gluingRenderNode);
        this.setPostFXRenderNode(postFXRenderNode);
        this.setUIRenderNode(iuiRenderNode);
    }

    public void setDeferredRenderNode(@NotNull IDeferredRenderNode node) {
        this.getConveyorNodes().put(JGemsOpenGLRenderer.DEFERRED_RENDER_PASS, node);
    }

    public void setForwardRenderNode(@NotNull IForwardRenderNode node) {
        this.getConveyorNodes().put(JGemsOpenGLRenderer.FORWARD_RENDER_PASS, node);
    }

    public void setTransparencyRenderNode(@NotNull ITransparencyRenderNode node) {
        this.getConveyorNodes().put(JGemsOpenGLRenderer.TRANSPARENCY_RENDER_PASS, node);
    }

    public void setUIRenderNode(@NotNull IUIRenderNode node) {
        this.getConveyorNodes().put(JGemsOpenGLRenderer.UI_RENDER_PASS, node);
    }

    public void setGluingRenderNode(@NotNull IGluingRenderNode node) {
        this.getConveyorNodes().put(JGemsOpenGLRenderer.GLUING_RENDER_PASS, node);
    }

    public void setPostFXRenderNode(@NotNull IPostFXRenderNode node) {
        this.getConveyorNodes().put(JGemsOpenGLRenderer.POST_EFFECTS_RENDER_PASS, node);
    }

    @SuppressWarnings("all")
    public @NotNull <T extends IRenderNode> T getRenderNodeByPass(NodeID node) {
        return (T) this.getConveyorNodes().get(node);
    }

    @Override
    public void onStartRender() {
        this.constructScreenModel();
        this.jGemsUI = new JGemsUI();
        this.dearUIRenderer = new DearUIRenderer(this.getWindow(), JGemsResourceManager.globalShaderAssets.imgui, null, JGemsResourceManager.getGlobalGameResources());

        this.setDefaultNodes();
        this.getConveyorNodes().keySet().forEach(e -> Log.get().trace("Registered scene node: " + e.getName()));
        this.createResources();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        IDeferredRenderNode deferredRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.DEFERRED_RENDER_PASS);
        IForwardRenderNode forwardRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.FORWARD_RENDER_PASS);
        ITransparencyRenderNode transparencyRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.TRANSPARENCY_RENDER_PASS);
        IGluingRenderNode gluingRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.GLUING_RENDER_PASS);
        IPostFXRenderNode postRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.POST_EFFECTS_RENDER_PASS);
        IUIRenderNode uiRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.UI_RENDER_PASS);

        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_STENCIL_BUFFER_BIT);
        OpenGLRenderer.setViewPort(this.getWindowSize());
        if (this.getWorld().getCamera() == null) {
            uiRenderNode.setAnInterface(JGemsOpenGLRenderer.inMenuInterface);
            uiRenderNode.onRender(frameTicking);
            return;
        }
        if (JGems3D.get().isPaused()) {
            uiRenderNode.setAnInterface(JGemsOpenGLRenderer.inGameInterface);
            uiRenderNode.onRender(frameTicking);
            return;
        }
        this.getWorld().getEnvironment().updateEnvironment(this.getWorld().getCamera());
        OpenGLRenderer.setViewPort(this.getRenderingResolution());

        Set<SceneObject> toRender = new HashSet<>(this.getWorld().getSceneObjects());
        JGemsOpenGLRenderer.renderScene(this, frameTicking, toRender, forwardRenderNode, deferredRenderNode, transparencyRenderNode, (e) -> {
            this.getSceneCulling().cull(toRender, JGemsTransformManager.INSTANCE.getPerspectiveMatrix(), this.getCamera());
        });

        GL46.glDepthMask(false);
        gluingRenderNode.onRender(frameTicking);
        postRenderNode.onRender(frameTicking);
        GL46.glDepthMask(true);

        OpenGLRenderer.setViewPort(this.getWindowSize());
        this.renderFinalSceneInMainBuffer(postRenderNode.getOutColorBuffer());
        uiRenderNode.setAnInterface(JGemsOpenGLRenderer.inGameInterface);
        uiRenderNode.onRender(frameTicking);

        if (JGemsConfig.DEBUG.SHOW_DEBUG_LINES) {
            for (SceneObject sceneObject : this.getWorld().getEnvironment().getSkyBox().getBackground().getSkySceneObjects()) {
                CullingAABB cullingAABB = sceneObject.getCullingData();
                if (cullingAABB != null) {
                    JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(0.0f, 1.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
                }
            }
            for (SceneObject sceneObject : this.getWorld().getSceneObjects()) {
                CullingAABB cullingAABB = sceneObject.getCullingData();
                if (cullingAABB != null) {
                    JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
                }
                //if (sceneObject instanceof SceneEntity) {
                //    SceneEntity sceneEntity = (SceneEntity) sceneObject;
                //    if (sceneEntity.getWorldItem() instanceof JGemsBody) {
                //        JGemsBody gemsBody = (JGemsBody) sceneEntity.getWorldItem();
                //        BoundingBox boundingBox = new BoundingBox();
                //        gemsBody.getPhysicsRigidBody().boundingBox(boundingBox);
                //        Vector3f min = DynamicsUtils.convertV3F_JOML(boundingBox.getMin(new com.jme3.math.Vector3f()));
                //        Vector3f max = DynamicsUtils.convertV3F_JOML(boundingBox.getMax(new com.jme3.math.Vector3f()));
                //        JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(min, max, new Vector3f(0.0f, 0.0f, 1.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
                //    }
                //}
            }
        }
        JGemsOpenGLRenderer.DebugLinesDrawer().render();
    }

    public static void renderScene(OpenGLRenderer openGLRenderer, FrameTicking frameTicking, Collection<SceneObject> toRender, IForwardRenderNode forwardRenderNode, IDeferredRenderNode deferredRenderNode, ITransparencyRenderNode transparencyRenderNode, @Nullable Consumer<Void> cullingFun) {
        if (cullingFun != null) {
            cullingFun.accept(null);
        }

        List<SceneObject> redirectedInTransparency = new ArrayList<>();
        Map<Stage, List<SceneObject>> dividedGroups = OpenGLRenderer.groupObjectsFromStages(toRender, Pipeline.SCENE, new Pair<>(redirectedInTransparency, Redirections.SCENE__IN__TRANSPARENCY));
        deferredRenderNode.setIndirectDeferredRenderingObjects(dividedGroups.getOrDefault(Stage.DEFERRED_INDIRECT, new ArrayList<>()));
        deferredRenderNode.setDirectDeferredRenderingObjects(dividedGroups.getOrDefault(Stage.DEFERRED_DIRECT, new ArrayList<>()));
        forwardRenderNode.setForwardRenderingObjects(dividedGroups.getOrDefault(Stage.FORWARD, new ArrayList<>()));

        deferredRenderNode.onRender(frameTicking);
        forwardRenderNode.onRender(frameTicking);

        Collection<SceneObject> rejectedIndirect = deferredRenderNode.getRejectedIndirectDeferredRenderingObjects();
        Collection<SceneObject> rejectedDirect = deferredRenderNode.getRejectedDirectDeferredRenderingObjects();
        rejectedDirect.addAll(forwardRenderNode.getRejectedDirectForwardRenderingObjects());
        rejectedDirect.addAll(redirectedInTransparency);

        transparencyRenderNode.setIndirectDeferredRenderingObjects(rejectedIndirect);
        transparencyRenderNode.setDirectDeferredRenderingObjects(rejectedDirect);
        transparencyRenderNode.onRender(frameTicking);
    }

    protected void renderFinalSceneInMainBuffer(FBOTexture2DProgram finalFBO) {
        JGemsShaderManager imgShader = JGemsResourceManager.globalShaderAssets.gui_image;
        imgShader.beginShading();
        imgShader.performUniformTextureBindless(new UniformString("texture_map"), finalFBO.getTextureByIndex(0));//finalFBO.getTextureByIndex(0)
        imgShader.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsRenderingHelper.renderModel2D(this.getScreenModel(), GL46.GL_TRIANGLES);
        imgShader.endShading();
    }

    @Override
    public void onStopRender() {
        if (this.screenModel != null) {
            this.screenModel.clear();
        }
        this.getSceneIndirectBuffer().clear();
        this.getJGemsUI().destroyUI();
        this.getDearUIRenderer().destroyUI();
        this.destroyResources();
        this.getConveyorNodes().clear();
    }

    protected void constructScreenModel() {
        if (this.screenModel != null) {
            this.screenModel.clear();
        }
        this.screenModel = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getWindowSize()), 0);
    }

    @Override
    public void openUIPanel(@Nullable PanelUI panelUI) {
        this.getJGemsUI().setPanel(panelUI);
    }

    @Override
    public @NotNull Model2D getScreenModel() {
        return this.screenModel;
    }

    @Override
    public void onLoaded(@NotNull IMapProcessor mapProcessor, @NotNull IGameMap gameMap, @NotNull JGemsResourceManager resourceManager) {
        this.initSceneIndirectRenderBuffer(resourceManager.getResourceDataCache().getMeshBuffersDataCache());
        resourceManager.loadMeshMaterialsIsSSBO(JGemsResourceManager.globalShaderAssets.MaterialsData);
        resourceManager.loadBindlessHandlersInSSBO(JGemsResourceManager.globalShaderAssets.BindlessTexturesData);
        resourceManager.loadModelAnimationsInTexture();
    }

    @Override
    public void onDestroying(@NotNull IGameMap gameMap, @NotNull JGemsResourceManager resourceManager) {
        this.destroySceneIndirectRenderBuffer();
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
        this.getDebugLinesDrawer().setup();
        this.getWorld().getEnvironment().createEnvironment(this);
        this.getConveyorNodes().values().forEach(IRenderNode::createResources);
        this.getSceneCulling().createResources();
    }

    public void destroyResources() {
        this.getDebugLinesDrawer().clear();
        this.getWorld().getEnvironment().destroyEnvironment();
        this.getConveyorNodes().values().forEach(IRenderNode::destroyResources);
        this.getSceneCulling().destroyResources();
    }

    @Override
    public void onWindowResize(IWindow window) {
        if (this.getJGemsUI() != null) {
            this.getJGemsUI().onWindowResize(window);
        }
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
        return ((JGemsOpenGLRenderer) JGems3D.get().getScreen().getScene().getSceneRenderer()).getDebugLinesDrawer();
    }

    public DebugLinesDrawer getDebugLinesDrawer() {
        return this.debugLinesDrawer;
    }

    @Override
    public @NotNull SceneWorld getWorld() {
        return (SceneWorld) super.getWorld();
    }

    public IndirectBufferProgram getSceneIndirectBuffer() {
        return this.sceneIndirectBufferProgram;
    }

    @Override
    public JGemsUI getJGemsUI() {
        return this.jGemsUI;
    }

    @Override
    public void openUIInterface(@Nullable DearUIInterface dearUIInterface) {
        ((IUIRenderNode) this.getRenderNodeByPass(JGemsOpenGLRenderer.UI_RENDER_PASS)).setAnInterface(dearUIInterface);
    }

    @Override
    public DearUIRenderer getDearUIRenderer() {
        return this.dearUIRenderer;
    }

    public Map<NodeID, IRenderNode> getConveyorNodes() {
        return this.conveyorNodes;
    }

    @Override
    public ISceneCulling<SceneObject> getSceneCulling() {
        return this.sceneCulling;
    }

    public static JGemsShaderManager UBOShader() {
        return JGemsOpenGLRenderer.UBO_SHADER;
    }
}