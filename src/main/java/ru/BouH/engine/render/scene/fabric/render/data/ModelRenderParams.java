package ru.BouH.engine.render.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;

@SuppressWarnings("all")
public final class ModelRenderParams {
    private boolean lightOpaque;
    private boolean shadowCaster;
    private boolean shadowReceiver;
    private boolean hasTransparency;
    private boolean isBright;
    private Vector2d textureScaling;
    private ShaderManager shaderManager;
    private float renderDistance;

    public ModelRenderParams(boolean shadowCaster, boolean shadowReceiver, boolean hasTransparency, @NotNull ShaderManager shaderManager) {
        this.shadowCaster = shadowCaster;
        this.shadowReceiver = shadowReceiver;
        this.hasTransparency = hasTransparency;
        this.shaderManager = shaderManager;
        this.lightOpaque = true;
        this.isBright = false;
        this.renderDistance = -1.0f;
        this.textureScaling = new Vector2d(1.0d);
    }

    public static ModelRenderParams defaultModelRenderConstraints(@NotNull ShaderManager shaderManager) {
        return new ModelRenderParams(true, true, false, shaderManager);
    }

    public ModelRenderParams invertTextureCoordinates() {
        this.textureScaling.mul(1.0d, -1.0d);
        return this;
    }

    public ModelRenderParams setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
        return this;
    }

    public ModelRenderParams setTextureScaling(Vector2d textureScaling) {
        this.textureScaling = textureScaling;
        return this;
    }

    public ModelRenderParams setLightOpaque(boolean lightOpaque) {
        this.lightOpaque = lightOpaque;
        return this;
    }

    public boolean isBright() {
        return this.isBright;
    }

    public ModelRenderParams setBright(boolean bright) {
        isBright = bright;
        return this;
    }

    public void setShaderManager(@NotNull ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public void setShadowCaster(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
    }

    public void setShadowReceiver(boolean shadowReceiver) {
        this.shadowReceiver = shadowReceiver;
    }

    public boolean isHasTransparency() {
        return this.hasTransparency;
    }

    public boolean isLightOpaque() {
        return this.lightOpaque;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public boolean isShadowReceiver() {
        return this.shadowReceiver;
    }

    public Vector2d getTextureScaling() {
        return new Vector2d(this.textureScaling);
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    @NotNull
    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public ModelRenderParams copy() {
        ModelRenderParams modelRenderParams = new ModelRenderParams(this.isShadowCaster(), this.isShadowReceiver(), this.isHasTransparency(), this.getShaderManager());
        modelRenderParams.setLightOpaque(this.isLightOpaque());
        modelRenderParams.setTextureScaling(this.getTextureScaling());
        modelRenderParams.setBright(this.isBright());
        modelRenderParams.setRenderDistance(this.getRenderDistance());
        return modelRenderParams;
    }
}
