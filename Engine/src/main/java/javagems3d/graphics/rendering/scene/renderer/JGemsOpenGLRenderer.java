package javagems3d.graphics.rendering.scene.renderer;

import api.events.EventBus;
import api.events.EventLauncher;
import api.scripting.coding.env.internal.game.init.events.rendering.ogl.*;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.misc.JSFrameTicking;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneWorldLiquid;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;
import api.scripting.JavaToJsAPI;
import com.jme3.bounding.BoundingBox;
import javagems3d.JGems3D;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.world.SceneWorldLiquid;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Redirections;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.scene.culling.ISceneCulling;
import javagems3d.graphics.rendering.scene.culling.SceneCulling;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.rendering.scene.renderer.nodes.*;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.*;
import javagems3d.graphics.rendering.ui.dear_imgui.DearUIRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.IDearUIImp;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIGameInterface;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIMenuInterface;
import javagems3d.graphics.rendering.ui.jgems_imgui.IJGemsUIImp;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.IScreen;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.processing.base.IMapProcessor;
import javagems3d.system.external.mapping.processing.callbacks.IMapActionCallback;
import javagems3d.physics.entities.kinematic.JGemsKinematicItem;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.data.bindless_rendering_cache.MeshBuffersDataCache;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JGemsOpenGLRenderer extends OpenGLRenderer implements IJGemsUIImp, IDearUIImp, IMapActionCallback {
    public static int DEBUG_CULLED_OBJECTS;
    public static int DEBUG_CULLED_SUBMESHES;

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
    private final ISceneCulling sceneCulling;
    protected IndirectBufferProgram sceneIndirectBufferProgram;
    protected JGemsUI jGemsUI;
    protected DearUIRenderer dearUIRenderer;
    protected Model2D screenModel;

    private final DebugLinesDrawer debugLinesDrawer;

    public JGemsOpenGLRenderer(IWindow window, IRenderWorld sceneWorld) {
        super(window, sceneWorld);
        this.conveyorNodes = new TreeMap<>(Comparator.comparingInt(NodeID::id));

        JGemsOpenGLRenderer.inGameInterface = new DearUIGameInterface();
        JGemsOpenGLRenderer.inMenuInterface = new DearUIMenuInterface();

        this.sceneIndirectBufferProgram = new IndirectBufferProgram(DefaultAttributePointers.ATTR_POSITIONS, DefaultAttributePointers.ATTR_NORMALS, DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, DefaultAttributePointers.ATTR_TANGENTS, DefaultAttributePointers.ATTR_BI_TANGENTS, DefaultAttributePointers.ATTR_BONES_INDEXES, DefaultAttributePointers.ATTR_BONES_WEIGHTS);
        this.screenModel = null;

        this.sceneCulling = new SceneCulling(SceneCulling.FRUSTUM_CPU | SceneCulling.DISTANCE, null);
        this.debugLinesDrawer = new DebugLinesDrawer(JGemsResourceManager.globalShaderAssets.debug);
    }

    @Override
    public @NotNull Vector2i getRenderingResolution() {
        return this.getWindowSize().div(1.0f);
    }

    protected void setDefaultNodes() {
        IDeferredRenderNode defaultDeferredNode = new JGemsDeferredRenderNode(new FBOTexture2DProgram(true, false), this);
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
    public <T extends IRenderNode> T getRenderNodeByPass(NodeID node) {
        return (T) this.getConveyorNodes().get(node);
    }

    @Override
    public void onStartRender() {
        this.constructScreenModel();
        this.jGemsUI = new JGemsUI(this.getWindow());

        {
            JavaToJsAPI.Js_GAME_registerUI__EVENT();
            JavaToJsAPI.Js_GAME_registerUIBehaviour__EVENT(this.jGemsUI);
        }

        this.dearUIRenderer = new DearUIRenderer(this.getWindow(), JGemsResourceManager.globalShaderAssets.imgui, null, JGemsHelper.resources().getGlobalGameResources());

        this.setDefaultNodes();
        this.getConveyorNodes().keySet().forEach(e -> Log.get().trace("Registered scenes node: " + e.name()));
        this.createResources();

        EventLauncher.pushEvent(new EventBus.InitRendererOGLEvent(this), new Pair<>(new JSInitRendererOGLEvent(new JSOpenGLRenderer(this)), JavaToJsAPI.Target.Game));
    }

    public static void renderNodeWithEvent(OpenGLRenderer openGLRenderer, FrameTicking frameTicking, IRenderNode node) {
        if (openGLRenderer instanceof JGemsOpenGLRenderer) {
            if (!EventLauncher.pushEvent(new EventBus.RenderOGLNodeEvent(openGLRenderer, node, frameTicking, EventBus.Run.PRE), new Pair<>(new JSRenderOGLNodeEvent(new JSOpenGLRenderer(openGLRenderer), JSRenderNode.get(node.getNodeID()), new JSFrameTicking(frameTicking), JSEventRun.PRE), JavaToJsAPI.Target.Game)).isCancelled()) {
                node.onRender(frameTicking);
                EventLauncher.pushEvent(new EventBus.RenderOGLNodeEvent(openGLRenderer, node, frameTicking, EventBus.Run.POST),  new Pair<>(new JSRenderOGLNodeEvent(new JSOpenGLRenderer(openGLRenderer), JSRenderNode.get(node.getNodeID()), new JSFrameTicking(frameTicking), JSEventRun.POST), JavaToJsAPI.Target.Game));
            }
        } else {
            node.onRender(frameTicking);
        }
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        {
            JGemsOpenGLRenderer.DEBUG_CULLED_OBJECTS = 0;
            JGemsOpenGLRenderer.DEBUG_CULLED_SUBMESHES = 0;
        }
        IDeferredRenderNode deferredRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.DEFERRED_RENDER_PASS);
        IForwardRenderNode forwardRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.FORWARD_RENDER_PASS);
        ITransparencyRenderNode transparencyRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.TRANSPARENCY_RENDER_PASS);
        IGluingRenderNode gluingRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.GLUING_RENDER_PASS);
        IPostFXRenderNode postRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.POST_EFFECTS_RENDER_PASS);
        IUIRenderNode uiRenderNode = this.getRenderNodeByPass(JGemsOpenGLRenderer.UI_RENDER_PASS);
        JGemsOpenGLRenderer.updateTimerSSBO(JGemsHelper.screen().getScreen(), JGemsResourceManager.globalShaderAssets.TimerData);

        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_STENCIL_BUFFER_BIT);
        OpenGLRenderer.setViewPort(this.getWindowSize());
        if (this.getWorld().getCamera() == null) {
            uiRenderNode.setAnInterface(JGemsOpenGLRenderer.inMenuInterface);
            JGemsOpenGLRenderer.renderNodeWithEvent(this, frameTicking, uiRenderNode);
            return;
        }
        if (JGems3D.get().isPaused()) {
            uiRenderNode.setAnInterface(JGemsOpenGLRenderer.inGameInterface);
            JGemsOpenGLRenderer.renderNodeWithEvent(this, frameTicking, uiRenderNode);
            return;
        }
        this.getWorld().getEnvironment().updateEnvironment(this.getWorld().getCamera());
        OpenGLRenderer.setViewPort(this.getRenderingResolution());

        final Set<SceneObject> toRenderObjects = new HashSet<>(this.getWorld().getSceneObjects());
        final Set<SceneWorldLiquid> toRenderLiquids = new HashSet<>(this.getWorld().getLiquids());

        final Set<JSSceneObjectI> sceneObjectJS = toRenderObjects.stream().map(e -> (JSSceneObjectI) () -> e).collect(Collectors.toSet());
        final Set<JSSceneWorldLiquid> sceneWorldLiquidsJS = toRenderLiquids.stream().map(JSSceneWorldLiquid::new).collect(Collectors.toSet());
        final Set<ParticleFX> toRenderParticles = new HashSet<>(this.getWorld().getEnvironment().getParticlesScene().getParticlesManager().getParticlesFXCollection());
        final Set<DecalFX> toRenderDecals = new HashSet<>(this.getWorld().getEnvironment().getDecalsScene().getDecalFXCollection());

        this.getSceneCulling().updateFrustum(JGemsTransformManager.INSTANCE.getPerspectiveMatrix(), this.getCamera());
        if (!EventLauncher.pushEvent(new EventBus.RenderOGLSceneEvent(this, frameTicking, EventBus.Run.PRE, toRenderObjects, toRenderLiquids, toRenderParticles), new Pair<>(new JSRenderOGLSceneEvent(new JSOpenGLRenderer(this), new JSFrameTicking(frameTicking), JSEventRun.PRE, sceneObjectJS, sceneWorldLiquidsJS), JavaToJsAPI.Target.Game)).isCancelled()) {
            JGemsOpenGLRenderer.renderScene(this, frameTicking, toRenderObjects, toRenderLiquids, toRenderDecals, toRenderParticles, forwardRenderNode, deferredRenderNode, transparencyRenderNode, (e) -> {
                @SuppressWarnings("unchecked") Collection<? extends ICulled>[] collections = new Collection[] { toRenderObjects, toRenderLiquids, toRenderParticles, toRenderDecals };
                this.getSceneCulling().cull(JGemsTransformManager.INSTANCE.getPerspectiveMatrix(), this.getCamera(), collections);
            });

            GL46.glDepthMask(false);
            JGemsOpenGLRenderer.renderNodeWithEvent(this, frameTicking, gluingRenderNode);
            JGemsOpenGLRenderer.renderNodeWithEvent(this, frameTicking, postRenderNode);
            GL46.glDepthMask(true);

            OpenGLRenderer.setViewPort(this.getWindowSize());
            this.renderFinalSceneInMainBuffer(postRenderNode.getOutColorBuffer());
            uiRenderNode.setAnInterface(JGemsOpenGLRenderer.inGameInterface);
            JGemsOpenGLRenderer.renderNodeWithEvent(this, frameTicking, uiRenderNode);
            JGemsOpenGLRenderer.renderDebug(
                    this.getWorld().getSceneObjects(),
                    this.getWorld().getLiquids(),
                    this.getWorld().getEnvironment().getParticlesScene().getParticlesManager().getParticlesFXCollection(),
                    this.getWorld().getEnvironment().getDecalsScene().getDecalFXCollection()
            );
            EventLauncher.pushEvent(new EventBus.RenderOGLSceneEvent(this, frameTicking, EventBus.Run.POST, toRenderObjects, toRenderLiquids, toRenderParticles), new Pair<>(new JSRenderOGLSceneEvent(new JSOpenGLRenderer(this), new JSFrameTicking(frameTicking), JSEventRun.POST, sceneObjectJS, sceneWorldLiquidsJS), JavaToJsAPI.Target.Game));
        }

        JGemsOpenGLRenderer.DebugLinesDrawer().renderAndClearRequests((color_shader) -> {
            color_shader.second().performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC4F(new Vector4f(color_shader.first(), 1.0f)));
            color_shader.second().performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
            color_shader.second().performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
        });
    }

    public static void updateTimerSSBO(IScreen screen, ShaderStorageBufferObject shaderStorageBufferObject) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(1);
            buffer.put(screen.getRenderTicks());
            buffer.flip();
            ShaderStorageBufferProgram.updateSubDataSSBO(shaderStorageBufferObject, 0L, buffer);
        }
    }

    public static void renderDebug(Collection<SceneObject> sceneObjects, Collection<SceneWorldLiquid> liquids, Collection<ParticleFX> particleFXSet, Collection<DecalFX> decalFXSet) {
        if (JGemsConfig.DEBUG.SHOW_DEBUG_LINES) {
            // for (SceneObject sceneObject : this.getWorld().getEnvironment().getSkyBox().getBackground().getSkySceneObjects()) {
            //     CullingAABB cullingAABB = sceneObject.getCullingData();
            //     if (cullingAABB != null) {
            //         JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(0.0f, 1.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
            //     }
            // }
            for (ParticleFX particleFX : particleFXSet) {
                JGemsHelper.render().renderModelAABBDebug(JGemsOpenGLRenderer.DebugLinesDrawer(), particleFX.getCullingData());
            }
            for (DecalFX decalFX : decalFXSet) {
                JGemsHelper.render().renderModelAABBDebug(JGemsOpenGLRenderer.DebugLinesDrawer(), decalFX.getCullingData(), new Matrix4f(), new Vector3f(1.0f, 1.0f, 0.0f));
            }
            for (SceneObject sceneObject : sceneObjects) {
                if (sceneObject.getModel() != null) {
                    JGemsHelper.render().renderModelAABBDebug(JGemsOpenGLRenderer.DebugLinesDrawer(), sceneObject.getModel());
                }
                IPlayer player = JGemsHelper.map().getCurrentGameMapPlayer();
                if (player instanceof JGemsKinematicItem) {
                    BoundingBox boundingBox = new BoundingBox();
                    ((JGemsKinematicItem) player).getPhysicsBody().boundingBox(boundingBox);
                    Vector3f min = DynamicsUtils.convertV3F_JOML(boundingBox.getMin(new com.jme3.math.Vector3f()));
                    Vector3f max = DynamicsUtils.convertV3F_JOML(boundingBox.getMax(new com.jme3.math.Vector3f()));
                    JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(min, max, new Vector3f(0.0f, 0.0f, 1.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
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

            for (SceneWorldLiquid sceneWorldLiquid : liquids) {
                CullingAABB cullingAABB = sceneWorldLiquid.getCullingData();
                JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(0.0f, 0.0f, 1.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
            }
        }
    }

    public static void renderScene(OpenGLRenderer openGLRenderer, FrameTicking frameTicking, Collection<SceneObject> toRenderObjects, Collection<SceneWorldLiquid> toRenderLiquids, Set<DecalFX> toRenderDecals, Set<ParticleFX> toRenderParticles, IForwardRenderNode forwardRenderNode, IDeferredRenderNode deferredRenderNode, ITransparencyRenderNode transparencyRenderNode, @Nullable Consumer<Void> cullingFun) {
        if (cullingFun != null) {
            cullingFun.accept(null);
        }

        List<SceneObject> redirectedInTransparency = new ArrayList<>();
        Map<Stage, List<SceneObject>> dividedGroups = OpenGLRenderer.groupObjectsFromStages(toRenderObjects, Pipeline.SOLID_SCENE, new Pair<>(redirectedInTransparency, Redirections.SOLID_SCENE__IN__TRANSPARENCY));
        deferredRenderNode.setIndirectDeferredRenderingObjects(dividedGroups.getOrDefault(Stage.DEFERRED_INDIRECT, new ArrayList<>()));
        deferredRenderNode.setDirectDeferredRenderingObjects(dividedGroups.getOrDefault(Stage.DEFERRED_DIRECT, new ArrayList<>()));
        forwardRenderNode.setForwardRenderingObjects(dividedGroups.getOrDefault(Stage.FORWARD, new ArrayList<>()));
        forwardRenderNode.setFilteredParticlesToRender(toRenderParticles);
        deferredRenderNode.setFilteredDecalsToRender(toRenderDecals);

        if (deferredRenderNode instanceof JGemsDeferredRenderNode deferredRenderNode1) {
            deferredRenderNode1.setWorldLiquid(toRenderLiquids);
        }

        JGemsOpenGLRenderer.renderNodeWithEvent(openGLRenderer, frameTicking, deferredRenderNode);
        JGemsOpenGLRenderer.renderNodeWithEvent(openGLRenderer, frameTicking, forwardRenderNode);

        Collection<SceneObject> rejectedIndirect = deferredRenderNode.getRejectedIndirectDeferredRenderingObjects();
        Collection<SceneObject> rejectedDirect = deferredRenderNode.getRejectedDirectDeferredRenderingObjects();
        rejectedDirect.addAll(forwardRenderNode.getRejectedDirectForwardRenderingObjects());
        rejectedDirect.addAll(redirectedInTransparency);

        transparencyRenderNode.setIndirectDeferredRenderingObjects(rejectedIndirect);
        transparencyRenderNode.setDirectDeferredRenderingObjects(rejectedDirect);
        if (transparencyRenderNode instanceof JGemsTransparencyRenderNode transparencyRenderNode1) {
            transparencyRenderNode1.setWorldLiquid(toRenderLiquids);
        }
        JGemsOpenGLRenderer.renderNodeWithEvent(openGLRenderer, frameTicking, transparencyRenderNode);
    }

    protected void renderFinalSceneInMainBuffer(FBOTexture2DProgram finalFBO) {
        JGemsShaderManager imgShader = JGemsResourceManager.globalShaderAssets.gui_image;
        imgShader.beginShading();
        imgShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), finalFBO.getTextureByIndex(0));//finalFBO.getTextureByIndex(0)
        imgShader.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsHelper.render().renderModel2D(this.getScreenModel(), GL46.GL_TRIANGLES);
        imgShader.endShading();
    }

    @Override
    public void onStopRender() {
        EventLauncher.pushEvent(new EventBus.StopRendererOGLEvent(this), new Pair<>(new JSStopRendererOGLEvent(new JSOpenGLRenderer(this)), JavaToJsAPI.Target.Game));
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
        this.getJGemsUI().setUiPanel(panelUI);
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

    public void recreateEnvironment() {
        this.getWorld().getEnvironment().destroyEnvironment();
        this.getWorld().getEnvironment().createEnvironment(this);
    }

    @Override
    public void recreateResources() {
        EventLauncher.pushEvent(new EventBus.ReCreateRenderResourcesEvent(this), null);
        super.recreateResources();
        this.recreateEnvironment();
    }

    @Override
    public void createResources() {
        EventLauncher.pushEvent(new EventBus.CreateRenderResourcesEvent(this), null);
        this.getDebugLinesDrawer().setup();
        this.getConveyorNodes().values().forEach(IRenderNode::createResources);
        this.getSceneCulling().createResources();
    }

    @Override
    public void destroyResources() {
        EventLauncher.pushEvent(new EventBus.DestroyRenderResourcesEvent(this), null);
        this.getDebugLinesDrawer().clear();
        this.getConveyorNodes().values().forEach(IRenderNode::destroyResources);
        this.getSceneCulling().destroyResources();
    }

    @Override
    public void onWindowResize(IWindow window) {
        EventLauncher.pushEvent(new EventBus.ResizeWindowRenderPipelineEvent(this), null);
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
        if (this.getRenderNodeByPass(JGemsOpenGLRenderer.UI_RENDER_PASS) != null) {
            ((IUIRenderNode) this.getRenderNodeByPass(JGemsOpenGLRenderer.UI_RENDER_PASS)).setAnInterface(dearUIInterface);
        }
    }

    @Override
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

    public static JGemsShaderManager UBOShader() {
        return JGemsOpenGLRenderer.UBO_SHADER;
    }
}