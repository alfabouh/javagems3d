package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.DeferredRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;
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
    protected void createProcessorInstances() {
        super.createProcessorInstances();
        this.getSSAORenderProcessor().setEnabled(JGems3D.get().getGameSettings().ssao.getValue() != 0);
        this.getSSAORenderProcessor().setQuality(JGems3D.get().getGameSettings().ssao.getValue());
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        this.getSSAORenderProcessor().setSsaoBias(this.getWorld().getEnvironment().getLightScene().getSsaoBias());
        this.getSSAORenderProcessor().setSsaoRange(this.getWorld().getEnvironment().getLightScene().getSsaoRange());
        this.getSSAORenderProcessor().setSsaoRadius(this.getWorld().getEnvironment().getLightScene().getSsaoRadius());
        super.onRender(frameTicking);
    }

    @Override
    public @NotNull ITexture2DProgram getAnimationsTexture() {
        return JGemsHelper.resources().getAnimationsTextureBuffer();
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return JGemsResourceManager.globalShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return JGemsResourceManager.globalShaderAssets.MainScenePropertiesData;
    }

    @Override
    public @Nullable JGemsShaderManager getSsaoShader() {
        return JGemsResourceManager.globalShaderAssets.world_ssao;
    }

    @Override
    public @NotNull JGemsShaderManager getSsaoBlurring() {
        return JGemsResourceManager.globalShaderAssets.blur_ssao;
    }

    @Override
    public @NotNull JGemsShaderManager getDeferredRendererShader() {
        return JGemsResourceManager.globalShaderAssets.world_deferred;
    }

    @Override
    public @NotNull JGemsShaderManager getDeferredDecalsShader() {
        return JGemsResourceManager.globalShaderAssets.deferred_decals;
    }
}
