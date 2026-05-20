package api.application.workbench.resources.data.wbench.properties;

import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;

public class WBenchRenderProperties extends RenderProperties {
    public WBenchRenderProperties() {
        super(CullingRules.get());
        this.setDefaults();
    }

    public static WBenchRenderProperties getDefault() {
        return new WBenchRenderProperties();
    }

    protected void setDefaults() {
        this.setValueInt(JGemsRenderProperties.KEY_GBUFFER_DECAL_LAYER_ID, 0, 0, 255);
        this.setValueFloat(JGemsRenderProperties.KEY_RENDER_DISTANCE, -1.0f, -1.0f, 1024.0f);
        this.setValueFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, 0.0f, 0.0f, 1.0f);
        this.setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, true);
        this.setValueBool(JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION, true);
    }
}
