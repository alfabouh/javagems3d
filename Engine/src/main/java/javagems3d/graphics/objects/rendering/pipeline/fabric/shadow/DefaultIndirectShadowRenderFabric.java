package javagems3d.graphics.objects.rendering.pipeline.fabric.shadow;

import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultIndirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.indirect.scene_objects.GroupedSceneObjectsIndirectRenderer;

public class DefaultIndirectShadowRenderFabric extends DefaultIndirectRenderFabric {
    public DefaultIndirectShadowRenderFabric(GroupedSceneObjectsIndirectRenderer.IRenderingFunction renderingFunction) {
        super(Stage.SHADOW_INDIRECT, renderingFunction);
    }
}
