package api.application.workbench.resources.data.properties;

import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;

public class WBenchRenderProperties extends RenderProperties {
    public WBenchRenderProperties() {
        super(CullingRules.get());
    }

    public static WBenchRenderProperties getDefault() {
        return new WBenchRenderProperties();
    }

    @Override
    protected void setDefaults() {
        this.setValueFloat(JGemsRenderProperties.KEY_RENDER_DISTANCE, -1.0f);
        this.setValueFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, 1.0f);
        this.setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, true);
    }

    @Override
    public WBenchRenderProperties copy() {
        WBenchRenderProperties renderProperties = new WBenchRenderProperties();
        renderProperties.setPropertiesMap(this.copyPropertiesMap());
        return renderProperties;
    }
}
