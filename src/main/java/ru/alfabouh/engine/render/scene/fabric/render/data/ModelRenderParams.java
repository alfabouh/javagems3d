package ru.alfabouh.engine.render.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.render.scene.SceneRender;

@SuppressWarnings("all")
public final class ModelRenderParams {
    private boolean lightOpaque;
    private boolean shadowCaster;
    private boolean passShadowsInfoInRender;
    private boolean hasTransparency;
    private float alphaDiscardValue;
    private boolean isBright;
    private Vector2d textureScaling;
    private Vector3d customCullingAABSize;
    private ShaderManager shaderManager;
    private float renderDistance;
    private boolean shouldInterpolateMovement;
    private SceneRender.RenderPass renderPass;

    public ModelRenderParams(boolean shadowCaster, boolean passShadowsInfoInRender, boolean hasTransparency, @NotNull ShaderManager shaderManager) {
        this.setShaderManager(shaderManager);

        this.shadowCaster = shadowCaster;
        this.passShadowsInfoInRender = passShadowsInfoInRender;
        this.hasTransparency = hasTransparency;
        this.lightOpaque = true;
        this.isBright = false;
        this.renderDistance = -1.0f;
        this.alphaDiscardValue = 0.0f;
        this.textureScaling = new Vector2d(1.0d);
        this.shouldInterpolateMovement = true;
        this.customCullingAABSize = null;
    }

    private void setForwardRenderPass() {
        this.setRenderPass(SceneRender.RenderPass.FORWARD);
    }

    private void setDeferredRenderPass() {
        this.setRenderPass(SceneRender.RenderPass.DEFERRED);
    }

    public SceneRender.RenderPass getRenderPassToRenderIn() {
        return this.renderPass;
    }

    public static ModelRenderParams defaultModelRenderConstraints(@NotNull ShaderManager shaderManager) {
        return new ModelRenderParams(true, false, false, shaderManager);
    }

    public boolean isShouldInterpolateMovement() {
        return this.shouldInterpolateMovement;
    }

    public ModelRenderParams setRenderPass(SceneRender.RenderPass renderPass) {
        this.renderPass = renderPass;
        return this;
    }

    public ModelRenderParams setShouldInterpolateMovement(boolean shouldInterpolateMovement) {
        this.shouldInterpolateMovement = shouldInterpolateMovement;
        return this;
    }

    public Vector3d getCustomCullingAABSize() {
        return this.customCullingAABSize;
    }

    public ModelRenderParams setCustomCullingAABSize(Vector3d customCullingAABSize) {
        this.customCullingAABSize = customCullingAABSize;
        return this;
    }

    public ModelRenderParams invertTextureCoordinates() {
        this.textureScaling.mul(1.0d, -1.0d);
        return this;
    }

    public boolean isBright() {
        return this.isBright;
    }

    public ModelRenderParams setBright(boolean bright) {
        isBright = bright;
        return this;
    }

    public boolean isHasTransparency() {
        return this.hasTransparency;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public ModelRenderParams setShadowCaster(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
        return this;
    }

    public boolean isPassShadowsInfoInRender() {
        return this.passShadowsInfoInRender;
    }

    public ModelRenderParams setPassShadowsInfoInRender(boolean passShadowsInfoInRender) {
        this.passShadowsInfoInRender = passShadowsInfoInRender;
        return this;
    }

    public Vector2d getTextureScaling() {
        return new Vector2d(this.textureScaling);
    }

    public ModelRenderParams setTextureScaling(Vector2d textureScaling) {
        this.textureScaling = textureScaling;
        return this;
    }

    public float getAlphaDiscardValue() {
        return this.alphaDiscardValue;
    }

    public ModelRenderParams setAlphaDiscard(float f) {
        this.alphaDiscardValue = f;
        return this;
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public ModelRenderParams setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
        return this;
    }

    @NotNull
    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public void setShaderManager(@NotNull ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        if (this.getShaderManager().isUseForGBuffer()) {
            this.setDeferredRenderPass();
        } else {
            this.setForwardRenderPass();
        }
    }

    public ModelRenderParams copy() {
        ModelRenderParams modelRenderParams = new ModelRenderParams(this.isShadowCaster(), this.isPassShadowsInfoInRender(), this.isHasTransparency(), this.getShaderManager());
        modelRenderParams.setTextureScaling(this.getTextureScaling());
        modelRenderParams.setBright(this.isBright());
        modelRenderParams.setRenderDistance(this.getRenderDistance());
        modelRenderParams.setAlphaDiscard(this.getAlphaDiscardValue());
        modelRenderParams.setCustomCullingAABSize(this.getCustomCullingAABSize());
        modelRenderParams.setShouldInterpolateMovement(this.isShouldInterpolateMovement());
        modelRenderParams.setRenderPass(this.getRenderPassToRenderIn());
        return modelRenderParams;
    }
}
