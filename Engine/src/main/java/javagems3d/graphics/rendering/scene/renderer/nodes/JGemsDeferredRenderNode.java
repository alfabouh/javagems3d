package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.DeferredRenderNode;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JGemsDeferredRenderNode extends DeferredRenderNode {
    public JGemsDeferredRenderNode(@NotNull FBOTexture2DProgram startColorFbo, OpenGLRenderer openGLRenderer) {
        super(startColorFbo, openGLRenderer);
    }

    @Override
    public boolean useSsao() {
        return true;
    }

    @Override
    public @NotNull ITexture2DProgram getAnimationsTexture() {
        return JGemsHelper.resources().getAnimationsTextureBuffer();
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return JGemsResourceManager.globalShaderAssets.IndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return JGemsResourceManager.globalShaderAssets.PropertiesData;
    }

    @Override
    public @Nullable JGemsShaderManager getSsaoShader() {
        return JGemsResourceManager.globalShaderAssets.world_ssao;
    }

    @Override
    public @NotNull JGemsShaderManager getDeferredRendererShader() {
        return JGemsResourceManager.globalShaderAssets.world_deferred;
    }
}
