package api.scripting.coding.env.internal.util.world.render.table.properties;

import api.scripting.coding.env.def.*;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;

@JSCodingClass(binding = "JSStage", description = "Wrapper for Stage enum, providing access to stage type and predefined constants.")
public class JSStage {
    @JSCodingField(description = "Underlying Java Stage object")
    private final Stage stage;

    @JSCodingConstructor(description = "Wraps an existing Stage object", paramNames = {"stage"})
    @JSHideFromDoc
    public JSStage(Stage stage) {
        this.stage = stage;
    }

    @JSCodingField(description = "Forward rendering stage")
    public static final JSStage FORWARD = new JSStage(Stage.FORWARD);

    @JSCodingField(description = "Deferred direct rendering stage")
    public static final JSStage DEFERRED_DIRECT = new JSStage(Stage.DEFERRED_DIRECT);

    @JSCodingField(description = "Deferred indirect rendering stage")
    public static final JSStage DEFERRED_INDIRECT = new JSStage(Stage.DEFERRED_INDIRECT);

    @JSCodingField(description = "Direct shadow rendering stage")
    public static final JSStage SHADOW_DIRECT = new JSStage(Stage.SHADOW_DIRECT);

    @JSCodingField(description = "Indirect shadow rendering stage")
    public static final JSStage SHADOW_INDIRECT = new JSStage(Stage.SHADOW_INDIRECT);

    @JSCodingFunctionOrMethod(description = "Returns the type of this stage (DIRECT or INDIRECT)", paramNames = {})
    public Type getType() {
        return stage.getType();
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java Stage object", paramNames = {})
    public Stage getJavaStage() {
        return stage;
    }
}