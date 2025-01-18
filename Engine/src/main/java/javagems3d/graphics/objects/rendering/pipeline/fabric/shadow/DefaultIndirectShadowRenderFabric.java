package javagems3d.graphics.objects.rendering.pipeline.fabric.shadow;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultIndirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.system.service.args.ArbitraryArguments;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class DefaultIndirectShadowRenderFabric extends DefaultIndirectRenderFabric {
    public DefaultIndirectShadowRenderFabric(IndirectObjectsRenderer.RenderingFunction renderingFunction) {
        super(Stage.SHADOW_INDIRECT, renderingFunction);
    }
}
