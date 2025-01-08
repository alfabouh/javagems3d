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
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.ObjectRenderConfiguration;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.programs.indirect.IndirectRenderBufferProgram;
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
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.assets.texturing.ext.IBindlessTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.data.cache.BindlessTexturesDataCache;
import javagems3d.system.resources.managing.resources.data.cache.MeshBuffersDataCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class JGemsOpenGLRenderer extends OpenGLRenderer implements IResourceInit {
    protected Map<Nodes, IRenderNode> conveyorNodes;

    public static DearUIInterface inGameInterface;
    public static DearUIInterface inMenuInterface;

    protected IndirectRenderBufferProgram sceneIndirectRenderBufferProgram;

    protected JGemsUI jGemsUI;
    protected DearUIRenderer dearUIRenderer;

    protected Model<Format2D> sceenModel;

    public JGemsOpenGLRenderer(IWindow window, SceneWorld sceneWorld, Transformation transformation) {
        super(window, sceneWorld, transformation);
        this.conveyorNodes = new TreeMap<>(Comparator.comparingInt(Nodes::getId));
        this.initNodes();

        JGemsOpenGLRenderer.inGameInterface = new DearUIGameInterface();
        JGemsOpenGLRenderer.inMenuInterface = new DearUIMenuInterface();

        this.sceneIndirectRenderBufferProgram = new IndirectRenderBufferProgram(DefaultAttributePointers.ATTR_POSITIONS, DefaultAttributePointers.ATTR_NORMALS, DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, DefaultAttributePointers.ATTR_TANGENTS, DefaultAttributePointers.ATTR_BI_TANGENTS);
        this.sceenModel = null;
    }

    @Override
    public @NotNull Vector2i getRenderingResolution() {
        return this.getWindowSize();
    }

    protected void initNodes() {
        for (Nodes group : Nodes.values()) {
            this.getConveyorNodes().put(group, null);
        }
    }

    protected void setDefaults() {
        IDeferredRenderNode defaultDeferredNode = new IDeferredRenderNode.Default(this);

        this.setDeferredRenderNode(defaultDeferredNode);
        this.setForwardRenderNode(new IForwardRenderNode.Default(this));
        this.setGluingRenderNode(new IGluingRenderNode.Default(defaultDeferredNode, this));
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

    @SuppressWarnings("all")
    public @NotNull <T extends IRenderNode> T getRenderNodeByPass(Nodes node) {
        return (T) this.getConveyorNodes().get(node);
    }

    @Override
    public void onStartRender() {
        this.constructScreenModel();
        this.jGemsUI = new JGemsUI();
        this.dearUIRenderer = new DearUIRenderer(this.getWindow(), JGemsResourceManager.getGlobalGameResources());
        this.setDefaults();
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
        this.getSceneWorld().getEnvironment().updateEnvironment(this.getSceneWorld(), this.getSceneWorld().getCamera());
        JGems3D.get().getScreen().normalizeViewPort();
        deferredRenderNode.onRender(frameTicking);
        //forwardRenderNode.onRender(frameTicking);
        //transparencyRenderNode.onRender(frameTicking);
        gluingRenderNode.onRender(frameTicking);
        uiRenderNode.onRender(frameTicking);
       //postRenderNode.onRender(frameTicking);

        this.renderFinalSceneInMainBuffer(gluingRenderNode.getOutGluedScene());
        this.getDearUIRenderer().onRender(JGemsOpenGLRenderer.inGameInterface, frameTicking);
    }

    protected void renderFinalSceneInMainBuffer(FBOTexture2DProgram finalFBO) {
        JGemsShaderManager imgShader = JGemsResourceManager.globalShaderAssets.gui_image;
        imgShader.beginShading();
        imgShader.performUniformTexture(new UniformString("texture_sampler"), finalFBO.getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        imgShader.getUtils().performOrthographicMatrix(this.getScreenModel());
        JGemsHelper.RENDERING.renderModel(this.getScreenModel(), GL46.GL_TRIANGLES);
        imgShader.endShading();
    }

    @Override
    public void onStopRender() {
        if (this.sceenModel != null) {
            this.sceenModel.clear();
        }
        this.getSceneIndirectBuffer().clear();
        this.getJGemsUI().destroyUI();
        this.getDearUIRenderer().destroyUI();
        this.getConveyorNodes().clear();
        this.destroyResources();
    }

    protected void constructScreenModel() {
        if (this.sceenModel != null) {
            this.sceenModel.clear();
        }
        this.sceenModel = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getRenderingResolution()), 0);
    }

    @Override
    public void UIPanelActionRequest(@Nullable PanelUI panelUI) {
        this.getJGemsUI().setPanel(panelUI);
    }

    @Override
    public @NotNull Model<Format2D> getScreenModel() {
        return this.sceenModel;
    }

    @Override
    public void onMapLoaded(IMapLoader loader, JGemsResourceManager resourceManager) {
        this.loadMeshMaterialsIsSSBO(resourceManager.getResourceDataCache().getBindlessTexturesCache(), resourceManager.getResourceDataCache().getMeshBuffersDataCache());
        this.initSceneIndirectRenderBuffer(resourceManager.getResourceDataCache().getMeshBuffersDataCache());
        this.loadBindlessHandlersInSSBO(resourceManager.getResourceDataCache().getBindlessTexturesCache(), JGemsResourceManager.globalShaderAssets.BindlessTextures);
    }

    @Override
    public void onMapDestroyed(IMapLoader loader, JGemsResourceManager resourceManager) {
        this.getSceneIndirectBuffer().clear();
    }

    public void loadBindlessHandlersInSSBO(BindlessTexturesDataCache bindlessTexturesDataCache, ShaderStorageBufferObject shaderStorageBufferObject) {
        LongBuffer longBuffer = MemoryUtil.memAllocLong(JGemsGlobalConfiguration.MAX_BINDLESS_TEXTURES);
        for (IBindlessTexture l : bindlessTexturesDataCache.getBindlessTexturesIdMap().keySet()) {
            longBuffer.put(l.getBindingHandler());
        }
        longBuffer.flip();
        ShaderStorageBufferProgram.fillSSBOWithData(shaderStorageBufferObject, 0L, longBuffer);
        MemoryUtil.memFree(longBuffer);
    }

    public void loadMeshMaterialsIsSSBO(BindlessTexturesDataCache bindlessTexturesDataCache, MeshBuffersDataCache meshBuffersDataCache) {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(Float.BYTES * JGemsGlobalConfiguration.INDIRECT_RENDERING_MATERIALS_PACK_SIZE * JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_MATERIALS);
        for (Material material : meshBuffersDataCache.getMaterials()) {
            ISample diffuse = material.getDiffuse();
            ISample normals = material.getNormalsMap();
            ISample emission = material.getEmissionMap();
            ISample specular = material.getSpecularMap();
            ISample metallic = material.getMetallicMap();
            if (diffuse instanceof RGBAColor) {
                RGBAColor rgbaColor = (RGBAColor) diffuse;
                byteBuffer.putFloat(rgbaColor.getColor().x);
                byteBuffer.putFloat(rgbaColor.getColor().y);
                byteBuffer.putFloat(rgbaColor.getColor().z);
                byteBuffer.putFloat(rgbaColor.getColor().w);
            } else {
                byteBuffer.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
            }
            byteBuffer.putInt(diffuse instanceof IBindlessTexture ? bindlessTexturesDataCache.getTextureId((IBindlessTexture) diffuse) : 0);
            byteBuffer.putInt(normals instanceof IBindlessTexture ? bindlessTexturesDataCache.getTextureId((IBindlessTexture) normals) : 0);
            byteBuffer.putInt(emission instanceof IBindlessTexture ? bindlessTexturesDataCache.getTextureId((IBindlessTexture) emission) : 0);
            byteBuffer.putInt(specular instanceof IBindlessTexture ? bindlessTexturesDataCache.getTextureId((IBindlessTexture) specular) : 0);
            byteBuffer.putInt(metallic instanceof IBindlessTexture ? bindlessTexturesDataCache.getTextureId((IBindlessTexture) metallic) : 0);
            byteBuffer.putInt(JGemsHelper.RENDERING.getTexturingCodeForShader(material));
            byteBuffer.putInt(0);
            byteBuffer.putInt(0);
        }
        byteBuffer.flip();
        ShaderStorageBufferProgram.fillSSBOWithData(JGemsResourceManager.globalShaderAssets.MaterialsData, 0L, byteBuffer);
        MemoryUtil.memFree(byteBuffer);
    }

    public void initSceneIndirectRenderBuffer(MeshBuffersDataCache meshBuffersDataCache) {
        this.getSceneIndirectBuffer().clear();
        this.getSceneIndirectBuffer().init(meshBuffersDataCache);
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
        this.constructScreenModel();
        this.getConveyorNodes().values().stream().filter(Objects::nonNull).forEach(e -> e.onWindowResize(window));
    }

    public IndirectRenderBufferProgram getSceneIndirectBuffer() {
        return this.sceneIndirectRenderBufferProgram;
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
            if (!e.isVisible() || !e.hasRender() || !e.hasModel()) {
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