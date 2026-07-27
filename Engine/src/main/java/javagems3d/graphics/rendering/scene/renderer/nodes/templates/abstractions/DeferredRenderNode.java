/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IDeferredRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.IndirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.DeferredColorRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.DeferredLightRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.SSAORenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class DeferredRenderNode extends IRenderNode.Template implements IDeferredRenderNode {
    private final FBOTexture2DProgram startColorFBO;
    private FBOTexture2DProgram gBuffer;
    private FBOTexture2DProgram lightBuffer;
    private FBOTexture2DProgram ssaoBuffer;

    private DirectGeometryRenderProcessor directGeometryRenderProcessor;
    private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
    private SSAORenderProcessor ssaoRenderProcessor;
    private DeferredColorRenderProcessor deferredColorRenderProcessor;
    private DeferredLightRenderProcessor deferredLightRenderProcessor;

    private Collection<SceneObject> indirectDeferredRenderingObjects;
    private Collection<SceneObject> directDeferredRenderingObjects;
    private Collection<DecalFX> filteredDecalsToRender;

    public DeferredRenderNode(@NotNull FBOTexture2DProgram startColorFbo, OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.indirectDeferredRenderingObjects = new HashSet<>();
        this.directDeferredRenderingObjects = new HashSet<>();
        this.startColorFBO = startColorFbo;
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return this.startColorFBO;
    }

    public FBOTexture2DProgram getLightBuffer() {
        return this.lightBuffer;
    }

    public FBOTexture2DProgram getOutGBuffer() {
        return this.gBuffer;
    }

    @Override
    public @Nullable FBOTexture2DProgram getOutSSAOBuffer() {
        return this.ssaoBuffer;
    }

    protected void renderInGBuffer() {
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        //for (SceneObject sceneObject : this.getIndirectDeferredRenderingObjects()) {
        //    CullingAABB cullingAABB = sceneObject.pickAABBDataFromMesh();
        //    if (cullingAABB != null) {
        //        JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
        //    }
        //}

        this.getOutGBuffer().bindFBO();
        //GL46.glDisable(GL46.GL_DITHER);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        if (JGemsConfig.DEBUG.WIREFRAME_RENDERING) {
            GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_LINE);
        }
        if (!EventLauncher.pushEvent(new EventBus.DeferredOGLRenderInMainFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.PRE), null).isCancelled()) {
            this.renderInGBuffer();

            this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.getIndirectDeferredRenderingObjects());
            this.getIndirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
