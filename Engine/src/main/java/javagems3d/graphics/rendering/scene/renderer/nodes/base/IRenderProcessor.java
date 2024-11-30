package javagems3d.graphics.rendering.scene.renderer.nodes.base;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IRenderProcessor extends IWindow.ResizeCallback {
    void createResources();
    void destroyResources();
    void onRenderNode(@Nullable FBOTexture2DProgram fboIn);

    int getNodeOrder();

    @NotNull OpenGLRenderer getOpenGLRenderer();
    @Nullable FBOTexture2DProgram outFrameBuffer();
}