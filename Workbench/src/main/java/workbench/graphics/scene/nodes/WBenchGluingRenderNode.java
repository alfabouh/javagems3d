package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.GluingRenderNode;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;
import workbench.resources.WBenchResourceManager;

public class WBenchGluingRenderNode extends GluingRenderNode {
    public WBenchGluingRenderNode(@NotNull FBOTexture2DProgram inColorTransparency, @NotNull FBOTexture2DProgram inColorScene, OpenGLRenderer openGLRenderer) {
        super(inColorTransparency, inColorScene, openGLRenderer);
    }

    @Override
    public @NotNull JGemsShaderManager getGluingShader() {
        return WBenchResourceManager.localShaderAssets.scene_gluing;
    }
}
