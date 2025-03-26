package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abtractions.GluingRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abtractions.PostFXRenderNode;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

public class JGemsPostFXRenderNode extends PostFXRenderNode {
    public JGemsPostFXRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(inColor, openGLRenderer);
    }

    @Override
    public @NotNull JGemsShaderManager getBlurringShader() {
        return JGemsResourceManager.globalShaderAssets.blur13;
    }

    @Override
    public @NotNull JGemsShaderManager getHDRShader() {
        return JGemsResourceManager.globalShaderAssets.hdr;
    }

    @Override
    public @NotNull JGemsShaderManager getFXAAShader() {
        return JGemsResourceManager.globalShaderAssets.fxaa;
    }
}
