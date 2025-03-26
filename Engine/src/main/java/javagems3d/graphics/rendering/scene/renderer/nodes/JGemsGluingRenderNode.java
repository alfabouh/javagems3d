package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abtractions.GluingRenderNode;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

public class JGemsGluingRenderNode extends GluingRenderNode {
    public JGemsGluingRenderNode(@NotNull FBOTexture2DProgram inColorTransparency, @NotNull FBOTexture2DProgram inColorScene, OpenGLRenderer openGLRenderer) {
        super(inColorTransparency, inColorScene, openGLRenderer);
    }

    @Override
    public @NotNull JGemsShaderManager getGluingShader() {
        return JGemsResourceManager.globalShaderAssets.scene_gluing;
    }
}
