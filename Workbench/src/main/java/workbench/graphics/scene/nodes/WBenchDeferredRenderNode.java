package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.DeferredRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.resources.WBenchResourceManager;

public class WBenchDeferredRenderNode extends DeferredRenderNode {
    public static boolean enabledTest = false;
    public static int qualityTest = 3;

    public WBenchDeferredRenderNode(@NotNull FBOTexture2DProgram startColorFbo, OpenGLRenderer openGLRenderer) {
        super(startColorFbo, openGLRenderer);
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        JGemsConfig.SYSTEM.USE_SSAO = WBenchDeferredRenderNode.enabledTest;
        this.getSSAORenderProcessor().setQuality(WBenchDeferredRenderNode.qualityTest);
        this.getSSAORenderProcessor().setSsaoBias(this.getWorld().getEnvironment().getLightScene().getSsaoBias());
        this.getSSAORenderProcessor().setSsaoRange(this.getWorld().getEnvironment().getLightScene().getSsaoRange());
        this.getSSAORenderProcessor().setSsaoRadius(this.getWorld().getEnvironment().getLightScene().getSsaoRadius());
        super.onRender(frameTicking);
    }

    @Override
    public boolean useSsao() {
        return true;
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
        return WBenchResourceManager.localShaderAssets.MainScenePropertiesData;
    }

    @Override
    public @Nullable JGemsShaderManager getSsaoShader() {
        return WBenchResourceManager.localShaderAssets.world_ssao;
    }

    @Override
    public @NotNull JGemsShaderManager getSsaoBlurring() {
        return WBenchResourceManager.localShaderAssets.blur_ssao;
    }

    @Override
    public @NotNull JGemsShaderManager getDeferredRendererShader() {
        return WBenchResourceManager.localShaderAssets.world_deferred;
    }

    @Override
    public @NotNull JGemsShaderManager getDeferredDecalsShader() {
        return WBenchResourceManager.localShaderAssets.deferred_decals;
    }
}
