package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.JGems3D;
import javagems3d.graphics.objects.entities.world.SceneWorldLiquid;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.DeferredRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.DeferredLightRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public class JGemsDeferredRenderNode extends DeferredRenderNode {
    private Collection<SceneWorldLiquid> worldLiquid;

    public JGemsDeferredRenderNode(@NotNull FBOTexture2DProgram startColorFbo, OpenGLRenderer openGLRenderer) {
        super(startColorFbo, openGLRenderer);
        this.worldLiquid = new HashSet<>();
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
    protected void renderInGBuffer() {
        for (SceneWorldLiquid sceneWorldLiquid : this.worldLiquid) {
            if (sceneWorldLiquid.getModel().getMeshStructure().getSolidNodes().isEmpty()) {
                continue;
            }
            this.renderLiquid(sceneWorldLiquid);
        }
    }

    protected void renderLiquid(SceneWorldLiquid sceneWorldLiquid) {
        JGemsShaderManager shaderManager = sceneWorldLiquid.getRenderLiquidData().shaderManager();
        shaderManager.beginShading();
        shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
        shaderManager.performModel3DMatrix(new UniformString(DefaultUniformDefinitions.MODEL_MATRIX), sceneWorldLiquid.getModel());
        shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
        shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.TEXTURE_SCALING), UniformFunctions.VEC2F(sceneWorldLiquid.getTextureScaling()));
        JGemsTransparencyRenderNode.renderMeshList3D_Liquid(this.getOpenGLRenderer(), shaderManager, sceneWorldLiquid.getModel(), MeshStructure3D.SOLID_LAYER);
        shaderManager.endShading();
    }

    public void setWorldLiquid(Collection<SceneWorldLiquid> worldLiquid) {
        this.worldLiquid = worldLiquid;
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
    public @NotNull JGemsShaderManager getDeferredColorRendererShader() {
        return JGemsResourceManager.globalShaderAssets.world_deferred_POST;
    }

    @Override
    public @NotNull DeferredLightRenderProcessor.DeferredShaders getDeferredLightRendererShaders() {
        return new DeferredLightRenderProcessor.DeferredShaders
                (
                    JGemsResourceManager.globalShaderAssets.world_deferred_SUNLIGHT,
                    JGemsResourceManager.globalShaderAssets.world_deferred_POINTLIGHT,
                    JGemsResourceManager.globalShaderAssets.world_deferred_SPOTLIGHT
                );
    }

    @Override
    public @NotNull JGemsShaderManager getDeferredDecalsShader() {
        return JGemsResourceManager.globalShaderAssets.deferred_decals;
    }
}
