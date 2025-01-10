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

package javagems3d.graphics.OLD;

public class JGemsOpenGLRendererOLD {
    /*
    public static DearUIInterface inGameInterface;
    public static DearUIInterface inMenuInterface;

    private final SceneData sceneData;
    private final SceneRenderBaseContainer sceneRenderBaseContainer;
    private final DearUIRenderer dearImGuiRender;
    private final Set<FBOTexture2DProgram> fboSet;
    private boolean wantToTakeScreenshot;
    private WorldTransparentRender worldTransparentRender;

    private FBOTexture2DProgram skyBoxBackGroundBuffer;
    private FBOTexture2DProgram bloomBlurredBuffer;
    private FBOTexture2DProgram forwardAndDeferredScenesBuffer;
    private FBOTexture2DProgram gBuffer;
    private FBOTexture2DProgram ssaoBuffer;
    private FBOTexture2DProgram sceneGluingBuffer;
    private FBOTexture2DProgram fxaaBuffer;
    private FBOTexture2DProgram hdrBuffer;
    private FBOTexture2DProgram transparencySceneBuffer;
    private FBOTexture2DProgram finalizingBuffer;

    private TextureProgram ssaoNoiseTexture;
    private TextureProgram ssaoKernelTexture;
    private TextureProgram ssaoBufferTexture;

    private Model<Format2D> screenModel;

    private final JGemsUI jGemsUI;
    private final IndirectRenderBuffer indirectRenderBuffer;

    public JGemsOpenGLRendererOLD(IndirectRenderBuffer indirectRenderBuffer, JGemsUI JGemsUI, Window window, SceneData sceneData) {
        this.jGemsUI = JGemsUI;
        this.indirectRenderBuffer = indirectRenderBuffer;

        JGemsOpenGLRendererOLD.inGameInterface = new DearUIGameInterface();
        JGemsOpenGLRendererOLD.inMenuInterface = new DearUIMenuInterface();

        this.fboSet = new HashSet<>();

        this.sceneData = sceneData;
        this.sceneRenderBaseContainer = new SceneRenderBaseContainer();
        this.createFBOObjects();

        this.createResources(window.getWindowSize());

        this.dearImGuiRender = new DearUIRenderer(window, JGemsResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static JGemsShaderManager getGameUboShader() {
        return JGemsResourceManager.globalShaderAssets.gameUbo;
    }

    public FBOTexture2DProgram getSceneGluingBuffer() {
        return this.sceneGluingBuffer;
    }

    public FBOTexture2DProgram getFxaaBuffer() {
        return this.fxaaBuffer;
    }

    public TextureProgram getSsaoBufferTexture() {
        return this.ssaoBufferTexture;
    }

    public TextureProgram getSsaoKernelTexture() {
        return this.ssaoKernelTexture;
    }

    public TextureProgram getSsaoNoiseTexture() {
        return this.ssaoNoiseTexture;
    }

    public FBOTexture2DProgram getTransparencySceneBuffer() {
        return this.transparencySceneBuffer;
    }

    public FBOTexture2DProgram getHdrBuffer() {
        return this.hdrBuffer;
    }

    public FBOTexture2DProgram getSsaoBuffer() {
        return this.ssaoBuffer;
    }

    public FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    public FBOTexture2DProgram getForwardAndDeferredScenesBuffer() {
        return this.forwardAndDeferredScenesBuffer;
    }

    public FBOTexture2DProgram getBloomBlurredBuffer() {
        return this.bloomBlurredBuffer;
    }

    public FBOTexture2DProgram getFinalizingBuffer() {
        return this.finalizingBuffer;
    }

    public FBOTexture2DProgram getSkyBoxBackGroundBuffer() {
        return this.skyBoxBackGroundBuffer;
    }

    protected WorldTransparentRender getWorldTransparentRender() {
        return this.worldTransparentRender;
    }

    public JGemsShaderManager getBasicOITShader() {
        return JGemsResourceManager.globalShaderAssets.weighted_oit;
    }

    protected void createFBOObjects() {
        this.transparencySceneBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.bloomBlurredBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.forwardAndDeferredScenesBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.gBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.ssaoBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.hdrBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.sceneGluingBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.fxaaBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.finalizingBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.skyBoxBackGroundBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
    }

    private FBOTexture2DProgram addFBOInSet(FBOTexture2DProgram fboTexture2DProgram) {
        this.getFboSet().add(fboTexture2DProgram);
        return fboTexture2DProgram;
    }

    public void recreateResources(Vector2i windowSize) {
        this.destroyResources();
        this.createResources(windowSize);
    }

    @Override
    public void createResources(Vector2i windowSize) {
        this.getShadowScene().createResources();
        this.createSSAOResources(this.getSSAOParams(windowSize));

        T2DAttachmentContainer transparency = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA16F, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_R8, GL46.GL_RED);
            add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA16F, GL46.GL_RGBA);
        }};
        this.transparencySceneBuffer.createFrameBuffer2DTexture(windowSize, transparency, true, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_NONE, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer blur = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.bloomBlurredBuffer.createFrameBuffer2DTexture(windowSize, blur, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_NONE, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer allScene = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
        }};
        this.forwardAndDeferredScenesBuffer.createFrameBuffer2DTexture(windowSize, allScene, true, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer gBuffer = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT3, GL46.GL_RGB, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT4, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.gBuffer.createFrameBuffer2DTexture(new Vector2i(windowSize), gBuffer, true, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer hdr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.hdrBuffer.createFrameBuffer2DTexture(windowSize, hdr, false, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_NONE, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer ssao = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_R16F, GL46.GL_RED);
        }};
        this.ssaoBuffer.createFrameBuffer2DTexture(windowSize, ssao, false, GL46.GL_LINEAR, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer fxaa = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.fxaaBuffer.createFrameBuffer2DTexture(windowSize, fxaa, false, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_NONE, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer gluing = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.sceneGluingBuffer.createFrameBuffer2DTexture(windowSize, gluing, false, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_NONE, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer finalB = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.finalizingBuffer.createFrameBuffer2DTexture(windowSize, finalB, false, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer skybox = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA, GL46.GL_RGBA);
        }};
        this.skyBoxBackGroundBuffer.createFrameBuffer2DTexture(windowSize, skybox, true, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    private void createSSAOResources(Vector3i ssaoParams) {
        if (ssaoParams == null) {
            return;
        }
        this.ssaoKernelTexture = this.calcSSAOKernel(ssaoParams.z * ssaoParams.z);
        this.ssaoNoiseTexture = this.calcSSAONoise(JGemsSceneGlobalConstants.SSAO_NOISE_SIZE * JGemsSceneGlobalConstants.SSAO_NOISE_SIZE);
        this.ssaoBufferTexture = this.createSSAOBuffer(new Vector2i(ssaoParams.x, ssaoParams.y));
    }

    private void destroySsaoTextures() {
        if (this.getSsaoNoiseTexture() != null) {
            this.getSsaoNoiseTexture().clear();
            this.ssaoNoiseTexture = null;
        }
        if (this.getSsaoKernelTexture() != null) {
            this.getSsaoKernelTexture().clear();
            this.ssaoKernelTexture = null;
        }
        if (this.getSsaoBufferTexture() != null) {
            this.getSsaoBufferTexture().clear();
            this.ssaoBufferTexture = null;
        }
    }

    @Override
    public void destroyResources() {
        this.getShadowScene().destroyResources();
        this.destroySsaoTextures();
        this.getFboSet().forEach(FBOTexture2DProgram::clearFBO);
    }

    private void fillScene() {
        this.worldTransparentRender = new WorldTransparentRender(this);

        this.getSceneRenderBaseContainer().addBaseInSkyBoxBackGroundContainer(new SkyBoxBackgroundRender(this.getSceneData().getSceneWorld().getEnvironment().getSkyBox(), this));

        this.getSceneRenderBaseContainer().addBaseInForwardContainer(new WorldForwardRender(this));
        this.getSceneRenderBaseContainer().addBaseInForwardContainer(new SkyBoxCubeMapRender(this.getSceneData().getSceneWorld().getEnvironment().getSkyBox(), this));
        this.getSceneRenderBaseContainer().addBaseInForwardContainer(new DebugRender(this));

        this.getSceneRenderBaseContainer().addBaseInDeferredContainer(new WorldDeferredRender(this));

        this.getSceneRenderBaseContainer().addBaseInTransparencyContainer(new ParticlesRender(this));
        this.getSceneRenderBaseContainer().addBaseInTransparencyContainer(new LiquidsRender(this));
        this.getSceneRenderBaseContainer().addBaseInTransparencyContainer(this.getWorldTransparentRender());

        this.getSceneRenderBaseContainer().addBaseInInventoryForwardContainer(new InventoryRender(this));
        this.getSceneRenderBaseContainer().addBaseInGUIContainer(new GuiRender(this.getImmediateUI(), this));
    }

    @Override
    public void onStartRender() {
        this.screenModel = JGemsSceneUtils.createScreenModel();
        this.fillScene();
        this.getSceneRenderBaseContainer().startAll();
    }

    @Override
    public void onStopRender() {
        this.getDearImGuiRender().clear();
        this.getSceneRenderBaseContainer().endAll();
        this.destroyResources();
        this.screenModel.clear();
    }

    //section HDR
    public void screenBloomHDRCorrection(Model<Format2D> model) {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        this.getHdrBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        JGemsShaderManager hdr = JGemsResourceManager.globalShaderAssets.hdr;
        hdr.beginShading();
        hdr.performUniform(new UniformString("exposure"), UniformFunctions.FLOAT(JGemsSceneGlobalConstants.HDR_EXPOSURE));
        hdr.performUniform(new UniformString("gamma"), UniformFunctions.FLOAT(JGemsSceneGlobalConstants.HDR_GAMMA));
        hdr.performUniform(new UniformString("use_hdr"), UniformFunctions.BOOLEAN(JGemsSceneGlobalConstants.USE_HDR));
        hdr.performUniformTexture(new UniformString("texture_sampler"), this.getSceneGluingBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        hdr.performUniformTexture(new UniformString("bloom_sampler"), this.getBloomBlurredBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        hdr.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
        hdr.endShading();
        this.getHdrBuffer().unBindFBO();
        GL46.glDisable(GL46.GL_BLEND);
    }

    //section Gluing
    public void sceneGluing(Model<Format2D> model) {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        this.getSceneGluingBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        JGemsShaderManager gluing = JGemsResourceManager.globalShaderAssets.scene_gluing;
        gluing.beginShading();
        gluing.performUniformTexture(new UniformString("texture_sampler"), this.getForwardAndDeferredScenesBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        gluing.performUniformTexture(new UniformString("bloom_sampler"), this.getForwardAndDeferredScenesBuffer().getTextureIDByIndex(1), GL46.GL_TEXTURE_2D);

        gluing.performUniformTexture(new UniformString("bloom_sampler2"), this.getTransparencySceneBuffer().getTextureIDByIndex(2), GL46.GL_TEXTURE_2D);
        gluing.performUniformTexture(new UniformString("accumulated_alpha"), this.getTransparencySceneBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        gluing.performUniformTexture(new UniformString("reveal_alpha"), this.getTransparencySceneBuffer().getTextureIDByIndex(1), GL46.GL_TEXTURE_2D);
        gluing.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
        gluing.endShading();
        this.getSceneGluingBuffer().unBindFBO();
        GL46.glDisable(GL46.GL_BLEND);
    }

    //section FXAA
    public void postFXAA(Model<Format2D> model, Vector2i windowSize) {
        JGemsShaderManager fxaaFilter = JGemsResourceManager.globalShaderAssets.fxaa;
        this.getFxaaBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        fxaaFilter.beginShading();
        fxaaFilter.performUniform(new UniformString("use_fxaa"), UniformFunctions.BOOLEAN(JGemsSceneGlobalConstants.USE_FXAA));
        fxaaFilter.performUniform(new UniformString("resolution"), UniformFunctions.VEC2I(windowSize));
        fxaaFilter.performUniformTexture(new UniformString("texture_sampler"), this.getHdrBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        fxaaFilter.performUniform(new UniformString("FXAA_SPAN_MAX"), UniformFunctions.FLOAT((float) Math.pow(JGems3D.get().getGameSettings().fxaa.getValue(), 2)));
        fxaaFilter.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
        fxaaFilter.endShading();
        this.getFxaaBuffer().unBindFBO();
    }

    //section onRender
    @Override
    public void onRender(FrameTicking frameTicking, Vector2i windowSize) {
        try (SpeedProfiler.Section f = SpeedProfiler.getGroup("#onRender").profile("prof1")) {
            JGemsOpenGLRendererOLD.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Misc, new float[]{JGemsHelper.getScreen().getRenderTicks()});
            if (!APIEventsLauncher.pushEvent(new Events.RenderScenePre(frameTicking, windowSize, this)).isCancelled()) {
                if (this.getSceneData().getCamera() == null) {
                    GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
                    SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getGuiRenderSet());
                    this.takeScreenShotIfNeeded(windowSize);
                    this.getDearImGuiRender().onRender(JGemsOpenGLRendererOLD.inMenuInterface, windowSize, frameTicking);
                    return;
                }
                if (JGems3D.get().isPaused()) {
                    GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
                    SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getGuiRenderSet());
                } else {
                    GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_STENCIL_BUFFER_BIT);
                    this.getSceneData().getSceneWorld().getEnvironment().updateEnvironment(this.getSceneData().getSceneWorld(), this.getSceneData().getCamera());
                    JGems3D.get().getScreen().normalizeViewPort();
                    this.renderForwardAndDeferredScenes(frameTicking, windowSize, this.screenModel);

                    try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("transparency")) {
                        this.renderTransparentObjects(frameTicking, windowSize);
                    }
                    try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("gluing")) {
                        this.sceneGluing(this.screenModel);
                    }
                    try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("blur_bloom")) {
                        this.blurBloomBuffer(this.screenModel, windowSize);
                    }
                    try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("hdr")) {
                        this.screenBloomHDRCorrection(this.screenModel);
                    }
                    try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("fxaa")) {
                        this.postFXAA(this.screenModel, windowSize);
                    }
                    try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("post")) {
                        this.postProcessing(frameTicking, windowSize);
                    }
                    try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("final")) {
                        this.renderFinalSceneInMainBuffer(this.screenModel);
                    }

                    SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getInventoryRenderSet());
                    SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getGuiRenderSet());
                }
            }
            APIEventsLauncher.pushEvent(new Events.RenderScenePost(frameTicking, windowSize, this));
            this.takeScreenShotIfNeeded(windowSize);
        }
        this.getDearImGuiRender().onRender(JGemsOpenGLRendererOLD.inGameInterface, windowSize, frameTicking);
    }

    // section Post
    private void postProcessing(FrameTicking frameTicking, Vector2i size) {
        this.getFinalizingBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        if (!APIEventsLauncher.pushEvent(new Events.RenderPostProcessing(frameTicking, size, this.getFxaaBuffer().getTextureIDByIndex(0), this)).isCancelled()) {
            this.getFxaaBuffer().copyFBOtoFBOColor(this.getFinalizingBuffer().getFrameBufferId(), new int[]{GL46.GL_COLOR_ATTACHMENT0}, size);
        }
        this.getFinalizingBuffer().unBindFBO();
    }

    //section FinalRender
    private void renderFinalSceneInMainBuffer(Model<Format2D> model) {
        JGemsShaderManager imgShader = JGemsResourceManager.globalShaderAssets.gui_image;
        imgShader.beginShading();
        imgShader.performUniformTexture(new UniformString("texture_sampler"), this.getFinalizingBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        imgShader.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
        imgShader.endShading();
    }

    //section RenderForwardDeferred
    public void renderForwardAndDeferredScenes(FrameTicking frameTicking, Vector2i windowSize, Model<Format2D> model) {
        try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("g_buffer")) {
            this.deferredGeometry(frameTicking);
        }

        if (this.getSsaoNoiseTexture() != null) {
            try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("ssao_buffer")) {
                GL46.glDisable(GL46.GL_DEPTH_TEST);
                this.calcSSAOValueOnGBuffer(model, windowSize);
                GL46.glEnable(GL46.GL_DEPTH_TEST);
            }
        }

        try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("lights")) {
            this.getForwardAndDeferredScenesBuffer().bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
            this.deferredLighting(model);
            this.getForwardAndDeferredScenesBuffer().unBindFBO();
        }

        this.getGBuffer().copyFBOtoFBODepth(this.getForwardAndDeferredScenesBuffer().getFrameBufferId(), windowSize);

        try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("skybox")) {
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            this.getSkyBoxBackGroundBuffer().bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
            SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getSkyBoxBackgroundRenderSet());
            this.getSkyBoxBackGroundBuffer().unBindFBO();
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        }

        try (SpeedProfiler.Section s = SpeedProfiler.getGroup("Render_Sections").profile("forward")) {
            this.getForwardAndDeferredScenesBuffer().bindFBO();
            this.renderForwardScene(frameTicking);
            this.getForwardAndDeferredScenesBuffer().unBindFBO();
        }
    }

    int staticRenderBufferHandle;
    int staticDrawCount;

    private void test(int ents) {
        int COM_SIZE = 5 * 4;

        int allMeshes = 0;
        for (MeshBuffer meshBuffer : this.getIndirectRenderBuffer().getAllStaticMeshBuffers()) {
            allMeshes += meshBuffer.getPassData().size();
        }

        int firstIdx = 0;
        int baseInstance = 0;
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(allMeshes * COM_SIZE);
        for (MeshBuffer meshBuffer : this.getIndirectRenderBuffer().getAllStaticMeshBuffers()) {
            for (MeshBuffer.PassData data : meshBuffer.getPassData()) {
                commandBuffer.putInt(data.getVertices());
                commandBuffer.putInt(ents);
                commandBuffer.putInt(firstIdx);
                commandBuffer.putInt(data.getOffset());
                commandBuffer.putInt(baseInstance);

                firstIdx += data.getVertices();
                baseInstance += ents;
            }
        }
        commandBuffer.flip();

        staticDrawCount = commandBuffer.remaining() / COM_SIZE;
        staticRenderBufferHandle = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, staticRenderBufferHandle);
        GL46.glBufferData(GL46.GL_DRAW_INDIRECT_BUFFER, commandBuffer, GL46.GL_DYNAMIC_DRAW);

        MemoryUtil.memFree(commandBuffer);
    }

    //section DeferredGeom
    private void deferredGeometry(FrameTicking frameTicking) {
        this.getGBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getDeferredRenderSet());
        JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect.beginShading();
        JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(JGemsHelper.getScreen().getScene().getTransformationUtils().getPerspectiveMatrix()));
        JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(JGemsHelper.getScreen().getScene().getTransformationUtils().getMainCameraViewMatrix()));
        int entityIdx = 0;
        for (AbstractSceneObject a : abstractSceneObjects) {
            JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect.performUniform(new UniformString("modelMatrices", entityIdx++), UniformFunctions.MAT4F(Transformation.getModelMatrix(a.getModel().getFormat())));
        }

        int drawElement = 0;
        for (AbstractSceneObject a : abstractSceneObjects) {
            JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect.performUniform(new UniformString("modelMatrixIdx", entityIdx++), UniformFunctions.INTEGER(drawElement++));
        }

        test(drawElement);

        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, staticRenderBufferHandle);
        GL46.glBindVertexArray(this.getIndirectRenderBuffer().getStaticVao());
        GL46.glMultiDrawElementsIndirect(GL46.GL_TRIANGLES, GL46.GL_UNSIGNED_INT, 0, staticDrawCount, 0);
        GL46.glBindVertexArray(0);

        JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect.endShading();
        JGemsOpenGLRendererOLD.abstractSceneObjects.clear();
        this.getGBuffer().unBindFBO();
    }

    //section DeferredLighting
    private void deferredLighting(Model<Format2D> model) {
        JGemsShaderManager deferredShader = JGemsResourceManager.globalShaderAssets.world_deferred;
        deferredShader.beginShading();
        deferredShader.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(JGemsSceneUtils.getMainCameraViewMatrix()));
        deferredShader.performUniformTexture(new UniformString("gPositions"), this.getGBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gNormals"), this.getGBuffer().getTextureIDByIndex(1), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gTexture"), this.getGBuffer().getTextureIDByIndex(2), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gEmission"), this.getGBuffer().getTextureIDByIndex(3), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gSpecular"), this.getGBuffer().getTextureIDByIndex(4), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("ssaoSampler"), this.getSsaoBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        deferredShader.performUniform(new UniformString("isSsaoValid"), UniformFunctions.BOOLEAN(this.getSsaoBufferTexture() != null));
        deferredShader.getUtils().performShadowsInfo();
        deferredShader.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
        deferredShader.endShading();
    }

    //section Forward
    private void renderForwardScene(FrameTicking frameTicking) {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getForwardRenderSet());
        GL46.glDisable(GL46.GL_BLEND);
    }

    //section Transient
    private void renderTransparentObjects(FrameTicking frameTicking, Vector2i windowSize) {
        this.getForwardAndDeferredScenesBuffer().copyFBOtoFBODepth(this.getTransparencySceneBuffer().getFrameBufferId(), windowSize);
        GL46.glDepthMask(false);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunci(0, GL46.GL_ONE, GL46.GL_ONE);
        GL46.glBlendFunci(1, GL46.GL_ZERO, GL46.GL_ONE_MINUS_SRC_COLOR);
        GL46.glBlendFunci(2, GL46.GL_ONE, GL46.GL_ONE);
        GL46.glBlendEquation(GL46.GL_FUNC_ADD);
        this.getTransparencySceneBuffer().bindFBO();
        GL46.glClearBufferfv(GL46.GL_COLOR, 0, new float[]{0.0f, 0.0f, 0.0f, 0.0f});
        GL46.glClearBufferfv(GL46.GL_COLOR, 1, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        GL46.glClearBufferfv(GL46.GL_COLOR, 2, new float[]{0.0f, 0.0f, 0.0f, 0.0f});
        SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getTransparencyRenderSet());
        this.getTransparencySceneBuffer().unBindFBO();
        GL46.glDisable(GL46.GL_BLEND);
        GL46.glDepthMask(true);
    }

    //section SSAO
    private void calcSSAOValueOnGBuffer(Model<Format2D> model, Vector2i windowSize) {
        if (!JGemsSceneGlobalConstants.USE_SSAO) {
            this.getSsaoBuffer().bindFBO();
            GL46.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            this.getSsaoBuffer().unBindFBO();
            return;
        }
        JGemsShaderManager ssaoComputeShader = JGemsResourceManager.globalShaderAssets.world_ssao;
        ssaoComputeShader.beginComputing();

        ssaoComputeShader.performUniform(new UniformString("ssao_bias"), UniformFunctions.FLOAT(JGemsSceneGlobalConstants.SSAO_BIAS));
        ssaoComputeShader.performUniform(new UniformString("ssao_radius"), UniformFunctions.FLOAT(JGemsSceneGlobalConstants.SSAO_RADIUS));
        ssaoComputeShader.performUniform(new UniformString("ssao_range"), UniformFunctions.FLOAT(JGemsSceneGlobalConstants.SSAO_RANGE));

        ssaoComputeShader.performUniform(new UniformString("noiseScale"), UniformFunctions.VEC2I(new Vector2i(windowSize).div(JGemsSceneGlobalConstants.SSAO_NOISE_SIZE)));
        ssaoComputeShader.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(JGemsSceneUtils.getMainPerspectiveMatrix()));
        ssaoComputeShader.performUniformTexture(new UniformString("gPositions"), this.getGBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture(new UniformString("gNormals"), this.getGBuffer().getTextureIDByIndex(1), GL46.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture(new UniformString("ssaoNoise"), this.getSsaoNoiseTexture().getTextureId(), GL46.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture(new UniformString("ssaoKernel"), this.getSsaoKernelTexture().getTextureId(), GL46.GL_TEXTURE_2D);
        GL46.glBindImageTexture(4, this.getSsaoBufferTexture().getTextureId(), 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_RGBA16F);
        ssaoComputeShader.dispatchComputeShader(windowSize.x / 8, windowSize.y / 8, 1, GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        ssaoComputeShader.endComputing();

        JGemsShaderManager ssaoBlur = JGemsResourceManager.globalShaderAssets.blur_ssao;
        this.getSsaoBuffer().bindFBO();
        ssaoBlur.beginShading();
        ssaoBlur.performUniformTexture(new UniformString("texture_sampler"), this.getSsaoBufferTexture().getTextureId(), GL46.GL_TEXTURE_2D);
        ssaoBlur.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
        ssaoBlur.endShading();
        this.getSsaoBuffer().unBindFBO();
    }

    //section BlurBloom
    private void blurBloomBuffer(Model<Format2D> model, Vector2i windowSize) {
        if (!JGemsSceneGlobalConstants.USE_BLOOM || JGems3D.get().getGameSettings().bloom.getValue() == 0) {
            this.getBloomBlurredBuffer().bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            this.getBloomBlurredBuffer().unBindFBO();
            return;
        }
        JGemsShaderManager blurShader = JGemsResourceManager.globalShaderAssets.blur13;
        FBOTexture2DProgram startFbo = this.getSceneGluingBuffer();
        int startBinding = 1;
        int steps = 6;

        blurShader.beginShading();
        blurShader.performUniform(new UniformString("resolution"), UniformFunctions.VEC2I(new Vector2i(windowSize).div(4.0f)));
        for (int i = 0; i < steps; i++) {
            this.getBloomBlurredBuffer().bindFBO();
            blurShader.performUniformTexture(new UniformString("texture_sampler"), startFbo.getTextureIDByIndex(startBinding), GL46.GL_TEXTURE_2D, 0);
            blurShader.performUniform(new UniformString("direction"), UniformFunctions.VEC2F(i % 2 == 0 ? new Vector2f(1.0f, 0.0f) : new Vector2f(0.0f, 1.0f)));
            blurShader.getUtils().performOrthographicMatrix(model);
            JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
            this.getBloomBlurredBuffer().unBindFBO();
            startFbo = this.getBloomBlurredBuffer();
            startBinding = 0;
        }
        blurShader.endShading();
    }

    private static List<AbstractSceneObject> abstractSceneObjects = new ArrayList<>();
    public void renderModeledSceneObject(AbstractSceneObject sceneObject) {
        if (sceneObject.getModel().getMeshStructure().getMeshDataType() == MeshDataType.INDIRECT_RENDER_DATA) {
            abstractSceneObjects.add(sceneObject);
            return;
        }
        JGemsSceneUtils.renderModeledSceneObject(sceneObject);
    }

    public void renderModeledSceneObject(AbstractSceneObject sceneObject, ICamera camera) {
        JGemsSceneUtils.renderModeledSceneObject(sceneObject, camera);
    }

    public void addModelNodeInTransparencyPass(WorldTransparentRender.RenderNodeInfo node) {
        this.getWorldTransparentRender().addModelNodeInTransparencyPass(node);
    }

    public void addSceneModelObjectInTransparencyPass(AbstractSceneObject modeledSceneObject) {
        this.getWorldTransparentRender().addSceneModelObjectInTransparencyPass(modeledSceneObject);
    }

    private void takeScreenShotIfNeeded(Vector2i windowSize) {
        if (this.wantToTakeScreenshot) {
            JGemsHelper.getLogger().log("Took screenshot!");
            this.writeBufferInFile(windowSize);
            this.wantToTakeScreenshot = false;
        }
    }

    private void remakeScreenModel() {
        if (this.screenModel != null) {
            this.screenModel.clear();
            this.screenModel = JGemsSceneUtils.createScreenModel();
        }
    }

    //section WinResize
    @Override
    public void onWindowResize(Vector2i windowSize) {
        this.remakeScreenModel();
        this.recreateResources(windowSize);
        this.getDearImGuiRender().onResize(windowSize);
    }

    public void takeScreenShot() {
        this.wantToTakeScreenshot = true;
    }

    public JGemsUI getImmediateUI() {
        return this.jGemsUI;
    }

    public IndirectRenderBuffer getIndirectRenderBuffer() {
        return this.indirectRenderBuffer;
    }

    public LightsScene getLightManager() {
        return this.getSceneData().getSceneWorld().getEnvironment().getLightManager();
    }

    public ShadowManager getShadowScene() {
        return this.getSceneData().getSceneWorld().getEnvironment().getShadowManager();
    }

    private Set<FBOTexture2DProgram> getFboSet() {
        return this.fboSet;
    }

    public DearUIRenderer getDearImGuiRender() {
        return this.dearImGuiRender;
    }

    public SceneRenderBaseContainer getSceneRenderBaseContainer() {
        return this.sceneRenderBaseContainer;
    }

    @Override
    public SceneData getSceneData() {
        return this.sceneData;
    }

    private TextureProgram calcSSAOKernel(int size) {
        TextureProgram textureProgram = new TextureProgram();
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(size * 3);
        for (int i = 0; i < size; ++i) {
            float x = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            float y = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            float z = JGems3D.random.nextFloat();

            Vector3f sample = new Vector3f(x, y, z);
            sample.normalize();
            sample.mul(JGems3D.random.nextFloat());

            float scale = (float) i / ((float) size);
            scale = JGemsHelper.MATH.lerp(0.1f, 1.0f, scale * scale);
            sample.mul(scale);

            floatBuffer.put(sample.x);
            floatBuffer.put(sample.y);
            floatBuffer.put(sample.z);
        }
        floatBuffer.flip();
        int s = (int) Math.sqrt(size);
        textureProgram.createTexture(new Vector2i(s), GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_REPEAT, GL46.GL_REPEAT, null, floatBuffer);
        MemoryUtil.memFree(floatBuffer);
        return textureProgram;
    }

    private TextureProgram calcSSAONoise(int size) {
        TextureProgram textureProgram = new TextureProgram();
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(size * 3);
        for (int i = 0; i < size; ++i) {
            float x = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            float y = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            floatBuffer.put(x);
            floatBuffer.put(y);
            floatBuffer.put(0.0f);
        }
        floatBuffer.flip();
        int s = (int) Math.sqrt(size);
        textureProgram.createTexture(new Vector2i(s), GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_REPEAT, GL46.GL_REPEAT, null, floatBuffer);
        MemoryUtil.memFree(floatBuffer);
        return textureProgram;
    }

    private TextureProgram createSSAOBuffer(Vector2i windowSize) {
        TextureProgram textureProgram = new TextureProgram();
        textureProgram.createTexture(windowSize, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_LINEAR, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, null);
        return textureProgram;
    }

    private Vector3i getSSAOParams(Vector2i windowSize) {
        switch (JGems3D.get().getGameSettings().ssao.getValue()) {
            case 1: {
                return new Vector3i((int) (windowSize.x * 1.0f), (int) (windowSize.y * 1.0f), 3);
            }
            case 2: {
                return new Vector3i((int) (windowSize.x * 1.0f), (int) (windowSize.y * 1.0f), 4);
            }
            case 3: {
                return new Vector3i((int) (windowSize.x * 1.0f), (int) (windowSize.y * 1.0f), 6);
            }
            case 0:
            default: {
                return null;
            }
        }
    }

    private void writeBufferInFile(Vector2i windowSize) {
        int w = windowSize.x;
        int h = windowSize.y;
        int i1 = w * h;
        ByteBuffer p = ByteBuffer.allocateDirect(i1 * 4);
        GL46.glReadPixels(0, 0, w, h, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, p);
        try {
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            int[] pArray = new int[i1];
            p.asIntBuffer().get(pArray);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = (x + (w * y)) * 4;
                    int r = p.get(i) & 0xFF;
                    int g = p.get(i + 1) & 0xFF;
                    int b = p.get(i + 2) & 0xFF;
                    int a = p.get(i + 3) & 0xFF;
                    int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                    image.setRGB(x, windowSize.y - y - 1, rgb);
                }
            }
            Path scrPath = Paths.get(JGems3D.getGameFilesFolder() + "/screenshots/");
            if (!Files.exists(scrPath)) {
                Files.createDirectories(scrPath);
            }
            String builder = scrPath + "/screen_" + JGems3D.systemTime() + ".png";
            ImageIO.write(image, "PNG", new File(builder));
        } catch (IOException e) {
            JGemsHelper.getLogger().warn(e.getMessage());
        }
    }

    public static final class SceneRenderBaseContainer {
        private final Set<SceneRenderBase> skyBoxRenderSet;
        private final Set<SceneRenderBase> forwardRenderSet;
        private final Set<SceneRenderBase> deferredRenderSet;
        private final Set<SceneRenderBase> transparencyRenderSet;
        private final Set<SceneRenderBase> guiRenderSet;
        private final Set<SceneRenderBase> inventoryRenderSet;

        public SceneRenderBaseContainer() {
            this.skyBoxRenderSet = new HashSet<>();
            this.forwardRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
            this.deferredRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
            this.transparencyRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
            this.guiRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
            this.inventoryRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
        }

        public static void renderSceneRenderSet(FrameTicking frameTicking, Set<SceneRenderBase> sceneRenderBases) {
            sceneRenderBases.forEach(e -> e.onBaseRender(frameTicking));
        }

        public void endAll() {
            this.getSkyBoxBackgroundRenderSet().forEach(SceneRenderBase::onStopRender);

            this.getForwardRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getDeferredRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getTransparencyRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getGuiRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getInventoryRenderSet().forEach(SceneRenderBase::onStopRender);
        }

        public void startAll() {
            this.getSkyBoxBackgroundRenderSet().forEach(SceneRenderBase::onStartRender);

            this.getForwardRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getDeferredRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getTransparencyRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getGuiRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getInventoryRenderSet().forEach(SceneRenderBase::onStartRender);
        }

        public void addBaseInGUIContainer(SceneRenderBase base) {
            this.getGuiRenderSet().add(base);
        }

        public void addBaseInSkyBoxBackGroundContainer(SceneRenderBase base) {
            this.getSkyBoxBackgroundRenderSet().add(base);
        }

        public void addBaseInInventoryForwardContainer(SceneRenderBase base) {
            this.getInventoryRenderSet().add(base);
        }

        public void addBaseInForwardContainer(SceneRenderBase base) {
            this.getForwardRenderSet().add(base);
        }

        public void addBaseInDeferredContainer(SceneRenderBase base) {
            this.getDeferredRenderSet().add(base);
        }

        public void addBaseInTransparencyContainer(SceneRenderBase base) {
            this.getTransparencyRenderSet().add(base);
        }

        public Set<SceneRenderBase> getSkyBoxBackgroundRenderSet() {
            return this.skyBoxRenderSet;
        }

        public Set<SceneRenderBase> getInventoryRenderSet() {
            return this.inventoryRenderSet;
        }

        public Set<SceneRenderBase> getGuiRenderSet() {
            return this.guiRenderSet;
        }

        public Set<SceneRenderBase> getTransparencyRenderSet() {
            return this.transparencyRenderSet;
        }

        public Set<SceneRenderBase> getDeferredRenderSet() {
            return this.deferredRenderSet;
        }

        public Set<SceneRenderBase> getForwardRenderSet() {
            return this.forwardRenderSet;
        }
    }

     */
}