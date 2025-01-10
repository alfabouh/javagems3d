package javagems3d.graphics.objects.rendering.configuration;

import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("all")
public class RenderAttributes implements IRenderConfiguration {
    private ShadingTable shadingTable;

    private float renderDistance;
    private float alphaDiscardValue;

    private boolean allowMoveMeshesIntoTransparencyPass;
    private boolean lightsAffected;
    private boolean shadowCaster;
    private boolean shadowReceiver;
    private boolean defaultBrightLighted;
    private boolean allowMovementInterpolation;
    private boolean disableFaceCulling;

    public RenderAttributes(@NotNull ShadingTable shadingTable) {
        this.shadingTable = shadingTable;

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

    public RenderAttributes(@NotNull JGemsShaderManager sceneShader, @NotNull ShadingTable.Stage renderingSceneShaderTarget) {
        this(new ShadingTable(sceneShader, renderingSceneShaderTarget));
    }

    public RenderAttributes(@NotNull JGemsShaderManager sceneShader) {
        this(new ShadingTable(sceneShader, ShadingTable.Stage.DEFERRED_INDIRECT));
    }

    public ShadingTable getShadingTable() {
        return this.shadingTable;
    }

    public RenderAttributes setShadingTable(ShadingTable shadingTable) {
        this.shadingTable = shadingTable;
        return this;
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public RenderAttributes setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
        return this;
    }

    public float getAlphaDiscardValue() {
        return this.alphaDiscardValue;
    }

    public RenderAttributes setAlphaDiscardValue(float alphaDiscardValue) {
        this.alphaDiscardValue = alphaDiscardValue;
        return this;
    }

    public boolean isAllowedMoveMeshesIntoTransparencyPass() {
        return this.allowMoveMeshesIntoTransparencyPass;
    }

    public RenderAttributes setAllowMoveMeshesIntoTransparencyPass(boolean allowMoveMeshesIntoTransparencyPass) {
        this.allowMoveMeshesIntoTransparencyPass = allowMoveMeshesIntoTransparencyPass;
        return this;
    }

    public boolean isLightsAffected() {
        return this.lightsAffected;
    }

    public RenderAttributes setLightsAffected(boolean lightsAffected) {
        this.lightsAffected = lightsAffected;
        return this;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public RenderAttributes setShadowCaster(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
        return this;
    }

    public boolean isShadowReceiver() {
        return this.shadowReceiver;
    }

    public RenderAttributes setShadowReceiver(boolean shadowReceiver) {
        this.shadowReceiver = shadowReceiver;
        return this;
    }

    public boolean isDefaultBrightLighted() {
        return this.defaultBrightLighted;
    }

    public RenderAttributes setDefaultBrightLighted(boolean defaultBrightLighted) {
        this.defaultBrightLighted = defaultBrightLighted;
        return this;
    }

    public boolean isAllowedMovementInterpolation() {
        return this.allowMovementInterpolation;
    }

    public RenderAttributes setAllowMovementInterpolation(boolean allowMovementInterpolation) {
        this.allowMovementInterpolation = allowMovementInterpolation;
        return this;
    }

    public boolean isDisabledFaceCulling() {
        return this.disableFaceCulling;
    }

    public RenderAttributes setDisableFaceCulling(boolean disableFaceCulling) {
        this.disableFaceCulling = disableFaceCulling;
        return this;
    }

    @Override
    public @NotNull RenderAttributes copy() {
        RenderAttributes objectRenderingConfiguration = new RenderAttributes(this.getShadingTable());
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
