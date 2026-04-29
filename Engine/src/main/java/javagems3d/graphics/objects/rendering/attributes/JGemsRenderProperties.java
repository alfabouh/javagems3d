package javagems3d.graphics.objects.rendering.attributes;

import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;

public class JGemsRenderProperties extends RenderProperties {
    public static final String KEY_RENDER_DISTANCE = "KEY_RENDER_DISTANCE";
    public static final String KEY_ALPHA_DISCARD = "KEY_ALPHA_DISCARD";
    public static final String KEY_SHADOW_CASTER = "KEY_SHADOW_CASTER";
    public static final String KEY_ALLOW_MOVEMENT_INTERPOLATION = "KEY_ALLOW_MOVEMENT_INTERPOLATION";

    public JGemsRenderProperties() {
        super(CullingRules.get());
        this.setDefaults();
    }

    public static JGemsRenderProperties getDefault() {
        return new JGemsRenderProperties();
    }

    protected void setDefaults() {
        this.setValueFloat(JGemsRenderProperties.KEY_RENDER_DISTANCE, -1.0f);
        this.setValueFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, 0.0f);
        this.setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, true);
        this.setValueBool(JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION, true);
    }
}
