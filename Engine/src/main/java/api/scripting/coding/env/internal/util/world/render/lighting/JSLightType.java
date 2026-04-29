package api.scripting.coding.env.internal.util.world.render.lighting;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.graphics.environment.lights.LightType;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSLightType", description = "Wrapper for LightType enum. Allows checking and using light types.")
public class JSLightType {
    @JSCodingField(description = "Real java LightType object")
    private final LightType type;

    @JSCodingConstructor(description = "Wrap existing LightType enum value", paramNames = {"type"})
    public JSLightType(@NotNull LightType type) {
        this.type = type;
    }

    @JSCodingFunctionOrMethod(description = "Check if light type is POINT")
    public boolean isPoint() {
        return this.type == LightType.POINT;
    }

    @JSCodingFunctionOrMethod(description = "Check if light type is SUN")
    public boolean isSun() {
        return this.type == LightType.SUN;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying LightType object (for internal use)")
    public LightType getJavaLightType() {
        return this.type;
    }

    @JSCodingField(description = "POINT light type")
    public static final JSLightType POINT = new JSLightType(LightType.POINT);

    @JSCodingField(description = "SUN light type")
    public static final JSLightType SUN = new JSLightType(LightType.SUN);
}