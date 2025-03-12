package javagems3d.graphics.objects.rendering.configuration;

import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.system.resources.managing.resources.data.ICopyable;

public class RenderProperties implements ICopyable<RenderProperties> {
    private CullingRules cullingRules;
    private float renderDistance;
    private float alphaDiscardValue;
    private boolean allowMoveMeshesIntoTransparencyPass;
    private boolean lightsAffected;
    private boolean shadowCaster;
    private boolean shadowReceiver;
    private boolean defaultBrightLighted;
    private boolean allowMovementInterpolation;
    private boolean disableFaceCulling;

    public RenderProperties(CullingRules cullingRules) {
        this.cullingRules = cullingRules;
    }

    public RenderProperties() {
        this(CullingRules.get());
    }

    public static RenderProperties get() {
        return new RenderProperties();
    }

    public RenderProperties setCullingRules(CullingRules cullingRules) {
        this.cullingRules = cullingRules;
        return this;
    }

    public RenderProperties setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
        return this;
    }

    public RenderProperties setAlphaDiscardValue(float alphaDiscardValue) {
        this.alphaDiscardValue = alphaDiscardValue;
        return this;
    }

    public RenderProperties setAllowMoveMeshesIntoTransparencyPass(boolean allowMoveMeshesIntoTransparencyPass) {
        this.allowMoveMeshesIntoTransparencyPass = allowMoveMeshesIntoTransparencyPass;
        return this;
    }

    public RenderProperties setLightsAffected(boolean lightsAffected) {
        this.lightsAffected = lightsAffected;
        return this;
    }

    public RenderProperties setShadowCaster(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
        return this;
    }

    public RenderProperties setShadowReceiver(boolean shadowReceiver) {
        this.shadowReceiver = shadowReceiver;
        return this;
    }

    public RenderProperties setDefaultBrightLighted(boolean defaultBrightLighted) {
        this.defaultBrightLighted = defaultBrightLighted;
        return this;
    }

    public RenderProperties setAllowMovementInterpolation(boolean allowMovementInterpolation) {
        this.allowMovementInterpolation = allowMovementInterpolation;
        return this;
    }

    public RenderProperties setDisableFaceCulling(boolean disableFaceCulling) {
        this.disableFaceCulling = disableFaceCulling;
        return this;
    }

    public CullingRules getCullingRules() {
        return this.cullingRules;
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public float getAlphaDiscardValue() {
        return this.alphaDiscardValue;
    }

    public boolean isAllowMoveMeshesIntoTransparencyPass() {
        return this.allowMoveMeshesIntoTransparencyPass;
    }

    public boolean isLightsAffected() {
        return this.lightsAffected;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public boolean isShadowReceiver() {
        return this.shadowReceiver;
    }

    public boolean isDefaultBrightLighted() {
        return this.defaultBrightLighted;
    }

    public boolean isAllowMovementInterpolation() {
        return this.allowMovementInterpolation;
    }

    public boolean isDisableFaceCulling() {
        return this.disableFaceCulling;
    }

    public RenderProperties copy() {
        RenderProperties renderProperties = new RenderProperties();
        renderProperties.setCullingRules(this.getCullingRules().copy());
        renderProperties.setRenderDistance(this.getRenderDistance());
        renderProperties.setAllowMovementInterpolation(this.isAllowMovementInterpolation());
        renderProperties.setAlphaDiscardValue(this.getAlphaDiscardValue());
        renderProperties.setLightsAffected(this.isLightsAffected());
        renderProperties.setAllowMoveMeshesIntoTransparencyPass(this.isAllowMoveMeshesIntoTransparencyPass());
        renderProperties.setDefaultBrightLighted(this.isDefaultBrightLighted());
        renderProperties.setShadowCaster(this.isShadowCaster());
        renderProperties.setShadowReceiver(this.isShadowReceiver());
        renderProperties.setDisableFaceCulling(this.isDisableFaceCulling());
        return renderProperties;
    }
}