//MODEL MATRIX PROBLEM
            this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getDirectDeferredRenderingObjects());
            this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
            EventLauncher.pushEvent(new EventBus.DeferredOGLRenderInMainFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.POST), null);
        }
        if (JGemsConfig.DEBUG.WIREFRAME_RENDERING) {
            GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_FILL);
        }
       // GL46.glEnable(GL46.GL_DITHER);
        this.getOutGBuffer().unBindFBO();

        if (this.getOutSSAOBuffer() != null) {
            this.getOutSSAOBuffer().bindFBO();
            this.getSSAORenderProcessor().runProcessorRendering(frameTicking);
            this.getOutSSAOBuffer().unBindFBO();
        }

        if (!JGemsConfig.DEBUG.DISABLE_DECALS) {
            if (this.filteredDecalsToRender != null) {
                GL46.glDepthMask(false);
                this.getOutGBuffer().bindFBO();
                //GL46.glDisable(GL46.GL_DITHER);
                {
                    //GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT5, GL46.GL_TEXTURE_2D, 0, 0);
                    GL46.glEnable(GL46.GL_BLEND);
                    GL46.glBlendFunci(2, GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
                    GL46.glBlendFunci(3, GL46.GL_ONE, GL46.GL_ONE);
                    this.getDeferredColorRenderProcessor().getDeferredDecalsRenderProcessor().renderDecals(this.filteredDecalsToRender, this.getOpenGLRenderer().getWorld().getEnvironment());
                    GL46.glDisable(GL46.GL_BLEND);
                    //GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT5, GL46.GL_TEXTURE_2D, this.getOutGBuffer().getTextureIDByIndex(5), 0);
                }
               // GL46.glEnable(GL46.GL_DITHER);
                this.getOutGBuffer().unBindFBO();
                GL46.glDepthMask(true);
            }
        }

        this.getLightBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendEquation(GL46.GL_FUNC_ADD);
        GL46.glBlendFunc(GL46.GL_ONE, GL46.GL_ONE);
        this.getDeferredLightRenderProcessor().runProcessorRendering(frameTicking);
        GL46.glDisable(GL46.GL_BLEND);
        this.getLightBuffer().unBindFBO();

        this.getOutColorBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.getDeferredColorRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutColorBuffer().unBindFBO();

        this.getOutGBuffer().copyFBOtoFBODepth(this.getOutColorBuffer().getFrameBufferId(), this.getRenderingResolution());
    }

    public DeferredRenderNode setFilteredDecalsToRender(Collection<DecalFX> filteredDecalsToRender) {
        this.filteredDecalsToRender = filteredDecalsToRender;
        return this;
    }

    public void initFBOs() {
        this.lightBuffer = new FBOTexture2DProgram(true, false);
        this.gBuffer = new FBOTexture2DProgram(true, false);
        if (this.useSsao()) {
            this.ssaoBuffer = new FBOTexture2DProgram(true, false);
        }

        T2DAttachmentContainer gBuffer = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT3, GL46.GL_RGB16F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT4, GL46.GL_RG, GL46.GL_RG);
            add(GL46.GL_COLOR_ATTACHMENT5, GL46.GL_R16, GL46.GL_RED);
        }};
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
        }};
        T2DAttachmentContainer light = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
        }};
        this.getOutGBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), gBuffer, true, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        this.getLightBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), light, false, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        if (this.getOutSSAOBuffer() != null) {
            T2DAttachmentContainer ssao = new T2DAttachmentContainer() {{
                add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_R16F, GL46.GL_RED);
            }};
            this.getOutSSAOBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), ssao, false, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        }
        this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, true, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    public static Consumer<Pair<JGemsShaderManager, IRendered>> getDefaultConsumerForDirectObjects(Supplier<ITexture2DProgram> animations, IRenderWorld renderWorld) {
        return (pair) -> {
            pair.first().disableWarns();
            pair.first().performUniformTexture(new UniformString(DefaultUniformDefinitions.ANIMATIONS_MATRIX), animations.get());
            pair.first().enableWarns();
            JGemsHelper.render().performDefaultModelMaterialOnShader(renderWorld.getEnvironment(), pair.first(), new Material(new Color4Texture(1.0f, 1.0f, 1.0f)),
                    pair.second().getRenderAttributes().getProperties().getFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, JGemsConfig.SYSTEM.MAX_ALPHA_TO_DISCARD_SHADOW_FRAGMENT),
                    pair.second().getRenderAttributes().getProperties().getInt(JGemsRenderProperties.KEY_GBUFFER_DECAL_LAYER_ID, 0)
            );
        };
    }

    @Override
    public void createResources() {
        this.initFBOs();
        this.createProcessorInstances();

        if (this.getSSAORenderProcessor() != null) {
            this.getSSAORenderProcessor().createResources();
        }
        this.getDirectGeometryRenderProcessor().createResources();
        this.getIndirectGeometryRenderProcessor().createResources();
        this.getDeferredColorRenderProcessor().createResources();
        this.getDeferredLightRenderProcessor().createResources();
    }

    protected void destroyProcessorInstances() {
        this.directGeometryRenderProcessor = null;
        this.indirectGeometryRenderProcessor = null;
        this.ssaoRenderProcessor = null;
        this.deferredColorRenderProcessor = null;
        this.deferredLightRenderProcessor = null;
    }

    protected void createProcessorInstances() {
        final Consumer<JGemsShaderManager> uniformsHandlerI = (shaderManager) -> {
            final ICamera camera = this.getOpenGLRenderer().getCamera();
            final Matrix4f cameraMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
            final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
            final ICubeMapProgram cubeMapProgram = this.getWorld().getEnvironment().getSkyBox().getTexture();
            shaderManager.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.CAMERA_POS), UniformFunctions.VEC3F(camera.getCamPosition()));
            if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP))) {
                shaderManager.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP), cubeMapProgram);
            }
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), UniformFunctions.MAT4F(projection));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), UniformFunctions.MAT4F(cameraMatrix));
            shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.ANIMATIONS_MATRIX), this.getAnimationsTexture());
        };

        final Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandlerD = DeferredRenderNode.getDefaultConsumerForDirectObjects(this::getAnimationsTexture, this.getWorld());
        {
            this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(uniformsHandlerD, Pipeline.SOLID_SCENE, this.getOpenGLRenderer());
            this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(uniformsHandlerI, this.getIndirectBufferData(), this.getPropertiesData(), Pipeline.SOLID_SCENE, this.getOpenGLRenderer());
            if (this.getOutSSAOBuffer() != null && this.getSsaoShader() != null) {
                this.ssaoRenderProcessor = new SSAORenderProcessor(this.getOpenGLRenderer(), this.getOutGBuffer(), this.getSsaoShader(), this.getSsaoBlurring());
            }
            this.deferredColorRenderProcessor = new DeferredColorRenderProcessor(this.getOpenGLRenderer(), this.getDeferredDecalsShader(), this.getOutGBuffer(), this.getLightBuffer(), this.getOutSSAOBuffer(), getDeferredColorRendererShader());
            this.deferredLightRenderProcessor = new DeferredLightRenderProcessor(this.getOpenGLRenderer(), this.getOutGBuffer(), this.getDeferredLightRendererShaders());
        }
    }

    public abstract boolean useSsao();
    public abstract @NotNull ITexture2DProgram getAnimationsTexture();
    public abstract @NotNull ShaderStorageBufferObject getIndirectBufferData();
    public abstract @NotNull ShaderStorageBufferObject getPropertiesData();
    public abstract @Nullable JGemsShaderManager getSsaoShader();
    public abstract @NotNull JGemsShaderManager getSsaoBlurring();
    public abstract @NotNull JGemsShaderManager getDeferredColorRendererShader();
    public abstract @NotNull DeferredLightRenderProcessor.DeferredShaders getDeferredLightRendererShaders();
    public abstract @NotNull JGemsShaderManager getDeferredDecalsShader();

    @Override
    public void destroyResources() {
        if (this.getSSAORenderProcessor() != null) {
            this.getSSAORenderProcessor().destroyResources();
        }
        this.getDirectGeometryRenderProcessor().destroyResources();
        this.getIndirectGeometryRenderProcessor().destroyResources();
        this.getDeferredColorRenderProcessor().destroyResources();
        this.getDeferredLightRenderProcessor().destroyResources();

        if (this.getOutGBuffer() != null) {
            this.getOutGBuffer().clearFBO();
        }
        if (this.getOutSSAOBuffer() != null) {
            this.getOutSSAOBuffer().clearFBO();
        }
        if (this.getOutColorBuffer() != null) {
            this.getOutColorBuffer().clearFBO();
        }
        this.destroyProcessorInstances();
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
    public Collection<SceneObject> getRejectedIndirectDeferredRenderingObjects() {
        return this.getIndirectGeometryRenderProcessor().getRejected();
    }

    @Override
    public Collection<SceneObject> getRejectedDirectDeferredRenderingObjects() {
        return this.getDirectGeometryRenderProcessor().getRejected();
    }

    public DeferredLightRenderProcessor getDeferredLightRenderProcessor() {
        return this.deferredLightRenderProcessor;
    }

    public DeferredColorRenderProcessor getDeferredColorRenderProcessor() {
        return this.deferredColorRenderProcessor;
    }

    public SSAORenderProcessor getSSAORenderProcessor() {
        return this.ssaoRenderProcessor;
    }

    public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
        return this.indirectGeometryRenderProcessor;
    }

    public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
        return this.directGeometryRenderProcessor;
    }

    @Override
    public NodeID getNodeID() {
        return JGemsOpenGLRenderer.DEFERRED_RENDER_PASS;
    }
    /*
    public void MillionCubesTest() {
        final Consumer<JGemsShaderManager> uniformsHandler = (mainSceneShaderManager) -> {
            final SceneWorld sceneWorld = (SceneWorld) this.getWorld();
            final ICamera camera = this.getOpenGLRenderer().getCamera();
            final Matrix4f cameraMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
            final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
            final ICubeMapProgram cubeMapProgram = sceneWorld.getEnvironment().getSkyBox().getTexture();

            mainSceneShaderManager.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.CAMERA_POS), UniformFunctions.VEC3F(camera.getCamPosition()));
            if (cubeMapProgram != null && mainSceneShaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP))) {
                mainSceneShaderManager.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP), cubeMapProgram);
            }
            mainSceneShaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), UniformFunctions.MAT4F(projection));
            mainSceneShaderManager.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), UniformFunctions.MAT4F(cameraMatrix));
        };

        IndirectBufferProgram renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        DefaultIndirectCommandsProgram baseIndirectCommandProgram1 = new DefaultIndirectCommandsProgram(renderBuffer);
        baseIndirectCommandProgram1.createBuffer();
        //baseIndirectCommandProgram1.buildCommands(null, null, JGemsResourceManager.globalModelAssets.grassCube, 1_000_000);
        GroupedSceneObjectsIndirectRenderer.IRenderingFunction renderingFunction = IndirectRenderFabric.DEFAULT_FUNC;
        renderingFunction.func(JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect, baseIndirectCommandProgram1, renderBuffer, ArbitraryArguments.pass(uniformsHandler));
        baseIndirectCommandProgram1.destroyBuffer();
    }
     */
}
