package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.DeferredRenderNode;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.resources.WBenchResourceManager;

public class WBenchDeferredRenderNode extends DeferredRenderNode {
    public WBenchDeferredRenderNode(@NotNull FBOTexture2DProgram startColorFbo, OpenGLRenderer openGLRenderer) {
        super(startColorFbo, openGLRenderer);
    }

    @Override
    public boolean useSsao() {
        return false;
    }

    @Override
    public @NotNull ITexture2DProgram getAnimationsTexture() {
        return WBenchResourceManager.getAnimationsTextureBuffer();
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return WBenchResourceManager.localShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return WBenchResourceManager.localShaderAssets.PropertiesData;
    }

    @Override
    public @Nullable JGemsShaderManager getSsaoShader() {
        return null;
    }

    @Override
    public @NotNull JGemsShaderManager getDeferredRendererShader() {
        return WBenchResourceManager.localShaderAssets.world_deferred;
    }
}
