package ru.BouH.engine.render.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;

@SuppressWarnings("all")
public final class ModelRenderParams {
    private boolean lightOpaque;
    private boolean shadowCaster;
    private boolean shadowReceiver;
    private boolean hasTransparency;
    private float alphaDiscardValue;
    private boolean isBright;
    private Vector2d textureScaling;
    private Vector3d customCullingAABSize;
    private ShaderManager shaderManager;
    private float renderDistance;
    private boolean shouldInterpolateMovement;

    public ModelRenderParams(boolean shadowCaster, boolean shadowReceiver, boolean hasTransparency, @NotNull ShaderManager shaderManager) {
        this.shadowCaster = shadowCaster;
        this.shadowReceiver = shadowReceiver;
        this.hasTransparency = hasTransparency;
        this.shaderManager = shaderManager;
        this.lightOpaque = true;
        this.isBright = false;
        this.renderDistance = -1.0f;
        this.alphaDiscardValue = 0.0f;
        this.textureScaling = new Vector2d(1.0d);
        this.shouldInterpolateMovement = true;
        this.customCullingAABSize = null;
    }

    public static ModelRenderParams defaultModelRenderConstraints(@NotNull ShaderManager shaderManager) {
        return new ModelRenderParams(true, true, false, shaderManager);
    }

    public boolean isShouldInterpolateMovement() {
        return this.shouldInterpolateMovement;
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

    public boolean isLightOpaque() {
        return this.lightOpaque;
    }

    public ModelRenderParams setLightOpaque(boolean lightOpaque) {
        this.lightOpaque = lightOpaque;
        return this;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public ModelRenderParams setShadowCaster(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
        return this;
    }

    public boolean isShadowReceiver() {
        return this.shadowReceiver;
    }

    public ModelRenderParams setShadowReceiver(boolean shadowReceiver) {
        this.shadowReceiver = shadowReceiver;
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
    }

    public ModelRenderParams copy() {
        ModelRenderParams modelRenderParams = new ModelRenderParams(this.isShadowCaster(), this.isShadowReceiver(), this.isHasTransparency(), this.getShaderManager());
        modelRenderParams.setLightOpaque(this.isLightOpaque());
        modelRenderParams.setTextureScaling(this.getTextureScaling());
        modelRenderParams.setBright(this.isBright());
        modelRenderParams.setRenderDistance(this.getRenderDistance());
        modelRenderParams.setAlphaDiscard(this.getAlphaDiscardValue());
        modelRenderParams.setCustomCullingAABSize(this.getCustomCullingAABSize());
        modelRenderParams.setShouldInterpolateMovement(this.isShouldInterpolateMovement());
        return modelRenderParams;
    }
}
