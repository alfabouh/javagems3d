package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.IForwardRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.skybox.BackgroundRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.skybox.SkyboxRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.Collection;
import java.util.function.Consumer;

public final class ForwardRenderNode extends IRenderNode.Template implements IForwardRenderNode {
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
        this.getBackgroundRenderProcessor().runProcessorRendering(frameTicking);
        GL46.glDisable(GL46.GL_BLEND);

        this.getOutColorBuffer().bindFBO();
        this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getForwardRenderingObjects());
        this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);

        this.getSkyboxRenderProcessor().setBackgroundTexture(this.getBackgroundRenderProcessor().getBackground().getTextureByIndex(0));
        this.getSkyboxRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutColorBuffer().unBindFBO();

        // GL46.glDisable(GL46.GL_DEPTH_TEST);
        // if (true) {
        //     for (SceneObject sceneObject : this.getSceneWorld().getSceneObjects()) {
        //         CullingAABB cullingAABB = sceneObject.getCullingData();
        //         if (cullingAABB == null) {
        //             continue;
        //         }
        //         JGemsDebugGlobalConstants.linesDebugDraw.drawAABB(DynamicsUtils.convertV3F_JME(cullingAABB.getAabbMin()), DynamicsUtils.convertV3F_JME(cullingAABB.getAabbMax()));
        //     }
        // }
        // GL46.glEnable(GL46.GL_DEPTH_TEST);
    }

    public void initProcessors() {
        final Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandlerD = DeferredRenderNode.getDefaultConsumerForDirectObjects((SceneWorld) this.getWBenchWorld());

        SceneWorld sceneWorld = (SceneWorld) this.getWBenchWorld();
        this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(uniformsHandlerD, Pipeline.SCENE, this.getOpenGLRenderer());
        this.skyboxRenderProcessor = new SkyboxRenderProcessor(sceneWorld.getEnvironment().getSkyBox(), JGemsResourceManager.globalShaderAssets.skybox, JGemsResourceManager.globalModelAssets.defaultCube_gr, this.getOpenGLRenderer());
        this.backgroundRenderProcessor = new BackgroundRenderProcessor(this.getInColorBuffer(), JGemsResourceManager.globalShaderAssets.IndirectBufferData, JGemsResourceManager.globalShaderAssets.PropertiesData, sceneWorld.getEnvironment().getSkyBox(), this.getOpenGLRenderer());
    }

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
}
