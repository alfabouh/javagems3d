package javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions;

import api.events.EventBus;
import api.events.EventLauncher;
import api.scripting.JavaToJsAPI;
import api.scripting.coding.env.internal.game.init.events.rendering.ogl.JSRenderNode;
import api.scripting.coding.env.internal.game.init.events.rendering.ogl.JSRenderOGLNodeEvent;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.misc.JSFrameTicking;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.ITransparencyRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.IndirectGeometryRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public abstract class TransparencyRenderNode extends IRenderNode.Template implements ITransparencyRenderNode {
    private final FBOTexture2DProgram outColor;
    private final FBOTexture2DProgram inColor;

    private Collection<SceneObject> indirectDeferredRenderingObjects;
    private Collection<SceneObject> directDeferredRenderingObjects;

    private DirectGeometryRenderProcessor directGeometryRenderProcessor;
    private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;

    private boolean renderParticles;

    public TransparencyRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.indirectDeferredRenderingObjects = new HashSet<>();
        this.directDeferredRenderingObjects = new HashSet<>();
        this.inColor = inColor;
        this.outColor = new FBOTexture2DProgram(true, false);
        this.renderParticles = true;
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        //boolean oldV = GL46.glIsEnabled(GL46.GL_CULL_FACE);
        //GL46.glDisable(GL46.GL_CULL_FACE);
        this.getInColorBuffer().copyFBOtoFBODepth(this.getOutColorBuffer().getFrameBufferId(), this.getRenderingResolution());

        GL46.glDepthMask(false);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunci(0, GL46.GL_ONE, GL46.GL_ONE);
        GL46.glBlendFunci(1, GL46.GL_ZERO, GL46.GL_ONE_MINUS_SRC_COLOR);
        GL46.glBlendFunci(2, GL46.GL_ONE, GL46.GL_ONE);
        GL46.glBlendEquation(GL46.GL_FUNC_ADD);

        this.getOutColorBuffer().bindFBO();
        GL46.glClearBufferfv(GL46.GL_COLOR, 0, new float[] { 0.0f, 0.0f, 0.0f, 0.0f });
        GL46.glClearBufferfv(GL46.GL_COLOR, 1, new float[] { 1.0f, 1.0f, 1.0f, 1.0f });
        GL46.glClearBufferfv(GL46.GL_COLOR, 2, new float[] { 0.0f, 0.0f, 0.0f, 0.0f });
        if (!EventLauncher.pushEvent(new EventBus.TransparencyOGLRenderInMainFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.PRE), null).isCancelled()) {
            this.renderContent(frameTicking);
            EventLauncher.pushEvent(new EventBus.TransparencyOGLRenderInMainFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.POST), null);
        }
        this.getOutColorBuffer().unBindFBO();

        GL46.glDisable(GL46.GL_BLEND);
        GL46.glDepthMask(true);
        //if (oldV) {
        //    GL46.glEnable(GL46.GL_CULL_FACE);
        //}
    }

    protected void renderContent(FrameTicking frameTicking) {
        this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.getIndirectDeferredRenderingObjects());
        this.getIndirectGeometryRenderProcessor().runProcessorRendering(frameTicking);

        this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getDirectDeferredRenderingObjects());
        this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);

        if (this.renderParticles) {
            this.getWorld().getEnvironment().getParticlesScene().passObjectInTransparencySSBO();
            this.getWorld().getEnvironment().getParticlesScene().getParticlesIndirectRendererTransparency().processAndRender(ArbitraryArguments.pass(this.getWorld().getEnvironment().getParticlesScene().getDefaultConsumerForParticlesScene()));
        }
    }

    @Override
    public void createResources() {
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA16F, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_R8, GL46.GL_RED);
            add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA16F, GL46.GL_RGBA);
        }};
        this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        final Consumer<JGemsShaderManager> uniformsHandler = (shaderManager) -> {
            final ICamera camera = this.getOpenGLRenderer().getCamera();
            final Matrix4f cameraMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
            final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
            final ICubeMapProgram cubeMapProgram = this.getWorld().getEnvironment().getSkyBox().getTexture();

            shaderManager.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.CAMERA_POS), UniformFunctions.VEC3F(camera.getCamPosition()));
            if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP))) {
                shaderManager.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP), cubeMapProgram);
                if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP))) {
                    shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP), UniformFunctions.BOOLEAN(true));
                }
            } else {
                if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP))) {
                    shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP), UniformFunctions.BOOLEAN(false));
                }
            }
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), UniformFunctions.MAT4F(projection));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), UniformFunctions.MAT4F(cameraMatrix));
            JGemsHelper.render().performShadowsInfo(this.getWorld().getEnvironment(), shaderManager);
        };
        final Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandlerD = DeferredRenderNode.getDefaultConsumerForDirectObjects(this.getWorld());

        this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(uniformsHandlerD, Pipeline.TRANSPARENCY, this.getOpenGLRenderer());
        this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(uniformsHandler, this.getIndirectBufferData(), this.getPropertiesData(), Pipeline.TRANSPARENCY, this.getOpenGLRenderer());
        this.getDirectGeometryRenderProcessor().createResources();
        this.getIndirectGeometryRenderProcessor().createResources();
    }

    public abstract @NotNull ShaderStorageBufferObject getIndirectBufferData();
    public abstract @NotNull ShaderStorageBufferObject getPropertiesData();

    @Override
    public void destroyResources() {
        this.getDirectGeometryRenderProcessor().destroyResources();
        this.getIndirectGeometryRenderProcessor().destroyResources();
        if (this.getOutColorBuffer() != null) {
            this.getOutColorBuffer().clearFBO();
        }
    }

    public boolean isRenderParticles() {
        return this.renderParticles;
    }

    public TransparencyRenderNode setRenderParticles(boolean renderParticles) {
        this.renderParticles = renderParticles;
        return this;
    }

    public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
        return this.directGeometryRenderProcessor;
    }

    public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
        return this.indirectGeometryRenderProcessor;
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return this.outColor;
    }

    @Override
    public FBOTexture2DProgram getInColorBuffer() {
        return this.inColor;
    }

    public void setIndirectDeferredRenderingObjects(@NotNull Collection<SceneObject> indirectDeferredRenderingObjects) {
        this.indirectDeferredRenderingObjects = indirectDeferredRenderingObjects;
    }

    public void setDirectDeferredRenderingObjects(@NotNull Collection<SceneObject> directDeferredRenderingObjects) {
        this.directDeferredRenderingObjects = directDeferredRenderingObjects;
    }

    public Collection<SceneObject> getIndirectDeferredRenderingObjects() {
        return this.indirectDeferredRenderingObjects;
    }

    public Collection<SceneObject> getDirectDeferredRenderingObjects() {
        return this.directDeferredRenderingObjects;
    }

    @Override
    public NodeID getNodeID() {
        return JGemsOpenGLRenderer.TRANSPARENCY_RENDER_PASS;
    }
}
