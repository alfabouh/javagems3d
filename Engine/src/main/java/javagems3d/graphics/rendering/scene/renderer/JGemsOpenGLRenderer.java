/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.rendering.scene.renderer;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.scene.culling.ISceneCulling;
import javagems3d.graphics.rendering.scene.culling.SceneCulling;
import javagems3d.graphics.rendering.scene.renderer.nodes.*;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.Nodes;
import javagems3d.graphics.rendering.ui.dear_imgui.DearUIRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIGameInterface;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIMenuInterface;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.map.loaders.IMapLoader;

import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.data.cache.MeshBuffersDataCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.util.*;
import java.util.stream.Collectors;

public class JGemsOpenGLRenderer extends OpenGLRenderer implements IResourceInit {
    protected Map<Nodes, IRenderNode> conveyorNodes;

    public static DearUIInterface inGameInterface;
    public static DearUIInterface inMenuInterface;

    protected IndirectBufferProgram sceneIndirectBufferProgram;

    protected JGemsUI jGemsUI;
    protected DearUIRenderer dearUIRenderer;

    protected Model2D screenModel;

    private final ISceneCulling sceneCulling;

    public JGemsOpenGLRenderer(IWindow window, SceneWorld sceneWorld) {
        super(window, sceneWorld);
        this.conveyorNodes = new TreeMap<>(Comparator.comparingInt(Nodes::getId));
        this.initNodes();

        JGemsOpenGLRenderer.inGameInterface = new DearUIGameInterface();
        JGemsOpenGLRenderer.inMenuInterface = new DearUIMenuInterface();

        this.sceneIndirectBufferProgram = new IndirectBufferProgram(DefaultAttributePointers.ATTR_POSITIONS, DefaultAttributePointers.ATTR_NORMALS, DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, DefaultAttributePointers.ATTR_TANGENTS, DefaultAttributePointers.ATTR_BI_TANGENTS);
        this.screenModel = null;

        this.sceneCulling = new SceneCulling(this, null);
    }

    @Override
    public @NotNull Vector2i getRenderingResolution() {
        return this.getWindowSize().div(1.0f);
    }

    protected void initNodes() {
        for (Nodes group : Nodes.values()) {
            this.getConveyorNodes().put(group, null);
        }
    }

    protected void setDefaultNodes() {
        IDeferredRenderNode defaultDeferredNode = new IDeferredRenderNode.Default(new FBOTexture2DProgram(true), this);
        IForwardRenderNode forwardRenderNode = new IForwardRenderNode.Default(defaultDeferredNode.getOutColorBuffer(), this);
        ITransparencyRenderNode transparencyRenderNode = new ITransparencyRenderNode.Default(defaultDeferredNode.getOutColorBuffer(), this);
        IGluingRenderNode gluingRenderNode = new IGluingRenderNode.Default(forwardRenderNode.getOutColorBuffer(), this);
        IPostFXRenderNode postFXRenderNode = new IPostFXRenderNode.Default(gluingRenderNode.getOutColorBuffer(), this);
        IUIRenderNode iuiRenderNode = new IUIRenderNode.Default(this.getJGemsUI(), this);

        this.setDeferredRenderNode(defaultDeferredNode);
        this.setForwardRenderNode(forwardRenderNode);
        this.setTransparencyRenderNode(transparencyRenderNode);
        this.setGluingRenderNode(gluingRenderNode);
        this.setPostFXRenderNode(postFXRenderNode);
        this.setUIRenderNode(iuiRenderNode);
    }

    public void setDeferredRenderNode(@NotNull IDeferredRenderNode node) {
        this.getConveyorNodes().replace(Nodes.DEFERRED_RENDER_PASS, node);
    }

    public void setForwardRenderNode(@NotNull IForwardRenderNode node) {
        this.getConveyorNodes().replace(Nodes.FORWARD_RENDER_PASS, node);
    }

    public void setTransparencyRenderNode(@NotNull ITransparencyRenderNode node) {
        this.getConveyorNodes().replace(Nodes.TRANSPARENCY_RENDER_PASS, node);
    }

    public void setUIRenderNode(@NotNull IUIRenderNode node) {
        this.getConveyorNodes().replace(Nodes.UI_RENDER_PASS, node);
    }

    public void setGluingRenderNode(@NotNull IGluingRenderNode node) {
        this.getConveyorNodes().replace(Nodes.GLUING_RENDER_PASS, node);
    }

    public void setPostFXRenderNode(@NotNull IPostFXRenderNode node) {
        this.getConveyorNodes().replace(Nodes.POST_EFFECTS_RENDER_PASS, node);
    }

    @SuppressWarnings("all")
    public @NotNull <T extends IRenderNode> T getRenderNodeByPass(Nodes node) {
        return (T) this.getConveyorNodes().get(node);
    }

