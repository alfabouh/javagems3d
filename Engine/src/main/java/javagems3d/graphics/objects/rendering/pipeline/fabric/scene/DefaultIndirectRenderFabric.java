package javagems3d.graphics.objects.rendering.pipeline.fabric.scene;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.indirect.GroupedIndirectRenderer;
import javagems3d.help.JGemsRenderingHelper;
import javagems3d.system.service.args.ArbitraryArguments;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class DefaultIndirectRenderFabric extends IndirectRenderFabric {
    public DefaultIndirectRenderFabric(Stage stage, GroupedIndirectRenderer.IRenderingFunction renderingFunction) {
        super(stage, renderingFunction);
    }

    @Override
    public void onFillBufferWithMatrices(Pipeline pipeline, IRendered renderedItem, Matrix4f defaultMatrix, FloatBuffer matrices, ArbitraryArguments metaData) {
        matrices.put(defaultMatrix.get(new float[16]));
    }

    @Override
    public void onFillBufferWithProperties(Pipeline pipeline, IRendered renderedItem, RenderAttributes defaultAttributes, ByteBuffer properties, ArbitraryArguments metaData) {
        properties.putFloat(defaultAttributes.getProperties().getFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD));
        properties.putInt(JGemsRenderingHelper.getLightingCodeForShader(defaultAttributes));
    }

    @Override
    public void createResources(IRendered renderedItem) {

    }

    @Override
    public void destroyResources(IRendered renderedItem) {
    }
}