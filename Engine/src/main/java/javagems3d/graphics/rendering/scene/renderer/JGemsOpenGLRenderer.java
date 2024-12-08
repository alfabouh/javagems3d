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
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.shadows.ShadowScene;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.ObjectRenderConfiguration;
import javagems3d.graphics.rendering.scene.buffers.IndirectRenderBuffer;
import javagems3d.graphics.rendering.scene.renderer.nodes.*;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.predefined.*;
import javagems3d.graphics.rendering.ui.dear_imgui.DearUIRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIGameInterface;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIMenuInterface;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.Transformation;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.manager.JGemsResourceManager;
import javagems3d.system.resources.manager.mesh.MeshBuffersDrawCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;

import java.util.*;
import java.util.stream.Collectors;

public class JGemsOpenGLRenderer extends OpenGLRenderer implements IResourceInit {
    private final Map<Nodes, IRenderNode> conveyorNodes;

    public static DearUIInterface inGameInterface;
    public static DearUIInterface inMenuInterface;

    private final IndirectRenderBuffer sceneIndirectRenderBuffer;

    protected JGemsUI jGemsUI;
    protected DearUIRenderer dearUIRenderer;

    public JGemsOpenGLRenderer(IWindow window, SceneWorld sceneWorld, Transformation transformation) {
        super(window, sceneWorld, transformation);
        this.conveyorNodes = new TreeMap<>(Comparator.comparingInt(Nodes::getId));
        this.initNodes();

        JGemsOpenGLRenderer.inGameInterface = new DearUIGameInterface();
        JGemsOpenGLRenderer.inMenuInterface = new DearUIMenuInterface();

        this.sceneIndirectRenderBuffer = new IndirectRenderBuffer(DefaultAttributePointers.ATTR_POSITIONS, DefaultAttributePointers.ATTR_NORMALS, DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, DefaultAttributePointers.ATTR_TANGENTS, DefaultAttributePointers.ATTR_BI_TANGENTS);
    }

    protected void initNodes() {
        for (Nodes group : Nodes.values()) {
            this.getConveyorNodes().put(group, null);
        }
    }

    protected void setDefaults() {
        this.setDeferredRenderNode(new IDeferredRenderNode.Default(this));
        this.setForwardRenderNode(new IForwardRenderNode.Default(this));
        this.setGluingRenderNode(new IGluingRenderNode.Default(this));
        this.setTransparencyRenderNode(new ITransparencyRenderNode.Default(this));
        this.setPostFXRenderNode(new IPostFXRenderNode.Default(this));
        this.setUIRenderNode(new IUIRenderNode.Default(this.getJGemsUI(), this));
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

    @Override
    public void onStartRender() {
        this.jGemsUI = new JGemsUI();
        this.dearUIRenderer = new DearUIRenderer(this.getWindow(), JGemsResourceManager.getGlobalGameResources().getResourceCache());
        this.setDefaults();
        this.createResources();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        IDeferredRenderNode deferredRenderNode = (IDeferredRenderNode) this.getConveyorNodes().get(Nodes.DEFERRED_RENDER_PASS);
        IForwardRenderNode forwardRenderNode = (IForwardRenderNode) this.getConveyorNodes().get(Nodes.FORWARD_RENDER_PASS);
        ITransparencyRenderNode transparencyRenderNode = (ITransparencyRenderNode) this.getConveyorNodes().get(Nodes.TRANSPARENCY_RENDER_PASS);
        IGluingRenderNode gluingRenderNode = (IGluingRenderNode) this.getConveyorNodes().get(Nodes.GLUING_RENDER_PASS);
        IPostFXRenderNode postRenderNode = (IPostFXRenderNode) this.getConveyorNodes().get(Nodes.POST_EFFECTS_RENDER_PASS);
        IUIRenderNode uiRenderNode = (IUIRenderNode) this.getConveyorNodes().get(Nodes.UI_RENDER_PASS);

        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_STENCIL_BUFFER_BIT);
        //this.getSceneWorld().getEnvironment().updateEnvironment(this.getSceneWorld(), this.getSceneWorld().getCamera());
        JGems3D.get().getScreen().normalizeViewPort();

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

        deferredRenderNode.onRender(frameTicking);
        //forwardRenderNode.onRender(frameTicking);
        //transparencyRenderNode.onRender(frameTicking);
        uiRenderNode.onRender(frameTicking);
       //gluingRenderNode.onRender(frameTicking);
       //postRenderNode.onRender(frameTicking);

        this.getDearUIRenderer().onRender(JGemsOpenGLRenderer.inGameInterface, frameTicking);
    }

    @Override
    public void onStopRender() {
        this.getSceneIndirectBuffer().clear();
        this.getJGemsUI().destroyUI();
        this.getDearUIRenderer().destroyUI();
        this.getConveyorNodes().clear();
        this.destroyResources();
    }

    @Override
    public void UIPanelActionRequest(@Nullable PanelUI panelUI) {
        this.getJGemsUI().setPanel(panelUI);
    }

    @Override
    public void onMapLoaded(IMapLoader loader, JGemsResourceManager resourceManager) {
        resourceManager.constructMeshBuffersDataCache();
        this.initSceneIndirectRenderBuffer(resourceManager.getMeshBuffersDrawCache());
    }

    @Override
    public void onMapDestroyed(IMapLoader loader, JGemsResourceManager resourceManager) {
        this.getSceneIndirectBuffer().clear();
    }

    public void initSceneIndirectRenderBuffer(MeshBuffersDrawCache meshBuffersDrawCache) {
        this.getSceneIndirectBuffer().clear();
        this.getSceneIndirectBuffer().init(meshBuffersDrawCache);
    }

    public void createResources() {
        this.getConveyorNodes().values().forEach(IRenderNode::createResources);
    }

    public void destroyResources() {
        this.getConveyorNodes().values().forEach(IRenderNode::destroyResources);
    }

    @Override
    public void onWindowResize(IWindow window) {
        if (this.getJGemsUI() != null) {
            this.getJGemsUI().onWindowResize(window);
        }
        if (this.getDearUIRenderer() != null) {
            this.getDearUIRenderer().onWindowResize(window);
        }
        this.getConveyorNodes().values().stream().filter(Objects::nonNull).forEach(e -> e.onWindowResize(window));
    }

    public IndirectRenderBuffer getSceneIndirectBuffer() {
        return this.sceneIndirectRenderBuffer;
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

    public static JGemsShaderManager UBOShader() {
        return JGemsResourceManager.globalShaderAssets.gameUbo;
    }

    @SuppressWarnings("all")
    public static Set<SceneObject> getFilteredSetToRender(Set<SceneObject> sceneObjects) {
        return sceneObjects.stream().filter(e -> {
            if (!e.isVisible() || !e.hasRender()) {
                return false;
            }
            if (JGemsOpenGLRenderer.checkReachedRenderDistance(e)) {
                return false;
            }
            return true;
        }).collect(Collectors.toSet());
    }

    public static boolean checkReachedRenderDistance(SceneObject renderObject) {
        ICamera camera = JGems3D.get().getScreen().getCamera();
        ObjectRenderConfiguration objectRenderConfiguration = renderObject.getObjectRenderConfiguration();
        return objectRenderConfiguration.getRenderDistance() >= 0 && camera.getCamPosition().distance(renderObject.getModel().getFormat().getPosition()) > objectRenderConfiguration.getRenderDistance();
    }
}