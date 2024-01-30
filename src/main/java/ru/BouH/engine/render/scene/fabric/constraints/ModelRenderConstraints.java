package ru.BouH.engine.render.scene.fabric.constraints;

public class ModelRenderConstraints {
    private boolean lightOpaque;
    private boolean shadowCaster;
    private boolean shadowReceiver;

    public ModelRenderConstraints(boolean lightOpaque, boolean shadowCaster, boolean shadowReceiver) {
        this.lightOpaque = lightOpaque;
        this.shadowCaster = shadowCaster;
        this.shadowReceiver = shadowReceiver;
    }

    public static ModelRenderConstraints defaultModelRenderConstraints() {
        return new ModelRenderConstraints(true, true, true);
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

    public boolean isLightOpaque() {
        return this.lightOpaque;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public boolean isShadowReceiver() {
        return this.shadowReceiver;
    }
}
