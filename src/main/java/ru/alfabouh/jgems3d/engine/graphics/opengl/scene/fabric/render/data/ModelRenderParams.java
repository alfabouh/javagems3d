package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.JGemsSceneRender;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

@SuppressWarnings("all")
public final class ModelRenderParams {
    private boolean lightOpaque;
    private boolean shadowCaster;
    private boolean hasTransparency;
    private float alphaDiscardValue;
    private boolean isBright;
    private JGemsShaderManager shaderManager;
    private float renderDistance;
    private boolean shouldInterpolateMovement;

    public ModelRenderParams(boolean shadowCaster, boolean hasTransparency, @NotNull JGemsShaderManager shaderManager) {
        this.setShaderManager(shaderManager);
        this.shadowCaster = shadowCaster;
        this.hasTransparency = hasTransparency;
        this.lightOpaque = true;
        this.isBright = false;
        this.renderDistance = -1.0f;
        this.alphaDiscardValue = 0.0f;
        this.shouldInterpolateMovement = true;
    }

    public static ModelRenderParams defaultModelRenderConstraints(@NotNull JGemsShaderManager shaderManager) {
        return new ModelRenderParams(true, false, shaderManager);
    }

    public void setLightOpaque(boolean lightOpaque) {
        this.lightOpaque = lightOpaque;
    }

    public boolean isLightOpaque() {
        return this.lightOpaque;
    }

    public boolean isShouldInterpolateMovement() {
        return this.shouldInterpolateMovement;
    }

    public ModelRenderParams setShouldInterpolateMovement(boolean shouldInterpolateMovement) {
        this.shouldInterpolateMovement = shouldInterpolateMovement;
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
    public JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public void setShaderManager(@NotNull JGemsShaderManager shaderManager) {
        this.shaderManager = shaderManager;
    }

    public ModelRenderParams copy() {
        ModelRenderParams modelRenderParams = new ModelRenderParams(this.isShadowCaster(), this.isHasTransparency(), this.getShaderManager());
        modelRenderParams.setBright(this.isBright());
        modelRenderParams.setRenderDistance(this.getRenderDistance());
        modelRenderParams.setAlphaDiscard(this.getAlphaDiscardValue());
        modelRenderParams.setShouldInterpolateMovement(this.isShouldInterpolateMovement());
        return modelRenderParams;
    }
}
