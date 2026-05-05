package javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IForwardRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.skybox.BackgroundRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.skybox.SkyboxRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class ForwardRenderNode extends IRenderNode.Template implements IForwardRenderNode {
    private Collection<SceneObject> forwardRenderingObjects;
    private DirectGeometryRenderProcessor directGeometryRenderProcessor;
    private SkyboxRenderProcessor skyboxRenderProcessor;
    private BackgroundRenderProcessor backgroundRenderProcessor;
    private final FBOTexture2DProgram inColor;

    public ForwardRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.inColor = inColor;
    }

    @Override
    public FBOTexture2DProgram getInColorBuffer() {
        return this.inColor;
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return this.getInColorBuffer();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        if (JGemsConfig.DEBUG.WIREFRAME_RENDERING) {
            GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_LINE);
        }
        this.getBackgroundRenderProcessor().setRender(this.renderBackground());
        this.getBackgroundRenderProcessor().runProcessorRendering(frameTicking);
        if (JGemsConfig.DEBUG.WIREFRAME_RENDERING) {
            GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_FILL);
        }
        GL46.glDisable(GL46.GL_BLEND);

        this.getOutColorBuffer().bindFBO();
        if (!EventLauncher.pushEvent(new EventBus.ForwardOGLRenderInMainFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.PRE), null).isCancelled()) {
            this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getForwardRenderingObjects());
            this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);

            this.getWorld().getEnvironment().getParticlesScene().passObjectInMainSceneSSBO();
            this.getWorld().getEnvironment().getParticlesScene().getParticlesIndirectRendererScene().processAndRender(ArbitraryArguments.pass(this.getWorld().getEnvironment().getParticlesScene().getDefaultConsumerForParticlesScene()));

            this.getSkyboxRenderProcessor().setBackgroundTexture(this.getBackgroundRenderProcessor().getBackground().getTextureByIndex(0));
            this.getSkyboxRenderProcessor().runProcessorRendering(frameTicking);
            EventLauncher.pushEvent(new EventBus.ForwardOGLRenderInMainFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.POST), null);
        }
        this.getOutColorBuffer().unBindFBO();
    }

    public void initProcessors() {
        final Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandlerD = DeferredRenderNode.getDefaultConsumerForDirectObjects(this.getWorld());
        this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(uniformsHandlerD, Pipeline.SCENE, this.getOpenGLRenderer());
        this.skyboxRenderProcessor = new SkyboxRenderProcessor(this.getWorld().getEnvironment().getSkyBox(), this.getSkyBoxShader(), this.getCube(), this.getOpenGLRenderer());
        this.backgroundRenderProcessor = new BackgroundRenderProcessor(this.getInColorBuffer(), this.getIndirectBufferData(), this.getPropertiesData(), this.getWorld().getEnvironment().getSkyBox(), this.getOpenGLRenderer());
    }

    public abstract @NotNull ShaderStorageBufferObject getIndirectBufferData();
    public abstract @NotNull ShaderStorageBufferObject getPropertiesData();
    public abstract @NotNull MeshGroup getCube();
    public abstract @NotNull JGemsShaderManager getSkyBoxShader();
    public abstract boolean renderBackground();

    public void initFBOs() {
    }

    @Override
    public void createResources() {
        this.initFBOs();
        this.initProcessors();

        this.getDirectGeometryRenderProcessor().createResources();
        this.getSkyboxRenderProcessor().createResources();
        this.getBackgroundRenderProcessor().createResources();
    }

    @Override
    public void destroyResources() {
        this.getDirectGeometryRenderProcessor().destroyResources();
        this.getSkyboxRenderProcessor().destroyResources();
        this.getBackgroundRenderProcessor().destroyResources();
    }

    @Override
    public void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects) {
        this.forwardRenderingObjects = forwardRenderingObjects;
    }

    public BackgroundRenderProcessor getBackgroundRenderProcessor() {
        return this.backgroundRenderProcessor;
    }

    public SkyboxRenderProcessor getSkyboxRenderProcessor() {
        return this.skyboxRenderProcessor;
    }

    public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
        return this.directGeometryRenderProcessor;
    }

    @Override
    public Collection<SceneObject> getForwardRenderingObjects() {
        return this.forwardRenderingObjects;
    }

    @Override
    public Collection<SceneObject> getRejectedDirectForwardRenderingObjects() {
        return this.getDirectGeometryRenderProcessor().getRejected();
    }

    @Override
    public NodeID getNodeID() {
        return JGemsOpenGLRenderer.FORWARD_RENDER_PASS;
    }
}
