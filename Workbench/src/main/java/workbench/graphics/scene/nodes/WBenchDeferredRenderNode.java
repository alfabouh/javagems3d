/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.DeferredRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.DeferredLightRenderProcessor;
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
    public @NotNull JGemsShaderManager getDeferredColorRendererShader() {
        return WBenchResourceManager.localShaderAssets.world_deferred_POST;
    }

    @Override
    public @NotNull DeferredLightRenderProcessor.DeferredShaders getDeferredLightRendererShaders() {
        return new DeferredLightRenderProcessor.DeferredShaders
                (
                        WBenchResourceManager.localShaderAssets.world_deferred_SUNLIGHT,
                        WBenchResourceManager.localShaderAssets.world_deferred_POINTLIGHT,
                        WBenchResourceManager.localShaderAssets.world_deferred_SPOTLIGHT
                );
    }

    @Override
    public @NotNull JGemsShaderManager getDeferredDecalsShader() {
        return WBenchResourceManager.localShaderAssets.deferred_decals;
    }
}
