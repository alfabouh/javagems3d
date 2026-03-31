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
        this.setValueFloat(JGemsRenderProperties.KEY_RENDER_DISTANCE, -1.0f);
        this.setValueFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, 0.0f);
        this.setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, true);
        this.setValueBool(JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION, true);
    }
}