    @Override
    public void onStartRender() {
        this.constructScreenModel();
        this.jGemsUI = new JGemsUI();
        this.dearUIRenderer = new DearUIRenderer(this.getWindow(), JGemsResourceManager.getGlobalGameResources());

        this.setDefaultNodes();
        this.createResources();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        IDeferredRenderNode deferredRenderNode = this.getRenderNodeByPass(Nodes.DEFERRED_RENDER_PASS);
        IForwardRenderNode forwardRenderNode = this.getRenderNodeByPass(Nodes.FORWARD_RENDER_PASS);
        ITransparencyRenderNode transparencyRenderNode = this.getRenderNodeByPass(Nodes.TRANSPARENCY_RENDER_PASS);
        IGluingRenderNode gluingRenderNode = this.getRenderNodeByPass(Nodes.GLUING_RENDER_PASS);
        IPostFXRenderNode postRenderNode = this.getRenderNodeByPass(Nodes.POST_EFFECTS_RENDER_PASS);
        IUIRenderNode uiRenderNode = this.getRenderNodeByPass(Nodes.UI_RENDER_PASS);

        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_STENCIL_BUFFER_BIT);
        OpenGLRenderer.setViewPort(this.getWindowSize());
        if (this.getSceneWorld().getCamera() == null) {
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            uiRenderNode.onRender(frameTicking);
            this.getDearUIRenderer().onRender(JGemsOpenGLRenderer.inMenuInterface, frameTicking);
            return;
        }
        if (JGems3D.get().isPaused()) {
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            uiRenderNode.onRender(frameTicking);
            this.getDearUIRenderer().onRender(JGemsOpenGLRenderer.inGameInterface, frameTicking);
            return;
        }
        this.getSceneWorld().getEnvironment().updateEnvironment(this.getSceneWorld().getCamera());
        OpenGLRenderer.setViewPort(this.getRenderingResolution());

        Set<SceneObject> toRender = new HashSet<>(this.getSceneWorld().getSceneObjects());
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
        gluingRenderNode.onRender(frameTicking);
        postRenderNode.onRender(frameTicking);

        OpenGLRenderer.setViewPort(this.getWindowSize());
        this.renderFinalSceneInMainBuffer(postRenderNode.getOutColorBuffer());
        uiRenderNode.onRender(frameTicking);
        this.getDearUIRenderer().onRender(JGemsOpenGLRenderer.inGameInterface, frameTicking);
    }

    protected void renderFinalSceneInMainBuffer(FBOTexture2DProgram finalFBO) {
        JGemsShaderManager imgShader = JGemsResourceManager.globalShaderAssets.gui_image;
        imgShader.beginShading();
        imgShader.performUniformTexture(new UniformString("texture_sampler"), finalFBO.getTextureByIndex(0));
        imgShader.getUtils().performOrthographicMatrix(this.getScreenModel());
        JGemsHelper.RENDERING.renderModel2D(this.getScreenModel(), GL46.GL_TRIANGLES);
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
        this.getConveyorNodes().clear();
        this.destroyResources();
    }

    protected void constructScreenModel() {
        if (this.screenModel != null) {
            this.screenModel.clear();
        }
        this.screenModel = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getWindowSize()), 0);
    }

    @Override
    public void UIPanelActionRequest(@Nullable PanelUI panelUI) {
        this.getJGemsUI().setPanel(panelUI);
    }

    @Override
    public @NotNull Model2D getScreenModel() {
        return this.screenModel;
    }

    @Override
    public void onMapLoaded(IMapLoader loader, JGemsResourceManager resourceManager) {
        this.initSceneIndirectRenderBuffer(resourceManager.getResourceDataCache().getMeshBuffersDataCache());
        resourceManager.loadMeshMaterialsIsSSBO(JGemsResourceManager.globalShaderAssets.MaterialsData);
        resourceManager.loadBindlessHandlersInSSBO(JGemsResourceManager.globalShaderAssets.BindlessTextures);
    }

    @Override
    public void onMapDestroyed(IMapLoader loader, JGemsResourceManager resourceManager) {
        this.getSceneIndirectBuffer().clear();
    }

    public void initSceneIndirectRenderBuffer(MeshBuffersDataCache meshBuffersDataCache) {
        this.getSceneIndirectBuffer().clear();
        this.getSceneIndirectBuffer().init(meshBuffersDataCache);
    }

    public void createResources() {
        this.getSceneWorld().getEnvironment().createEnvironment(this);
        this.getConveyorNodes().values().forEach(IRenderNode::createResources);
        this.getSceneCulling().createResources();
    }

    public void destroyResources() {
        this.getConveyorNodes().values().forEach(IRenderNode::destroyResources);
        this.getSceneWorld().getEnvironment().destroyEnvironment();
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

    public IndirectBufferProgram getSceneIndirectBuffer() {
        return this.sceneIndirectBufferProgram;
    }

    public JGemsUI getJGemsUI() {
        return this.jGemsUI;
    }

    public DearUIRenderer getDearUIRenderer() {
        return this.dearUIRenderer;
    }

    public Map<Nodes, IRenderNode> getConveyorNodes() {
        return this.conveyorNodes;
    }

    @Override
    public ISceneCulling getSceneCulling() {
        return this.sceneCulling;
    }

    public static JGemsShaderManager UBOShader() {
        return JGemsResourceManager.globalShaderAssets.gameUbo;
    }

    public static JGemsShaderManager SkyBoxShader() {
        return JGemsResourceManager.globalShaderAssets.skybox;
    }
}