package javagems3d.graphics.opengl.rendering.items.settings;

import javagems3d.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("all")
public class ObjectRenderSettings implements IRenderSettings {
    private JGemsShaderManager modelRenderShader;

    private float renderDistance;
    private float alphaDiscardValue;

    private boolean allowMoveMeshesIntoTransparencyPass;
    private boolean lightsAffected;
    private boolean shadowCaster;
    private boolean shadowReceiver;
    private boolean isDefaultBrightLighted;
    private boolean allowMovementInterpolation;
    private boolean disableFaceCulling;

    public ObjectRenderSettings(@NotNull JGemsShaderManager modelRenderShader) {
        this.modelRenderShader = modelRenderShader;

        this.alphaDiscardValue = JGemsSceneGlobalConstants.DEFAULT_ALPHA_DISCARD;
        this.renderDistance = -1.0f;

        this.allowMoveMeshesIntoTransparencyPass = true;
        this.lightsAffected = true;
        this.shadowCaster = true;
        this.shadowReceiver = true;
        this.isDefaultBrightLighted = false;
        this.allowMovementInterpolation = true;
        this.disableFaceCulling = false;
    }

    public JGemsShaderManager getModelRenderShader() {
        return this.modelRenderShader;
    }

    public ObjectRenderSettings setModelRenderShader(JGemsShaderManager modelRenderShader) {
        this.modelRenderShader = modelRenderShader;
        return this;
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public ObjectRenderSettings setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
        return this;
    }

    public float getAlphaDiscardValue() {
        return this.alphaDiscardValue;
    }

    public ObjectRenderSettings setAlphaDiscardValue(float alphaDiscardValue) {
        this.alphaDiscardValue = alphaDiscardValue;
        return this;
    }

    public boolean isAllowedMoveMeshesIntoTransparencyPass() {
        return this.allowMoveMeshesIntoTransparencyPass;
    }

    public ObjectRenderSettings setAllowMoveMeshesIntoTransparencyPass(boolean allowMoveMeshesIntoTransparencyPass) {
        this.allowMoveMeshesIntoTransparencyPass = allowMoveMeshesIntoTransparencyPass;
        return this;
    }

    public boolean isLightsAffected() {
        return this.lightsAffected;
    }

    public ObjectRenderSettings setLightsAffected(boolean lightsAffected) {
        this.lightsAffected = lightsAffected;
        return this;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public ObjectRenderSettings setShadowCaster(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
        return this;
    }

    public boolean isShadowReceiver() {
        return this.shadowReceiver;
    }

    public ObjectRenderSettings setShadowReceiver(boolean shadowReceiver) {
        this.shadowReceiver = shadowReceiver;
        return this;
    }

    public boolean isDefaultBrightLighted() {
        return this.isDefaultBrightLighted;
    }

    public ObjectRenderSettings setDefaultBrightLighted(boolean defaultBrightLighted) {
        isDefaultBrightLighted = defaultBrightLighted;
        return this;
    }

    public boolean isAllowedMovementInterpolation() {
        return this.allowMovementInterpolation;
    }

    public ObjectRenderSettings setAllowMovementInterpolation(boolean allowMovementInterpolation) {
        this.allowMovementInterpolation = allowMovementInterpolation;
        return this;
    }

    public boolean isDisabledFaceCulling() {
        return this.disableFaceCulling;
    }

    public ObjectRenderSettings setDisableFaceCulling(boolean disableFaceCulling) {
        this.disableFaceCulling = disableFaceCulling;
        return this;
    }

    @Override
    public @NotNull ObjectRenderSettings copy() {
        ObjectRenderSettings objectRenderSettings = new ObjectRenderSettings(this.getModelRenderShader());
        objectRenderSettings.setAllowMovementInterpolation(this.isAllowedMovementInterpolation());
        this.setDisableFaceCulling(this.isDisabledFaceCulling());
        this.setLightsAffected(this.isLightsAffected());
        this.setAllowMovementInterpolation(this.isAllowedMovementInterpolation());
        this.setShadowCaster(this.isShadowCaster());
        this.setShadowReceiver(this.isShadowReceiver());
        this.setAllowMoveMeshesIntoTransparencyPass(this.isAllowedMoveMeshesIntoTransparencyPass());
        this.setDefaultBrightLighted(this.isDefaultBrightLighted());
        return objectRenderSettings;
    }
}
