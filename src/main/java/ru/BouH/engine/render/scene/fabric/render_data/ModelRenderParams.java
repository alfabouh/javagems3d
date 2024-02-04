package ru.BouH.engine.render.scene.fabric.render_data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;

public class ModelRenderParams {
    private boolean lightOpaque;
    private boolean shadowCaster;
    private boolean shadowReceiver;
    private boolean hasTransparency;
    private Vector2d textureScaling;
    private ShaderManager shaderManager;

    public ModelRenderParams(boolean lightOpaque, boolean shadowCaster, boolean shadowReceiver, boolean hasTransparency, @NotNull ShaderManager shaderManager) {
        this.lightOpaque = lightOpaque;
        this.shadowCaster = shadowCaster;
        this.shadowReceiver = shadowReceiver;
        this.hasTransparency = hasTransparency;
        this.shaderManager = shaderManager;
        this.textureScaling = new Vector2d(1.0d);
    }

    public static ModelRenderParams defaultModelRenderConstraints(@NotNull ShaderManager shaderManager) {
        return new ModelRenderParams(true, true, true, false, shaderManager);
    }

    public ModelRenderParams invertTextureCoordinates() {
        this.textureScaling.mul(1.0d, -1.0d);
        return this;
    }

    public ModelRenderParams setTextureScaling(Vector2d textureScaling) {
        this.textureScaling = textureScaling;
        return this;
    }

    public void setShaderManager(@NotNull ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public void setLightOpaque(boolean lightOpaque) {
        this.lightOpaque = lightOpaque;
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

    @NotNull
    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public ModelRenderParams copy() {
        return new ModelRenderParams(this.isLightOpaque(), this.isShadowCaster(), this.isShadowReceiver(), this.isHasTransparency(), this.getShaderManager()).setTextureScaling(this.getTextureScaling());
    }
}
