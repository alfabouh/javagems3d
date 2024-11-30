package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.AbstractRenderProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;

public class SkyRenderProcessor extends AbstractRenderProcessor {

    private FBOTexture2DProgram skyBoxBackGroundBuffer;

    public SkyRenderProcessor(int renderOrder, @NotNull OpenGLRenderer openGLRenderer) {
        super(renderOrder, openGLRenderer);
    }

    @Override
    public void createResources() {
        this.skyBoxBackGroundBuffer = new FBOTexture2DProgram(true);
        T2DAttachmentContainer skybox = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA, GL46.GL_RGBA);
        }};
        this.getSkyBoxBackGroundBuffer().createFrameBuffer2DTexture(this.getWindowSize(), skybox, true, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        this.getSkyBoxBackGroundBuffer().clearFBO();
    }

    @Override
    public void onRenderNode(@Nullable FBOTexture2DProgram fboIn) {
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.getSkyBoxBackGroundBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        // TODO
        //JGemsOpenGLRendererOLD.SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getSkyBoxBackgroundRenderSet());
        this.getSkyBoxBackGroundBuffer().unBindFBO();
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public FBOTexture2DProgram getSkyBoxBackGroundBuffer() {
        return this.skyBoxBackGroundBuffer;
    }

    @Override
    public @Nullable FBOTexture2DProgram outFrameBuffer() {
        return this.getSkyBoxBackGroundBuffer();
    }
}
