package javagems3d.graphics.objects.rendering.configuration;

import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("all")
public class ObjectRenderConfiguration implements IRenderConfiguration {
    private JGemsShaderManager modelRenderShader;

    private float renderDistance;
    private float alphaDiscardValue;

    private boolean allowMoveMeshesIntoTransparencyPass;
    private boolean lightsAffected;
    private boolean shadowCaster;
    private boolean shadowReceiver;
    private boolean defaultBrightLighted;
    private boolean allowMovementInterpolation;
    private boolean disableFaceCulling;

    public ObjectRenderConfiguration(@NotNull JGemsShaderManager modelRenderShader) {
        this.modelRenderShader = modelRenderShader;

        this.alphaDiscardValue = JGemsRenderingGlobalConstants.DEFAULT_ALPHA_DISCARD;
        this.renderDistance = -1.0f;

        this.allowMoveMeshesIntoTransparencyPass = true;
        this.lightsAffected = true;
        this.shadowCaster = true;
        this.shadowReceiver = true;
        this.defaultBrightLighted = false;
        this.allowMovementInterpolation = true;
        this.disableFaceCulling = false;
    }

    public @NotNull JGemsShaderManager getModelRenderShader() {
        return this.modelRenderShader;
    }

    public ObjectRenderConfiguration setModelRenderShader(JGemsShaderManager modelRenderShader) {
        this.modelRenderShader = modelRenderShader;
        return this;
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public ObjectRenderConfiguration setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
        return this;
    }

    public float getAlphaDiscardValue() {
        return this.alphaDiscardValue;
    }

    public ObjectRenderConfiguration setAlphaDiscardValue(float alphaDiscardValue) {
        this.alphaDiscardValue = alphaDiscardValue;
        return this;
    }

    public boolean isAllowedMoveMeshesIntoTransparencyPass() {
        return this.allowMoveMeshesIntoTransparencyPass;
    }

    public ObjectRenderConfiguration setAllowMoveMeshesIntoTransparencyPass(boolean allowMoveMeshesIntoTransparencyPass) {
        this.allowMoveMeshesIntoTransparencyPass = allowMoveMeshesIntoTransparencyPass;
        return this;
    }

    public boolean isLightsAffected() {
        return this.lightsAffected;
    }

    public ObjectRenderConfiguration setLightsAffected(boolean lightsAffected) {
        this.lightsAffected = lightsAffected;
        return this;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public ObjectRenderConfiguration setShadowCaster(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
        return this;
    }

    public boolean isShadowReceiver() {
        return this.shadowReceiver;
    }

    public ObjectRenderConfiguration setShadowReceiver(boolean shadowReceiver) {
        this.shadowReceiver = shadowReceiver;
        return this;
    }

    public boolean isDefaultBrightLighted() {
        return this.defaultBrightLighted;
    }

    public ObjectRenderConfiguration setDefaultBrightLighted(boolean defaultBrightLighted) {
        this.defaultBrightLighted = defaultBrightLighted;
        return this;
    }

    public boolean isAllowedMovementInterpolation() {
        return this.allowMovementInterpolation;
    }

    public ObjectRenderConfiguration setAllowMovementInterpolation(boolean allowMovementInterpolation) {
        this.allowMovementInterpolation = allowMovementInterpolation;
        return this;
    }

    public boolean isDisabledFaceCulling() {
        return this.disableFaceCulling;
    }

    public ObjectRenderConfiguration setDisableFaceCulling(boolean disableFaceCulling) {
        this.disableFaceCulling = disableFaceCulling;
        return this;
    }

    @Override
    public @NotNull ObjectRenderConfiguration copy() {
        ObjectRenderConfiguration objectRenderingConfiguration = new ObjectRenderConfiguration(this.getModelRenderShader());
        objectRenderingConfiguration.setAllowMovementInterpolation(this.isAllowedMovementInterpolation());
        this.setDisableFaceCulling(this.isDisabledFaceCulling());
        this.setLightsAffected(this.isLightsAffected());
        this.setAllowMovementInterpolation(this.isAllowedMovementInterpolation());
        this.setShadowCaster(this.isShadowCaster());
        this.setShadowReceiver(this.isShadowReceiver());
        this.setAllowMoveMeshesIntoTransparencyPass(this.isAllowedMoveMeshesIntoTransparencyPass());
        this.setDefaultBrightLighted(this.isDefaultBrightLighted());
        return objectRenderingConfiguration;
    }
}
