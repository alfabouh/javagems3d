package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abtractions.GluingRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abtractions.TransparencyRenderNode;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

public class JGemsTransparencyRenderNode extends TransparencyRenderNode {
    public JGemsTransparencyRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(inColor, openGLRenderer);
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return JGemsResourceManager.globalShaderAssets.IndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return JGemsResourceManager.globalShaderAssets.PropertiesData;
    }
}
