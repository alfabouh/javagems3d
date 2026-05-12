package api.scripting.coding.env.internal.util.world.render.table.properties;

import api.scripting.coding.env.def.*;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;

@JSCodingClass(binding = "JSPipeline", description = "Enumeration of rendering pipelines, exposing Java Pipeline enum to scripts.")
public class JSPipeline {

    @JSCodingField(description = "Underlying Java Pipeline enum")
    private final Pipeline pipeline;

    @JSHideFromDoc
    @JSCodingConstructor(description = "Wraps an existing Java Pipeline enum", paramNames = {"pipeline"})
    public JSPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java Pipeline enum", paramNames = {})
    public Pipeline getJavaPipeline() {
        return this.pipeline;
    }

    @JSCodingField(description = "Pipeline for main scene rendering")
    public static final JSPipeline SCENE = new JSPipeline(Pipeline.SOLID_SCENE);

    @JSCodingField(description = "Pipeline for background rendering")
    public static final JSPipeline BACKGROUND = new JSPipeline(Pipeline.BACKGROUND);

    @JSCodingField(description = "Pipeline for point light shadow map rendering")
    public static final JSPipeline POINT_LIGHT_SHADOW_MAP = new JSPipeline(Pipeline.POINT_LIGHT_SHADOW_MAP);

    @JSCodingField(description = "Pipeline for sun light shadow map rendering")
    public static final JSPipeline SUN_LIGHT_SHADOW_MAP = new JSPipeline(Pipeline.SUN_LIGHT_SHADOW_MAP);

    @JSCodingField(description = "Pipeline for transparency rendering")
    public static final JSPipeline TRANSPARENCY = new JSPipeline(Pipeline.TRANSPARENCY);

    @JSCodingField(description = "Pipeline representing no rendering")
    public static final JSPipeline NONE = new JSPipeline(Pipeline.NONE);

    @JSCodingFunctionOrMethod(description = "Get the name of the pipeline", paramNames = {})
    @Override
    public String toString() {
        return this.pipeline.name();
    }
}