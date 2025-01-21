package javagems3d.graphics.objects.rendering.pipeline.fabric.shadow;

import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultIndirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;

public class DefaultIndirectShadowRenderFabric extends DefaultIndirectRenderFabric {
    public DefaultIndirectShadowRenderFabric(IndirectObjectsRenderer.IRenderingFunction renderingFunction) {
        super(Stage.SHADOW_INDIRECT, renderingFunction);
    }
}
